/*
 * The MIT License
 *
 * Copyright (c) <2011> <Bruno P. Kinoshita>
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
package jenkins.plugins.mrbayes;

import jenkins.plugins.mrbayes.util.Messages;
import hudson.CopyOnWrite;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Descriptor of MrBayes builder.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 * @see {@link MrBayesBuilder}
 */
public class MrBayesBuilderDescriptor extends Descriptor<Builder> {

	public final Class<MrBayesBuilder> builderType = MrBayesBuilder.class;
	
	private static final String DISPLAY_NAME = Messages.MrBayesDescriptor_DisplayName();
	
	@CopyOnWrite
	private volatile MrBayesInstallation[] installations = new MrBayesInstallation[0];
	
	public MrBayesBuilderDescriptor() {
		super(MrBayesBuilder.class);
		load();
	}
	
	/* (non-Javadoc)
	 * @see hudson.model.Descriptor#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return DISPLAY_NAME;
	}
	
	public MrBayesInstallation[] getInstallations() {
		return this.installations;
	}
	
	public MrBayesInstallation getInstallationByName(String name) {
		MrBayesInstallation found = null;
		for(MrBayesInstallation installation : this.installations) {
			if (StringUtils.isNotEmpty(installation.getName())) {
				if(name.equals(installation.getName())) {
					found = installation;
					break;
				}
			}
		}
		return found;
	}
	
	/* (non-Javadoc)
	 * @see hudson.model.Descriptor#configure(org.kohsuke.stapler.StaplerRequest, net.sf.json.JSONObject)
	 */
	@Override
	public boolean configure(StaplerRequest req, JSONObject json)
			throws hudson.model.Descriptor.FormException {
		this.installations = req.bindParametersToList(MrBayesInstallation.class, "MrBayes.").toArray(new MrBayesInstallation[0]);
		save();
		return Boolean.TRUE;
	}
	
	public FormValidation doRequired(@QueryParameter String value) {
		FormValidation returnValue = FormValidation.ok();
		if(StringUtils.isBlank(value)) {
			returnValue = FormValidation.error(Messages.MrBayesDescriptor_Required());
		}
		return returnValue;
	}
	
}
