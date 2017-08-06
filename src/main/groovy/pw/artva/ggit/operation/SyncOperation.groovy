/*
 * Copyright (c) 2017 Artur Vakhrameev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package pw.artva.ggit.operation

import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.Project
import pw.artva.ggit.core.GGit
import pw.artva.ggit.core.GitConfig
import pw.artva.ggit.core.GitRepository

/**
 * @author Artur Vakhrameev
 */
class SyncOperation extends AbstractOperation {

    private final OperationFactory factory = OperationFactory.instance
    private final Project mainProject = GGit.instance.project

    SyncOperation(GitConfig gitConfig, boolean chain) {
        super(gitConfig, chain)
    }

    @Override
    void execute() {
        if (chain) {
            recursiveSync(gitConfig)
        } else {
            sync(gitConfig)
        }
    }

    void recursiveSync(GitConfig config) {
        sync(config)
        config.subModules.each {
            recursiveSync(config)
        }
    }

    void sync(GitConfig config) {
        //get project dir by name
        def project = mainProject.findProject(config.name)
        def path = project.projectDir

        if (path.exists() && path.list() != null) {
            //project directory exist and not empty
            syncSingleRepo(path, config)
        } else {
            assert allowRepoClone: "Repository for project '${config.name}' not exists and cloning not allowed."
            factory.create(OperationType.CLONE, config, false).execute()
        }
    }

    void syncSingleRepo(File path, GitConfig config) {
        GitRepository repoConfig = config.repository
        //get repository instance
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder()
        File gitDir = new File("${path.path}/.git")
        Repository repo = repositoryBuilder.setGitDir(gitDir).build()

        assert repo.getObjectDatabase().exists() && repo.findRef('HEAD') != null:
                "Invalid git repository in project '${config.name}'"

        //remote
        def remoteUrl = repo.config.getString('remote', repoConfig.remote, 'url')
        if (remoteUrl == null) {
            assert allowRemoteAdd: ""
            repo.config.setString('remote', repoConfig.remote, 'url', repoConfig.remoteUrl)
        } else if (remoteUrl != repoConfig) {
            assert allowUrlRewrite: ""
            repo.config.setString('remote', repoConfig.remote, 'url', repoConfig.remoteUrl)
        }

        //branch
        if (repo.branch != repoConfig.branch) {
            assert allowCheckout: "Repository for project ${config.name} " +
                    "is currentyly on branch '${repo.branch}' and checkout not allowed."
            factory.create(OperationType.CHECKOUT, config, false).execute()
        }

        //pull
        factory.create(OperationType.PULL, config, false).execute()
    }
}
