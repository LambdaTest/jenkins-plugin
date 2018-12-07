package com.lambdatest.jenkins.freestyle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import org.springframework.util.CollectionUtils;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.lambdatest.jenkins.credential.MagicPlugCredentialsImpl;
import com.lambdatest.jenkins.freestyle.api.Constant;
import com.lambdatest.jenkins.freestyle.api.service.CapabilityService;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.ItemGroup;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.ListBoxModel;

@Extension
public class MagicPlugDescriptor extends BuildWrapperDescriptor {

	private static final Map<String, OsConfig> operatingSystems = new HashMap<>();
	static {
		operatingSystems.put("win10", new OsConfig());
		operatingSystems.put("sierra", new OsConfig());
	}

	@Override
	public boolean isApplicable(AbstractProject<?, ?> item) {
		return true;
	}

	@Override
	public String getDisplayName() {
		return "LambdaTest";
	}

	public MagicPlugDescriptor() {
		super(MagicPlugBuildWrapper.class);
		load();
	}

	public ListBoxModel doFillOperatingSystemItems() {
		Map<String, String> supportedOS = CapabilityService.getOperatingSystems();
		ListBoxModel items = new ListBoxModel();
		items.add(Constant.DEFAULT_OPERATING_SYSTEM_VALUE, Constant.EMPTY);
		supportedOS.forEach((key, value) -> {
			items.add(value, key);
		});
		return items;
	}

	public ListBoxModel doFillBrowserItems(@QueryParameter String operatingSystem) {
		ListBoxModel items = new ListBoxModel();
		if (StringUtils.isBlank(operatingSystem)) {
			items.add(Constant.DEFAULT_BROWSER_VALUE, Constant.EMPTY);
			return items;
		}
		System.out.println(operatingSystem);
		List<String> supportedBrowsers = CapabilityService.getBrowsers(operatingSystem);
		if (!CollectionUtils.isEmpty(supportedBrowsers)) {
			supportedBrowsers.forEach(br -> {
				items.add(br, br);
			});
		}
		return items;
	}

	public ListBoxModel doFillResolutionItems(@QueryParameter String operatingSystem) {
		ListBoxModel items = new ListBoxModel();
		if (StringUtils.isBlank(operatingSystem)) {
			items.add(Constant.DEFAULT_RESOLUTION_VALUE, Constant.EMPTY);
			return items;
		}
		System.out.println(operatingSystem);
		List<String> supportedBrowsers = CapabilityService.getResolution(operatingSystem);
		if (!CollectionUtils.isEmpty(supportedBrowsers)) {
			supportedBrowsers.forEach(br -> {
				items.add(br, br);
			});
		}
		return items;
	}

	public ListBoxModel doFillCredentialsIdItems(@QueryParameter String credentialsId) {
		if (!StringUtils.isBlank(credentialsId)) {
			System.out.println(credentialsId);
		} else {
			System.out.println("Not Found");
		}
		return new ListBoxModel();
	}

	public ListBoxModel doFillCredentialsIdItems(ItemGroup context) {
		return new StandardListBoxModel().withEmptySelection().withMatching(CredentialsMatchers.always(),
				MagicPlugCredentialsImpl.all(context));
	}
}
