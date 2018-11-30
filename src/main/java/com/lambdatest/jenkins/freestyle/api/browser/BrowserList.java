
package com.lambdatest.jenkins.freestyle.api.browser;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrowserList {

    @JsonProperty("chrome")
    private Chrome chrome;
    @JsonProperty("firefox")
    private Firefox firefox;
    @JsonProperty("edge")
    private Edge edge;
    @JsonProperty("ie")
    private Ie ie;
    
	public Chrome getChrome() {
		return chrome;
	}
	public void setChrome(Chrome chrome) {
		this.chrome = chrome;
	}
	public Firefox getFirefox() {
		return firefox;
	}
	public void setFirefox(Firefox firefox) {
		this.firefox = firefox;
	}
	public Edge getEdge() {
		return edge;
	}
	public void setEdge(Edge edge) {
		this.edge = edge;
	}
	public Ie getIe() {
		return ie;
	}
	public void setIe(Ie ie) {
		this.ie = ie;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\nchrome=");
		builder.append(chrome);
		builder.append(",\nfirefox=");
		builder.append(firefox);
		builder.append(",\nedge=");
		builder.append(edge);
		builder.append(",\nie=");
		builder.append(ie);
		builder.append("\n}");
		return builder.toString();
	}
   
}
