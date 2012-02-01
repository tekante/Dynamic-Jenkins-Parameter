package com.tekante.jenkins;

import hudson.Extension;

import hudson.model.Hudson;
import hudson.model.ParameterValue;
import hudson.model.ParameterDefinition;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.StringParameterValue;
import hudson.util.ListBoxModel;
import hudson.model.Job;

import java.util.List;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class DynamicParameter extends ParameterDefinition {
	static final long serialVersionUID = 2;
    public String value = "";
    public String dynamicValue = "";
    public String valueOptions;
    public String dynamicValueOptions;
    public String secondName;
    
	@DataBoundConstructor
	public DynamicParameter(String name, String description, String valueOptions, String dynamicValueOptions, String secondName) {
		super(name, description);
		this.secondName = secondName;
		this.valueOptions = valueOptions;
		this.dynamicValueOptions = dynamicValueOptions;
	}

	@Extension
	public static final class DescriptorImpl extends ParameterDescriptor {
		@Override
		public String getDisplayName() {
			return "Dynamic Parameter";
		}

		private DynamicParameter getDynamicParameter() {
			String containsJobName = getCurrentDescriptorByNameUrl();
			String jobName = java.net.URLDecoder.decode(containsJobName.substring(containsJobName.lastIndexOf("/") + 1));
			Job<?,?> j = Hudson.getInstance().getItemByFullName(jobName, hudson.model.Job.class);
			if (j != null) {
				ParametersDefinitionProperty pdp = j.getProperty(hudson.model.ParametersDefinitionProperty.class);
				List<ParameterDefinition> pds = pdp.getParameterDefinitions();
				for (ParameterDefinition pd : pds) {
					if (this.isInstance(pd)) {
						return (DynamicParameter) pd;
					}
				}
			}
			return null;
		}

		public ListBoxModel doFillValueItems() {
			Logger l = Logger.getLogger(DynamicParameter.class.getName());
			ListBoxModel m = new ListBoxModel();

			DynamicParameter dp = this.getDynamicParameter();
			if (dp != null) {
				for (String s : dp.valueOptions.split("\\r?\\n")) {
					m.add(s);
				}
			}
			return m;
		}

		public ListBoxModel doFillDynamicValueItems(@QueryParameter String value) {
			Logger l = Logger.getLogger(DynamicParameter.class.getName());
			ListBoxModel m = new ListBoxModel();

			DynamicParameter dp = this.getDynamicParameter();
			if (dp != null) {
				for (String s : dp.dynamicValueOptions.split("\\r?\\n")) {
					if (s.indexOf(value) == 0) {
						m.add(s);
					}
				}
			}
			return m;
		}
	}

	@Override
	public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
		StringParameterValue value = req.bindJSON(StringParameterValue.class, jo);
		return value;
	}

	@Override
	public ParameterValue createValue(StaplerRequest req) {
		String[] value = req.getParameterValues(getName());
		return new StringParameterValue(getName(), value[0]);
	}
}
