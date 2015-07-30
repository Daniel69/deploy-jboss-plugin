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
import hudson.tasks.Builder;
import java.io.File;
import java.io.IOException;
import java.util.List;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.deployjboss.deployer.JBossDeployer;
import org.jenkinsci.plugins.deployjboss.deployer.JBossDeployerException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 *
 * @author Daniel Bustamante Ospina
 */
public class JBossDeployBuilder extends Builder{
    
    private String artifact;
    private String deployTarget;

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        try {
            JBossConfigItem target = getJbossTarget();
            deploy(build, listener, 
                    target.getServerName(), 
                    target.getServerGroup(), target.getServerPort(), 
                    target.getUsername(), target.getPassword());
            listener.getLogger().printf("Successful deployment!\n");
        } catch (JBossDeployerException ex) {
            ex.printStackTrace(listener.error("Error at deployment to JBoss: "));
            listener.finished(Result.UNSTABLE);
            throw new RuntimeException(ex);
        }
        return true;
    }
    
    private void deploy(AbstractBuild<?, ?> build, BuildListener listener, String serverName, String serverGroup, String serverPort, String username, String password) throws JBossDeployerException{
        listener.getLogger().printf("Deploying artifact: %s to JBoss Server: %s, Server group: %s\n", artifact, serverName, serverGroup);
        JBossDeployer deployer = new JBossDeployer(serverName, Integer.parseInt(serverPort), username, password, serverGroup);
        deployer.deploy(getFile(artifact, build), null);        
    }
    
    public JBossConfigItem getJbossTarget(){
        for(JBossConfigItem item : getDescriptor().getTargets()){
            if(item.getName().equals(deployTarget))
                return item;
        }
        return null;
    }
    
    @DataBoundConstructor
    public JBossDeployBuilder(String artifact, String deployTarget) {
        this.artifact = artifact;
        this.deployTarget = deployTarget;
    }        

    private File getFile(String artifact, AbstractBuild<?, ?> build) {
        try {
            File workspace = new File(build.getWorkspace().toURI());
            return new File(workspace, artifact);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } 
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }
    
    /**
     * See <tt>src/main/resources/../deployjboss/JBossDeploy/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension 
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        /**
         * In order to load the persisted global configuration, you have to 
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }   
        
        public List<JBossConfigItem> getTargets() {
            return JBossConfig.get().getSetupConfigItems();
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

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public String getDeployTarget() {
        return deployTarget;
    }

    public void setDeployTarget(String deployTarget) {
        this.deployTarget = deployTarget;
    }
}
