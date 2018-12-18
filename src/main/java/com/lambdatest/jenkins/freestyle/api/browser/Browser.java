
package com.lambdatest.jenkins.freestyle.api.browser;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "versions" })
public class Browser {

	@JsonProperty("id")
	private String id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("versions")
	private List<BrowserVersion> browserVersions = null;

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("versions")
	public List<BrowserVersion> getVersions() {
		return browserVersions;
	}

	@JsonProperty("versions")
	public void setVersions(List<BrowserVersion> browserVersions) {
		this.browserVersions = browserVersions;
	}

}
