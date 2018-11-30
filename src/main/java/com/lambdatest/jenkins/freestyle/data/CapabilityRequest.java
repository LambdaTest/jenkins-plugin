
package com.lambdatest.jenkins.freestyle.data;

import java.util.List;

import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.StaplerResponseWrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "seleniumCapabilityRequest"
})
public class CapabilityRequest extends StaplerResponseWrapper {

    public CapabilityRequest(StaplerResponse wrapped) {
    	super(wrapped);
    	System.out.println(wrapped);
	}

	@JsonProperty("seleniumCapabilityRequest")
    private List<SeleniumCapabilityRequest> seleniumCapabilityRequest = null;

    @JsonProperty("seleniumCapabilityRequest")
    public List<SeleniumCapabilityRequest> getSeleniumCapabilityRequest() {
        return seleniumCapabilityRequest;
    }

    @JsonProperty("seleniumCapabilityRequest")
    public void setSeleniumCapabilityRequest(List<SeleniumCapabilityRequest> seleniumCapabilityRequest) {
        this.seleniumCapabilityRequest = seleniumCapabilityRequest;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{seleniumCapabilityRequest=");
		builder.append(seleniumCapabilityRequest);
		builder.append("}");
		return builder.toString();
	}
    
    

}
