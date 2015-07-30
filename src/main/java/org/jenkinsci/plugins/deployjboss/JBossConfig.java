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

 
    //TODO: Implement data validations

//    public FormValidation doCheckFilesDir(@QueryParameter String value) {
//        Jenkins.getInstance().checkPermission(Jenkins.ADMINISTER);
//        if (Util.fixEmpty(value)==null)
//        {
//            return FormValidation.ok(); // no value
//        }
//
//        if (!new File(value).isDirectory()) {
//            return FormValidation.error("Directory "+value+" doesn't exist");
//        }
//
//        return FormValidation.ok();
//    }
//
//    public FormValidation doCheckAssignedLabelString(@QueryParameter String value) {
//        if (Util.fixEmpty(value)==null) {
//            return FormValidation.ok(); // nothing typed yet
//        }
//
//        try {
//            Label.parseExpression(value);
//        } catch (ANTLRException e) {
//            return FormValidation.error(e,
//                    Messages.AbstractProject_AssignedLabelString_InvalidBooleanExpression(e.getMessage()));
//        }
//
//        Label l = Jenkins.getInstance().getLabel(value);
//
//        if (l.isEmpty()) {
//            for (LabelAtom a : l.listAtoms()) {
//                if (a.isEmpty()) {
//                    LabelAtom nearest = LabelAtom.findNearest(a.getName());
//                    return FormValidation.warning(Messages.AbstractProject_AssignedLabelString_NoMatch_DidYouMean(a.getName(),nearest.getDisplayName()));
//                }
//            }
//            return FormValidation.warning(Messages.AbstractProject_AssignedLabelString_NoMatch());
//        }
//        return FormValidation.ok();
//    }

 
}
