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

package pw.artva.ggit.core

import org.gradle.api.Project
import pw.artva.ggit.core.PluginConfiguration
import pw.artva.ggit.operation.OperationFactory
import pw.artva.ggit.tasks.GitSyncTask

/**
 * Main plugin class.
 *
 * @author Artur Vakhrameev
 */
@Singleton
final class GGit {
    private Project project
    private boolean initialized

    void init(Project project) {
        if (!initialized) {
            this.project = project
            initConfig()
            addTasks()
            initialized = true
        } else {
            throw new IllegalStateException("GGit instance has been already initialized.")
        }
    }

    def initConfig() {
        //setup extension
        project.extensions.add(PluginConfiguration.EXTENSION_NAME, PluginConfiguration)
    }

    def addTasks() {
        project.task(GitSyncTask.SYNC_TASK_NAME, type: GitSyncTask) {
            gitConfig = project.gitConfig
        }
    }

    Project getProject() {
        return project
    }
}
