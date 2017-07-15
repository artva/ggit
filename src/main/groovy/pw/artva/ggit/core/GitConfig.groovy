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
 * Git configuration class for gradle extension.
 *
 * @author Artur Vakhrameev
 */
class GitConfig {

    public static final String EXTENSION_NAME = "gitConfig"

    Project project
    String name
    GitRepository repository
    GitAuth auth
    NamedDomainObjectContainer<GitConfig> subModules
    GitConfig parent

    GitConfig(String name) {
        this.name = name
    }

    GitConfig() {
    }

    void repository(Closure closure) {
        repository = new GitRepository()
        closure.delegate = repository
        closure()
    }

    void auth(Closure closure) {
        auth = new GitAuth()
        closure.delegate = auth
        closure()
    }

    def subModules(final Closure configureClosure) {
        subModules.configure(configureClosure)
        subModules.all {
            delegate.project(this.project)
            delegate.parent = this
        }
    }

    def project(final Project) {
        this.project = project
        subModules = project.container(GitConfig)
    }

}
