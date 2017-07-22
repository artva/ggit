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

import org.eclipse.jgit.api.GitCommand
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import pw.artva.ggit.core.GGit
import pw.artva.ggit.core.GitConfig

/**
 * @author Artur Vakhrameev
 */
abstract class AbstractOperation implements Operation {

    protected final GitConfig gitConfig
    protected final OperationType type

    AbstractOperation(GitConfig gitConfig, OperationType type) {
        this.gitConfig = gitConfig
        this.type = type
    }

    @Override
    void execute() {
        configureCommand()
        command().call()
    }

    @Override
    void executeAll() {
        execute()
        gitConfig.subModules.each {
            //create and execute child operation
           OperationFactory.create(this.type, it).execute()
        }
    }

    protected UsernamePasswordCredentialsProvider credentials() {
        def auth = gitConfig.auth
        return new UsernamePasswordCredentialsProvider(auth.username, auth.password)
    }

    protected void configureCommand() {
    }

    protected abstract GitCommand command()
}
