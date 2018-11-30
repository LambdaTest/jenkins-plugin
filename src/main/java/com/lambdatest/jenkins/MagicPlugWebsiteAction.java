package com.lambdatest.jenkins;

import hudson.Extension;
import hudson.model.RootAction;
import jenkins.model.Jenkins;

@Extension
public class MagicPlugWebsiteAction implements RootAction {

	@Override
	public String getIconFileName() {
		System.out.println(Jenkins.RESOURCE_PATH + "/plugins/credentials/images/32x32/logo.png");
		return (Jenkins.RESOURCE_PATH + "/plugins/credentials/images/32x32/logo.png");
	}

	@Override
	public String getDisplayName() {
		return "LambdaTest Website";
	}

	@Override
	public String getUrlName() {
		return "https://app.lambdatest.com/";
	}

}
