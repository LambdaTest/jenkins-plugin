package com.lambdatest.jenkins.credential;

import com.cloudbees.plugins.credentials.common.StandardCredentials;

public interface MagicPlugCredentials extends StandardCredentials {
	String getUserName();
	String getAuthKey();
}