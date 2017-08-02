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
import pw.artva.ggit.exception.RepoNotExistException

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

    }

    void sync(GitConfig config) {
        //get project dir by name
        def project = mainProject.findProject(config.name)
        def path = project.projectDir

        if (path.exists() && path.list() != null) {
            //project directory exist and not empty
            syncRepo(path, config.repository)
        } else if (mainProject.ggit.cloneIfNotExists) {

            factory.create(OperationType.CLONE, config, false).execute()
        } else {
            throw new RepoNotExistException()
        }
    }

    void syncRepo(File path, GitRepository repository) {

        //get repository instance
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder()
        File gitDir = new File("${path.path}/.git")
        Repository repo = repositoryBuilder.setGitDir(gitDir).build()

    }
}
