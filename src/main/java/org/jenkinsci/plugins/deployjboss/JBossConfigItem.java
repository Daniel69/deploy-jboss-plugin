package org.jenkinsci.plugins.deployjboss;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;


/**
 * @author Daniel Bustamante Ospina
 */
public class JBossConfigItem extends AbstractDescribableImpl<JBossConfigItem> {

    private String name;
    private String serverName;
    private String serverPort;
    private String serverGroup;
    private String username;
    private String password;

    @DataBoundConstructor
    public JBossConfigItem(String name, String serverName, String serverPort, String serverGroup, String username, String password) {
        this.name = name;
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.serverGroup = serverGroup;
        this.username = username;
        this.password = password;
    }    
    
    public JBossConfigItem() {
    }
   
    @Extension
    public static class SetupConfigItemDescriptor extends Descriptor<JBossConfigItem> {

        @Override
        public String getDisplayName() {
            return "";
        }
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerGroup() {
        return serverGroup;
    }

    public void setServerGroup(String serverGroup) {
        this.serverGroup = serverGroup;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}