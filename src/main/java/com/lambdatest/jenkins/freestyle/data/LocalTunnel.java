package com.lambdatest.jenkins.freestyle.data;

import java.io.Serializable;

import org.kohsuke.stapler.DataBoundConstructor;

public class LocalTunnel implements Serializable {

	private static final long serialVersionUID = 1L;
	private String tunnelName;

	@DataBoundConstructor
	public LocalTunnel(String tunnelName) {
		super();
		this.tunnelName = tunnelName;
	}

	public String getTunnelName() {
		return tunnelName;
	}

	public void setTunnelName(String tunnelName) {
		this.tunnelName = tunnelName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{tunnelName=");
		builder.append(tunnelName);
		builder.append("}");
		return builder.toString();
	}

}
