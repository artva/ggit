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

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

/**
 * Git configuration class
 *
 * @author Artur Vakhrameev
 */
class GitConfig {
    String name
    GitRepository repository = new GitRepository()
    GitAuth auth = new GitAuth()
    NamedDomainObjectContainer<GitConfig> subModules

    GitConfig(String name) {
        this.name = name
    }

    GitConfig() {
        dir = GGit.instance.project.projectDir
        name = dir.name
    }

    void repository(Closure closure) {
        closure.delegate = repository
        closure()
    }

    void auth(Closure closure) {
        closure.delegate = auth
        closure()
    }

    void subModules(final Closure configureClosure) {
        subModules.configure(configureClosure)
        //children config registration
        subModules.all {
            Project project = GGit.instance.project
            delegate.subModules = project.container(GitConfig)
            //copy some settings from parent
            delegate.repository.path = this.repository.path + '/' + delegate.name
            if (project.ggit.defaultFromParent)
                ConfigUtils.copyForNullSettings(delegate, this)
            }

            delegate.repository {
                path = new File(this.dir, delegate.name)
            }
        }
    }
}
