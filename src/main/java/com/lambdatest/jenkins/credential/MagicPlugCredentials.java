package com.lambdatest.jenkins.credential;

import org.apache.commons.lang.StringUtils;

import com.cloudbees.plugins.credentials.CredentialsNameProvider;
import com.cloudbees.plugins.credentials.NameWith;
import com.cloudbees.plugins.credentials.common.StandardCredentials;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Util;

@NameWith(value = MagicPlugCredentials.NameProvider.class, priority = 1)
public interface MagicPlugCredentials extends StandardCredentials {
	String getUserName();

	String getAccessToken();

	String getDisplayName();
	
	public class NameProvider extends CredentialsNameProvider<MagicPlugCredentials> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName(@NonNull MagicPlugCredentials c) {
			StringBuilder name = new StringBuilder().append(c.getDisplayName());
			String description = Util.fixEmptyAndTrim(c.getDescription());
			if (StringUtils.isBlank(description)) {
				description = c.getId();
			}
			name.append(" (").append(description).append(")");
			return name.toString();
		}
	}

}