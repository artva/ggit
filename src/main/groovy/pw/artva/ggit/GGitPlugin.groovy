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

package pw.artva.ggit

import org.gradle.api.Plugin
import org.gradle.api.Project
import pw.artva.ggit.core.GitConfig
import pw.artva.ggit.tasks.GitSyncTask

/**
 * Plugin implementation class.
 *
 * @author Artur Vakhrameev
 */
class GGitPlugin implements Plugin<Project> {

    Project project

    void apply(final Project project) {
        this.project = project
        initConfig()
        addTasks()
    }

    def initConfig() {
        project.extensions.add(GitConfig.EXTENSION_NAME, GitConfig)
        def config = project.gitConfig
        config.project(project)
    }

    def addTasks() {
        project.task(GitSyncTask.SYNC_TASK_NAME, type: GitSyncTask) {
            gitConfig = project.gitConfig
        }
    }
}
