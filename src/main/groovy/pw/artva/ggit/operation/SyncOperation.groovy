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
import org.gradle.api.Project
import pw.artva.ggit.core.GGit
import pw.artva.ggit.core.GitConfig
import pw.artva.ggit.core.GitRepository
import pw.artva.ggit.core.SyncConfig

/**
 * @author Artur Vakhrameev
 */
class SyncOperation extends AbstractOperation {

    private final Project mainProject = GGit.instance.project
    private SyncConfig syncConfig = mainProject.ggit.sync

    SyncOperation(GitConfig gitConfig) {
        super(gitConfig)
    }

    @Override
    void execute() {
        sync(gitConfig)
    }

    void sync(GitConfig config) {
        def dir = config.repository.dir

        if (dir.exists() && dir.list() != null) {
            //project directory exist and not empty
            syncSingleRepo(dir, config)
        } else {
            assert syncConfig.allowRepoClone: "There isn't repository in '${dir.path}' " +
                    "and cloning not allowed."
            new CloneOperation(config).execute()
        }
    }

    void syncSingleRepo(File dir, GitConfig config) {
        Repository repo = GitUtils.getRepository(dir)
        setupRemote(repo, config)
        setupBranch(repo, config)
        new PullOperation(config).execute()
    }

    void setupRemote(Repository repo, GitConfig config) {
        GitRepository repoConfig = config.repository
        //remote
        def remoteUrl = repo.config.getString('remote', repoConfig.remote, 'url')
        if (remoteUrl == null) {
            assert syncConfig.allowRemoteAdd: "Remote ${repoConfig.remote} not exists and " +
                    "remote adding not allowed."
            repo.config.setString('remote', repoConfig.remote, 'url', repoConfig.remoteUrl)
        } else if (remoteUrl != repoConfig.remoteUrl) {
            assert syncConfig.allowUrlRewrite: "Remote ${repoConfig.remote} is currently configured for " +
                    "url: '${remoteUrl}' and url rewritting not allowed."
            repo.config.setString('remote', repoConfig.remote, 'url', repoConfig.remoteUrl)
        }
    }

    void setupBranch(Repository repo, GitConfig config) {
        //branch
        if (repo.branch != config.repository.branch) {
            assert syncConfig.allowCheckout: "Repository for project ${config.name} " +
                    "is currentyly on branch '${repo.branch}' and checkout not allowed."
            new CheckoutOperation(config).execute()
        }
    }
}
