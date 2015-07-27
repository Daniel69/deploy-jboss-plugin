/*
 * The MIT License
 *
 * Copyright 2015 Daniel Bustamante Ospina <dbustamante69@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.deployjboss.deployer;

import org.jenkinsci.plugins.deployjboss.repackaged.ClientCallbackHandler;
import org.jenkinsci.plugins.deployjboss.repackaged.ServerOperations;
import java.io.IOException;
import java.net.UnknownHostException;
import javax.security.auth.callback.CallbackHandler;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.domain.DomainClient;
import org.jboss.dmr.ModelNode;

/**
 *
 * @author Daniel Bustamante Ospina
 */
public class ClientFactory {
    
    private static final int timeout = 5000;
    
    public final synchronized ModelControllerClient getClient(String hostname, int port, String username, String passwd) {
        ModelControllerClient result = null;
        try {
            result = ModelControllerClient.Factory.create(hostname, port, getCallbackHandler(username, passwd), null, timeout);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(String.format("Host name '%s' is invalid.", hostname), e);
        }
        if (isDomainServer(result)) {
            result =  DomainClient.Factory.create(result);
        }
        return result;
    } 
    
    public void closeClient(ModelControllerClient client){
        try {
            client.close();
        } catch (Exception ex) {
        }
    }
    
    private synchronized CallbackHandler getCallbackHandler(String username, String password) {
        return new ClientCallbackHandler(username, password);
    }    
    
    public boolean isDomainServer(final ModelControllerClient client) {
        boolean result = false;
        // Check this is really a domain server
        final ModelNode op = ServerOperations.createReadAttributeOperation(ServerOperations.LAUNCH_TYPE);
        try {
            final ModelNode opResult = client.execute(op);
            if (ServerOperations.isSuccessfulOutcome(opResult)) {
                result = ("DOMAIN".equals(ServerOperations.readResultAsString(opResult)));
            }
        } catch (IOException e) {
            throw new IllegalStateException(String.format("I/O Error could not execute operation '%s'", op), e);
        }
        return result;
    }    
    
}
