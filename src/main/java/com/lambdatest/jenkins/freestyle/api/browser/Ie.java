
package com.lambdatest.jenkins.freestyle.api.browser;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ie {

	@JsonProperty("ie-11")
	private List<String> ie11 = null;

	public List<String> getIe11() {
		return ie11;
	}

	public void setIe11(List<String> ie11) {
		this.ie11 = ie11;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\nie11=");
		builder.append(ie11);
		builder.append("\n}");
		return builder.toString();
	}

	public List<String> getIEBrowsersList() {
		List<String> ieBrowsers=new ArrayList<String>();
		if(this.ie11!=null) {
			ieBrowsers.add("ie-11");
		}
		return ieBrowsers;
	}
	
}
