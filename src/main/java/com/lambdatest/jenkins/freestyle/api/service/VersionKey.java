package com.lambdatest.jenkins.freestyle.api.service;

public class VersionKey {

	private String operatingSystem;
	private String browserName;

	public VersionKey(String operatingSystem, String browserName) {
		super();
		this.operatingSystem = operatingSystem;
		this.browserName = browserName;
	}

	public String getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	public String getBrowserName() {
		return browserName;
	}

	public void setBrowserName(String browserName) {
		this.browserName = browserName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((browserName == null) ? 0 : browserName.hashCode());
		result = prime * result + ((operatingSystem == null) ? 0 : operatingSystem.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VersionKey other = (VersionKey) obj;
		if (browserName == null) {
			if (other.browserName != null)
				return false;
		} else if (!browserName.equals(other.browserName))
			return false;
		if (operatingSystem == null) {
			if (other.operatingSystem != null)
				return false;
		} else if (!operatingSystem.equals(other.operatingSystem))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\noperatingSystem=");
		builder.append(operatingSystem);
		builder.append(",\nbrowserName=");
		builder.append(browserName);
		builder.append("\n}");
		return builder.toString();
	}

}
