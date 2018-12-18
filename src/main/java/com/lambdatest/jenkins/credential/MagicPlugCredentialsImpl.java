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
	private final String accessToken;

	@DataBoundConstructor
	public MagicPlugCredentialsImpl(CredentialsScope scope, String id, String description, String username,
			String accessToken) throws Exception {
		super(scope, id, description);
		try {
			System.out.println("MagicPlugCredentialsImpl");
			this.username = username;
			this.accessToken = accessToken;
			System.out.println("Here We can Verify Credentials Also before Adding");
			if (!CapabilityService.isValidUser(username, accessToken)) {
				throw new Exception("Invalid username and access Token");
			}
		} catch (Exception e) {
			throw e;
		}
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
			return "LAMBDATEST Credentials";
		}

		@Override
		public String getCredentialsPage() {
			return super.getCredentialsPage();
		}

		public FormValidation doVerifyCredentials(@QueryParameter("username") final String username,
				@QueryParameter("accessToken") final String accessToken) throws IOException, ServletException {
			System.out.println(username + ":" + accessToken);
			if (StringUtils.isBlank(username) || StringUtils.isBlank(accessToken)) {
				return FormValidation.error("Please enter valid username and authKey");
			}
			if (CapabilityService.isValidUser(username, accessToken)) {
				return FormValidation.ok("Successful Authentication");
			} else {
				return FormValidation.error("Invalid Credentials");
			}
		}

		public FormValidation doCheckUsername(@QueryParameter String username) throws IOException, ServletException {
			try {
				if (StringUtils.isBlank(username)) {
					return FormValidation.error("Invalid username");
				}
				return FormValidation.ok();
			} catch (NumberFormatException e) {
				return FormValidation.error("Invalid username");
			}
		}

		public FormValidation doCheckAccessToken(@QueryParameter String accessToken)
				throws IOException, ServletException {
			try {
				if (StringUtils.isBlank(accessToken)) {
					return FormValidation.error("Invalid Access Token");
				}
				return FormValidation.ok();
			} catch (NumberFormatException e) {
				return FormValidation.error("Invalid Access Token");
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
	public String getAccessToken() {
		if (this.accessToken == null) {
			return Constant.NOT_AVAILABLE;
		} else {
			return this.accessToken;
		}
	}

	@Override
	public String getDisplayName() {
		return getUserName();
	}

}