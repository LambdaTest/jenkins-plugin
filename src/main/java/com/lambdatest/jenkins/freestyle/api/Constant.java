package com.lambdatest.jenkins.freestyle.api;

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
	String BROWSER = "browser";
	String RESOLUTION = "resolution";

	String OS_API_URL = "https://dev-ml.lambdatest.com/api/v1/capability?format=array";
	String BROWSER_API_URL = "https://dev-ml.lambdatest.com/api/v1/capability?format=array&os=";
	String AUTH_API_URL = "https://dev-accounts.lambdatest.com/api/user/token/auth";

	String DEFAULT_OPERATING_SYSTEM_VALUE = "Select Operating System";
	String DEFAULT_BROWSER_VALUE = "Select Browser Config";
	String DEFAULT_RESOLUTION_VALUE = "Select Screen Resolution";
}
