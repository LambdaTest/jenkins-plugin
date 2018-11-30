
package com.lambdatest.jenkins.freestyle.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "operatingSystem",
    "browser",
    "resolution"
})
public class SeleniumCapabilityRequest {

    @JsonProperty("operatingSystem")
    private String operatingSystem;
    @JsonProperty("browser")
    private String browser;
    @JsonProperty("resolution")
    private String resolution;

    @JsonProperty("operatingSystem")
    public String getOperatingSystem() {
        return operatingSystem;
    }

    @JsonProperty("operatingSystem")
    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    @JsonProperty("browser")
    public String getBrowser() {
        return browser;
    }

    @JsonProperty("browser")
    public void setBrowser(String browser) {
        this.browser = browser;
    }

    @JsonProperty("resolution")
    public String getResolution() {
        return resolution;
    }

    @JsonProperty("resolution")
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{operatingSystem=");
		builder.append(operatingSystem);
		builder.append(", browser=");
		builder.append(browser);
		builder.append(", resolution=");
		builder.append(resolution);
		builder.append("}");
		return builder.toString();
	}
    
    

}
