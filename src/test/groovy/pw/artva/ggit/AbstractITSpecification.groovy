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

import org.eclipse.jgit.api.Git
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import pw.artva.ggit.core.GGit
import spock.lang.Specification

/**
 * @author Artur Vakhrameev
 */
abstract class AbstractITSpecification extends Specification {
    @Rule
    final TemporaryFolder testProjDir = new TemporaryFolder()

    Project project

    def setup() {
        def projectName = 'testProject'
        project = ProjectBuilder.builder()
                .withProjectDir(testProjDir)
                .withName(projectName)
                .build()

        project.pluginManager.apply PluginInitializer

        project.ggit {
            gitConfig {
                repository {
                    branch = 'master'
                    remote = 'origin'
                }
                auth {
                    username = 'artva'
                    password = '12345'
                }
                subModules {
                    testSubProject {
                        remoteUrl = getTestProjDir().newFolder('testSubProject').toPath()
                    }
                }
            }
        }
    }

    Git newRepository(String name) {
        File dir = testProjDir.newFolder(name)
        return Git.init()
                .setDirectory(dir)
                .call()
    }

    Git newRepository(String name, Closure closure) {
        def result = newRepository(name)
        closure.delegate = result
        closure()
    }
}
