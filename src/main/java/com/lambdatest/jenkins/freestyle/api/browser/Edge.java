
package com.lambdatest.jenkins.freestyle.api.browser;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class Edge {

	@JsonProperty("edge-17")
	private List<String> edge17 = null;
	@JsonProperty("edge-16")
	private List<String> edge16 = null;
	@JsonProperty("edge-15")
	private List<String> edge15 = null;

	public List<String> getEdge17() {
		return edge17;
	}

	public void setEdge17(List<String> edge17) {
		this.edge17 = edge17;
	}

	public List<String> getEdge16() {
		return edge16;
	}

	public void setEdge16(List<String> edge16) {
		this.edge16 = edge16;
	}

	public List<String> getEdge15() {
		return edge15;
	}

	public void setEdge15(List<String> edge15) {
		this.edge15 = edge15;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\nedge17=");
		builder.append(edge17);
		builder.append(",\nedge16=");
		builder.append(edge16);
		builder.append(",\nedge15=");
		builder.append(edge15);
		builder.append("\n}");
		return builder.toString();
	}

	
	public List<String> getEdgeBrowsersList() {
		List<String> edgeBrowsers=new ArrayList<String>();
		if(this.edge17!=null) {
			edgeBrowsers.add("edge-17");
		}
		if(this.edge16!=null) {
			edgeBrowsers.add("edge-16");
		}
		if(this.edge15!=null) {
			edgeBrowsers.add("edge-15");
		}
		return edgeBrowsers;
	}
	
}
