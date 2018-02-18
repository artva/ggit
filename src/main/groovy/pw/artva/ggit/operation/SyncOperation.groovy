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

import groovy.transform.InheritConstructors
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.Project
import pw.artva.ggit.config.RepositoryConfig
import pw.artva.ggit.operation.base.AbstractOperation

import javax.inject.Inject

/**
 * @author Artur Vakhrameev
 */
class SyncOperation extends AbstractOperation {

    @Inject
    SyncOperation(RepositoryConfig config, Project project) {
        super(config, project)
    }

    @Override
    void execute() {
        def path = config.path
        def pathExists = path.exists() && path.list() != null

        if (!pathExists) {
            assert ggit.cloneIfNotExists:
                    "Repository '${config.name}' does not exist and cloning not allowed."
            new CloneOperation(config, project).run()
        }

        //get config instance
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder()
        File gitDir = new File(path, '.git')
        Repository gitRepo = repositoryBuilder.setGitDir(gitDir).build()

        assert gitRepo.getObjectDatabase().exists() && gitRepo.findRef('HEAD') != null:
                "Invalid git config '${config.name}'"

        //setup remote
        def remoteUrl = gitRepo.config.getString('remote', config.remote, 'url')
        if (remoteUrl == null) {
            assert ggit.remoteAddIfNotExists: ""
            gitRepo.config.setString('remote', config.remote, 'url', config.remoteUrl)
        } else if (remoteUrl != config.remoteUrl) {
            assert ggit.remoteUrlRewrite: ""
            gitRepo.config.setString('remote', config.remote, 'url', config.remoteUrl)
        }

        //setup branch
        if (gitRepo.branch != config.branch) {
            assert ggit.allowCheckout: "Repository ${config.name} " +
                    "is currentyly on branch '${gitRepo.branch}' and checkout not allowed."
            new CheckoutOperation(config, project).execute()
        }

        //pull
        new PullOperation(config, project).execute()
    }

    @Override
    protected void logComplete() {
        log.info "${config.name}: pull operation complete successfully"
    }
}
