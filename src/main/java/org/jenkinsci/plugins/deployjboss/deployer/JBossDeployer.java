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

import java.io.File;
import org.jenkinsci.plugins.deployjboss.repackaged.MatchPatternStrategy;
import org.jenkinsci.plugins.deployjboss.repackaged.Deployment;
import java.io.IOException;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.domain.DomainClient;
import org.jenkinsci.plugins.deployjboss.repackaged.DeploymentExecutionException;
import org.jenkinsci.plugins.deployjboss.repackaged.DeploymentFailureException;
import org.jenkinsci.plugins.deployjboss.repackaged.Domain;
import org.jenkinsci.plugins.deployjboss.repackaged.DomainDeployment;
import org.jenkinsci.plugins.deployjboss.repackaged.StandaloneDeployment;

/**
 *
 * @author Daniel Bustamante Ospina
 */
public class JBossDeployer {
    
    private ModelControllerClient client;
    private final ClientFactory clientFactory = new ClientFactory();
    
    private final String hostname;
    private final int port;
    private final String username;
    private final String password;
    private final Domain domain;

    public JBossDeployer(String hostname, Integer port, String username, String password, String serverGroup) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.domain = new Domain();
        domain.getServerGroups().add(serverGroup);
    }
    
    public void deploy(File file, String deploymentName) throws JBossDeployerException{
        try {
            validate();
            getClient();
            final String matchPattern = null;
            final MatchPatternStrategy matchPatternStrategy = null;
            final Deployment deployment;
            if (isDomainServer()) {
                deployment = DomainDeployment.create((DomainClient) client, domain, file, deploymentName, getType(), matchPattern, matchPatternStrategy);
            } else {
                deployment = StandaloneDeployment.create(client, file, deploymentName, getType(), matchPattern, matchPatternStrategy);
            }
            switch (executeDeployment(client, deployment)) {
                case REQUIRES_RESTART: {
//                    getLog().info("Server requires a restart");
                    break;
                }
                case SUCCESS:
                    break;
            }
        } catch (Exception ex) {
            throw new JBossDeployerException(ex.getMessage(), ex);
        } finally {
            close();
        }
    }   
    
    protected final Deployment.Status executeDeployment(final ModelControllerClient client, final Deployment deployment) throws DeploymentExecutionException, DeploymentFailureException, IOException {
        return deployment.execute();
    }    
    
    public Deployment.Type getType(){
        return Deployment.Type.FORCE_DEPLOY;
    }
    
    public final boolean isDomainServer() {
        return clientFactory.isDomainServer(getClient());
    }    
    
    public final synchronized ModelControllerClient getClient() {
        if(client == null){
            client = clientFactory.getClient(hostname, port, username, password);
        }
        return client;
    }    
    
    protected void validate() {
        if (isDomainServer()) {
            if (domain == null || domain.getServerGroups().isEmpty()) {
                throw new DeploymentFailureException(
                        "Server is running in domain mode, but no server groups have been defined.");
            }
        } /*else if (domain != null && !domain.getServerGroups().isEmpty()) {
            throw new DeploymentFailureException("Server is running in standalone mode, but server groups have been defined.");
        }*/
    }      

    private void close() {
        clientFactory.closeClient(client);
    }
    
}
