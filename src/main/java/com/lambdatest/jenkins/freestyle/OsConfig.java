package com.lambdatest.jenkins.freestyle;

import java.util.ArrayList;
import java.util.List;

public class OsConfig {

	private List<String> browsers = new ArrayList<String>();
	private List<String> resolution = new ArrayList<String>();;

	public OsConfig() {
		this.browsers.add("chrome");
		this.browsers.add("firefox");
		this.resolution.add("1280x1024");
		this.resolution.add("1920x1080");
	}

	public List<String> getBrowsers() {
		return browsers;
	}


	public List<String> getResolution() {
		return resolution;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OsConfig {\nbrowsers=");
		builder.append(browsers);
		builder.append(", \nresolution=");
		builder.append(resolution);
		builder.append("\n}");
		return builder.toString();
	}
}
