package com.lambdatest.jenkins.freestyle.api;

import jenkins.model.Jenkins;

public interface Constant {

	String NOT_AVAILABLE = "NA";
	String EMPTY = "";
	String LT_OPERATING_SYSTEM = "LT_OPERATING_SYSTEM";
	String LT_BROWSERS = "LT_BROWSERS";
	String LT_BROWSER_NAME = "LT_BROWSER_NAME";
	String LT_BROWSER_VERSION = "LT_BROWSER_VERSION";
	String LT_RESOLUTION = "LT_RESOLUTION";
	String LT_GRID_URL = "LT_GRID_URL";
	String LT_BUILD_NAME = "LT_BUILD_NAME";
	String LT_BUILD_NUMBER = "LT_BUILD_NUMBER";
	String USERNAME = "USERNAME";
	String ACCESS_TOKEN = "ACCESS_TOKEN";

	String OPERATING_SYSTEM = "operatingSystem";
	String BROWSER_NAME = "browserName";
	String BROWSER_VERSION = "browserVersion";
	String RESOLUTION = "resolution";

	String OS_API_URL = "https://dev-api.lambdatest.com/api/v1/capability?format=array";
	String BROWSER_API_URL = "https://dev-api.lambdatest.com/api/v1/capability?format=array&os=";
	String AUTH_API_URL = "https://beta-accounts.lambdatest.com/api/user/token/auth";

	interface Stage {
		String APP_URL = "https://stage-automation.lambdatest.com";
		String HUB_URL = "@stage-hub.lambdatest.com/wd/hub";
	}

	interface Beta {
		String APP_URL = "https://beta-automation.lambdatest.com";
		String HUB_URL = "@beta-hub.lambdatest.com/wd/hub";
	}

	interface Dev {
		String APP_URL = "https://dev-automation.lambdatest.com";
		String HUB_URL = "@dev-hub.lambdatest.com/wd/hub";
	}

	String DEFAULT_OPERATING_SYSTEM_VALUE = "Select Operating System";
	String DEFAULT_BROWSER_NAME_VALUE = "Select BrowserName";
	String DEFAULT_BROWSER_VERSION_VALUE = "Select BrowserVersion";
	String DEFAULT_RESOLUTION_VALUE = "Select Screen Resolution";

	String STAGE = "stage";
	String DEV = "dev";
	String BETA = "beta";

	String MAC_HASH_URL = "https://d190rtvbt0l12i.cloudfront.net/mac/latest";
	String MAC_BINARY_URL = "https://d190rtvbt0l12i.cloudfront.net/mac/LTC-mac.sh";

	String LINUX_HASH_URL = "https://d190rtvbt0l12i.cloudfront.net/linux/latest";
	String LINUX_BINARY_URL = "https://d190rtvbt0l12i.cloudfront.net/linux/LTC.sh";

	String WIN_HASH_URL = "https://d190rtvbt0l12i.cloudfront.net/windows/latest";
	String WIN_BINARY_URL = "https://d190rtvbt0l12i.cloudfront.net/windows/LTC.exe";

	String DEFAULT_TUNNEL_NAME = "jenkins-tunnel";
	String LT_TUNNEL_NAME = "LT_TUNNEL_NAME";

	String LT_ICON_FILE_NAME = Jenkins.RESOURCE_PATH + "/plugin/lambdatest-automation/images/logo.png";

}
