
package com.lambdatest.jenkins.freestyle.api.browser;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "selenium"
})
public class Version {

    @JsonProperty("id")
    private String id;
    @JsonProperty("selenium")
    private List<String> selenium = null;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("selenium")
    public List<String> getSelenium() {
        return selenium;
    }

    @JsonProperty("selenium")
    public void setSelenium(List<String> selenium) {
        this.selenium = selenium;
    }

}
