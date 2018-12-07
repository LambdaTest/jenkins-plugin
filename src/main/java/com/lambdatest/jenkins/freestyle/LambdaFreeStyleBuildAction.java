package com.lambdatest.jenkins.freestyle;

public class LambdaFreeStyleBuildAction extends AbstractFreeStyleBuildAction {
	/*
	 * Holds info about the Selenium Test
	 */
	private String operatingSystem;
	private String browser;
	private String resolution;
	private String buildName;
	private String buildNumber;
	private String testType;
	private String iframeLink;

	LambdaFreeStyleBuildAction(final String testType, final String operatingSystem, final String browser,
			final String resolution) {
		super();
		this.testType = testType;
		this.operatingSystem = operatingSystem;
		this.browser = browser;
		this.resolution = resolution;
		setIconFileName("document.png");
		setDisplayName("LT(" + operatingSystem + " " + browser + " " + resolution + ")");
		setTestUrl(displayName);
	}

	public String getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getBuildName() {
		return buildName;
	}

	public void setBuildName(String buildName) {
		this.buildName = buildName;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}

	public String getIframeLink() {
		return iframeLink;
	}

	public void setIframeLink(String buildNumber, String username, String accessToken) {
		this.iframeLink = new StringBuilder("http://dev.lambdatest.io:3000/jenkins/?buildID=['").append(buildNumber)
				.append("']&token=").append(accessToken).append("&username=").append(username).append("&auth=jenkins")
				.toString();
	}

}
