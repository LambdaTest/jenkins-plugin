
package com.lambdatest.jenkins.freestyle.api.osystem;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "os"
})
public class OSList {

    @JsonProperty("os")
    private List<O> os = null;

    @JsonProperty("os")
    public List<O> getOs() {
        return os;
    }

    @JsonProperty("os")
    public void setOs(List<O> os) {
        this.os = os;
    }

}
