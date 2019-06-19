package com.lambdatest.jenkins.analytics.data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalyticRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private int userId;
	private String gridURL;
	private String buildName;
	private int buildNumber;
	private String client = "Jenkins";
	private String tunnelName;
	private String capabilities;
	
	public AnalyticRequest() {
		super();
	}

	public AnalyticRequest(int userId, String gridURL, String buildName, int buildNumber) {
		super();
		this.userId = userId;
		this.gridURL = gridURL;
		this.buildName = buildName;
		this.buildNumber = buildNumber;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getGridURL() {
		return gridURL;
	}

	public void setGridURL(String gridURL) {
		this.gridURL = gridURL;
	}

	public String getBuildName() {
		return buildName;
	}

	public void setBuildName(String buildName) {
		this.buildName = buildName;
	}

	public int getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(int buildNumber) {
		this.buildNumber = buildNumber;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getTunnelName() {
		return tunnelName;
	}

	public void setTunnelName(String tunnelName) {
		this.tunnelName = tunnelName;
	}

	public String getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[userId=");
		builder.append(userId);
		builder.append(", gridURL=");
		builder.append(gridURL);
		builder.append(", buildName=");
		builder.append(buildName);
		builder.append(", buildNumber=");
		builder.append(buildNumber);
		builder.append(", client=");
		builder.append(client);
		builder.append(", tunnelName=");
		builder.append(tunnelName);
		builder.append("]");
		return builder.toString();
	}
	
}
