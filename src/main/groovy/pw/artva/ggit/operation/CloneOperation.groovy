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
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.TransportCommand
import pw.artva.ggit.config.RepositoryConfig
import pw.artva.ggit.operation.base.SimpleTransportOperation

/**
 * @author Artur Vakhrameev
 */
@InheritConstructors
class CloneOperation extends SimpleTransportOperation {

    @Override
    protected TransportCommand simpleCommand(RepositoryConfig config) {
        return Git.cloneRepository()
                .setDirectory(config.path)
                .setURI(config.remoteUrl)
                .setBranch(config.branch)
    }

    @Override
    protected void logComplete() {
        log.info "${config.name}: clone operation complete successfully"
    }
}
