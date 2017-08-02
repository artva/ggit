import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import pw.artva.ggit.PluginInitializer
import pw.artva.ggit.core.GitConfig
import spock.lang.Specification

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

/**
 * @author Artur Vakhrameev
 */
class GGPluginTest extends Specification {

    @Rule
    final TemporaryFolder testProjDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjDir.newFile('build.gradle')
    }

    def "Ggit extension is filling correctly from build file"() {
        setup:
        def project = ProjectBuilder.builder()
                .withProjectDir(testProjDir.root)
                .build()

        when: 'plugin applied'
        project.pluginManager.apply PluginInitializer

        then: 'extension defined'
        assert project.ggit != null

        when: 'plugin configured'
        //building subproject
        def subProject = ProjectBuilder.builder()
                .withName('subProj')
                .withParent(project)
                .build()

        project.ggit {
            gitConfig {
                repository {
                    branch = branchParam
                    remote = remoteParam
                }
                auth {
                    username = usernameParam
                    password = passwordParam
                }
                subModules {
                    subProj {
                        repository {
                            branch = subBranchParam
                            remote = subRemoteParam
                        }
                        auth {
                            password = subPasswordParam
                        }
                    }
                }
            }
        }
        GitConfig result = project?.ggit?.gitConfig
        GitConfig subModule = result?.subModules?.first()

        then: 'plugin configuration stores all user settings'
        assert result
        assert result?.auth?.username == usernameParam
        assert result?.auth?.password == passwordParam
        assert result?.repository?.branch == branchParam
        assert result?.repository?.remote == remoteParam

        assert subModule
        assert subModule?.auth?.username == subUsernameParam
        assert subModule?.auth?.password == subPasswordParam

        where:
        usernameParam << ['artva']
        passwordParam << ['12345']
        remoteParam << ['remote param']
        branchParam << ['branch param']
        subUsernameParam << ['sub artva']
        subPasswordParam << ['sub 12345']
        subRemoteParam << ['sub remote param']
        subBranchParam << ['sub branch param']
    }

}