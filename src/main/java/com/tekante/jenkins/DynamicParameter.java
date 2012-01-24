package com.tekante.jenkins;

import hudson.Extension;
import org.kohsuke.stapler.QueryParameter;

import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.ParameterValue;
import hudson.model.ParameterDefinition;
import hudson.model.StringParameterValue;
import hudson.model.User;
import hudson.model.ParameterDefinition.ParameterDescriptor;
import hudson.util.ListBoxModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;

public class DynamicParameter extends ParameterDefinition {
	static final long serialVersionUID = 1;
    public String value = "";
    
	@DataBoundConstructor
	public DynamicParameter(String name, String description) {
		super(name, description);
	}

	@Exported
	public String getValue() {
		return "two";
	}

	@Extension
	public static final class DescriptorImpl extends ParameterDescriptor {
		@Override
		public String getDisplayName() {
			return "Dynamic Parameter";
		}
		
		public ListBoxModel doFillValueItems() {
			ListBoxModel m = new ListBoxModel();
			m.add("one");
			m.add("two");
			m.add("three");
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
