package org.jenkinsci.plugins.deployjboss;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Daniel Bustamante Ospina
 */
@Extension
public class JBossConfig extends GlobalConfiguration {

  
    private List<JBossConfigItem> setupConfigItems = new ArrayList<JBossConfigItem>();
    
    public JBossConfig() {
        load();
    }

    public List<JBossConfigItem> getSetupConfigItems() {
        return setupConfigItems;
    }

    public void setSetupConfigItems(List<JBossConfigItem> setupConfigItems) {
        this.setupConfigItems = setupConfigItems;
    }   

    
    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this,json);
        save();
        return true;
    }

    public static JBossConfig get() {
        return GlobalConfiguration.all().get(JBossConfig.class);
    }
}
