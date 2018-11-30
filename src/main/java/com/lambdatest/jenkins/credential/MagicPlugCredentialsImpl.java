package com.lambdatest.jenkins.credential;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.cloudbees.plugins.credentials.CredentialsDescriptor;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;
import com.lambdatest.jenkins.freestyle.api.Constant;
import com.lambdatest.jenkins.freestyle.api.service.CapabilityService;

import hudson.Extension;
import hudson.model.ItemGroup;
import hudson.security.ACL;
import hudson.util.FormValidation;

public class MagicPlugCredentialsImpl extends BaseStandardCredentials implements MagicPlugCredentials {

	private static final long serialVersionUID = 1L;
	private final String username;
	private final String authKey;

	@DataBoundConstructor
	public MagicPlugCredentialsImpl(CredentialsScope scope, String id, String description, String username,
			String authKey) {
		super(scope, id, description);
		System.out.println("MagicPlugCredentialsImpl");
		this.username = username;
		this.authKey = authKey;
	}

	@SuppressWarnings("unchecked")
	public static List<MagicPlugCredentials> all(@SuppressWarnings("rawtypes") ItemGroup context) {
		return CredentialsProvider.lookupCredentials(MagicPlugCredentials.class, context, ACL.SYSTEM,
				Collections.EMPTY_LIST);
	}

	@SuppressWarnings("unchecked")
	@CheckForNull
	public static MagicPlugCredentials getCredentials(@Nullable String credentialsId,
			@SuppressWarnings("rawtypes") ItemGroup context) {
		if (StringUtils.isBlank(credentialsId)) {
			return null;
		}
		return (MagicPlugCredentials) CredentialsMatchers.firstOrNull(CredentialsProvider
				.lookupCredentials(MagicPlugCredentials.class, context, ACL.SYSTEM, Collections.EMPTY_LIST),
				CredentialsMatchers.withId(credentialsId));
	}

	@Extension
	public static class DescriptorImpl extends CredentialsDescriptor {

		@Override
		public String getDisplayName() {
			return "MagicPlug Credentials";
		}

		@Override
		public String getCredentialsPage() {
			return super.getCredentialsPage();
		}

		public FormValidation doVerifyCredentials(@QueryParameter("username") final String username,
				@QueryParameter("authKey") final String authKey) throws IOException, ServletException {
			System.out.println(username + ":" + authKey);
			if (StringUtils.isBlank(username) || StringUtils.isBlank(authKey)) {
				return FormValidation.error("Please enter valid username and authKey");
			}
			if (CapabilityService.isValidUser(username, authKey)) {
				return FormValidation.ok("Successful Authentication");
			} else {
				return FormValidation.error("Invalid Credentials");
			}
		}

	}

	@Override
	public String getUserName() {
		if (this.username == null) {
			return Constant.NOT_AVAILABLE;
		} else {
			return this.username;
		}
	}

	@Override
	public String getAuthKey() {
		if (this.authKey == null) {
			return Constant.NOT_AVAILABLE;
		} else {
			return this.authKey;
		}
	}

}