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
import pw.artva.ggit.core.GitConfig

/**
 * @author Artur Vakhrameev
 */
abstract class SimpleOperation<T> extends AbstractOperation {

    SimpleOperation(GitConfig gitConfig, boolean chain) {
        super(gitConfig, chain)
    }

    @Override
    void execute() {
        if (chain) {
            def commands = [command(gitConfig)]
            fillChildren(commands, gitConfig)
            commands.each {it.call()}
        } else {
            command(gitConfig).call()
        }
    }

    protected void fillChildren(List<GitCommand<T>> commands, GitConfig config) {
        //adds children recursively
        config.subModules.all {
            commands.add(command(it))
            it.subModules.all {
                fillChildren(commands, it)
            }
        }
    }

    protected abstract GitCommand<T> command(GitConfig config)
}
