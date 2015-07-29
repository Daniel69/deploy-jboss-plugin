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
package org.jenkinsci.plugins.deployjboss;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.deployjboss.deployer.JBossDeployer;
import org.jenkinsci.plugins.deployjboss.deployer.JBossDeployerException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 *
 * @author Daniel Bustamante Ospina
 */
public class JBossDeploy extends Notifier{
    
    private final String artifact;
    private final String serverGroup;
    private final String serverName;
    private final String serverPort;
    private final String username;
    private final String password;

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        try {
            listener.getLogger().printf("Deploying artifact: %s to JBoss Server: %s, Server group: %s\n", artifact, serverName, serverGroup);
            JBossDeployer deployer = new JBossDeployer(serverName, Integer.parseInt(serverPort), username, password, serverGroup);
            deployer.deploy(getFile(artifact, build), null);
            listener.getLogger().printf("Successful deployment!\n");
        } catch (JBossDeployerException ex) {
            ex.printStackTrace(listener.error("Error at deployment to JBoss: "));
            listener.finished(Result.UNSTABLE);
        }
        return true;
    }
    
    @DataBoundConstructor
    public JBossDeploy(String artifact, String serverGroup, String serverName, String serverPort, String username, String password) {
        this.artifact = artifact;
        this.serverGroup = serverGroup;
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.username = username;
        this.password = password;
    }    

    private File getFile(String artifact, AbstractBuild<?, ?> build) {
        try {
            File workspace = new File(build.getWorkspace().toURI());
            return new File(workspace, artifact);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } 
    }
    
    /**
     * See <tt>src/main/resources/../deployjboss/JBossDeploy/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension 
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        /**
         * In order to load the persisted global configuration, you have to 
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        public FormValidation doCheckArtifact(@QueryParameter String value)   throws IOException, ServletException {
            if (!value.toLowerCase().matches(".*(\\.ear|\\.war|\\.jar)"))
                return FormValidation.error("Please specify a valid artifact (.war, .ear, .jar)");
            return FormValidation.ok();
        }
        
        public FormValidation doCheckServerIp(@QueryParameter String value)  throws IOException, ServletException {
            if (value.isEmpty())
                return FormValidation.error("Please specify a server name");
            return FormValidation.ok();
        }        

        public FormValidation doCheckServerPort(@QueryParameter String value)  throws IOException, ServletException {
            if (!value.matches("\\d+"))
                return FormValidation.error("Please a valid server port");
            return FormValidation.ok();
        }        
        
        
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         * @return 
         */
        @Override
        public String getDisplayName() {
            return "Deploy Artifact to JBoss";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws Descriptor.FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            save();
            return super.configure(req,formData);
        }
    }   
    
    public String getArtifact() {
        return artifact;
    }

    public String getServerGroup() {
        return serverGroup;
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerPort() {
        return serverPort;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }    
}
