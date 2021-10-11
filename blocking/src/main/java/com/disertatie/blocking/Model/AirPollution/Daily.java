package com.disertatie.blocking.Model.AirPollution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "o3",
        "pm10",
        "pm25",
        "uvi"
})
@Generated("jsonschema2pojo")
public class Daily {

    @JsonProperty("o3")
    private List<O3> o3 = null;
    @JsonProperty("pm10")
    private List<Pm10__1> pm10 = null;
    @JsonProperty("pm25")
    private List<Pm25> pm25 = null;
    @JsonProperty("uvi")
    private List<Uvus> uvi = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("o3")
    public List<O3> getO3() {
        return o3;
    }

    @JsonProperty("o3")
    public void setO3(List<O3> o3) {
        this.o3 = o3;
    }

    @JsonProperty("pm10")
    public List<Pm10__1> getPm10() {
        return pm10;
    }

    @JsonProperty("pm10")
    public void setPm10(List<Pm10__1> pm10) {
        this.pm10 = pm10;
    }

    @JsonProperty("pm25")
    public List<Pm25> getPm25() {
        return pm25;
    }

    @JsonProperty("pm25")
    public void setPm25(List<Pm25> pm25) {
        this.pm25 = pm25;
    }

    @JsonProperty("uvi")
    public List<Uvus> getUvi() {
        return uvi;
    }

    @JsonProperty("uvi")
    public void setUvi(List<Uvus> uvi) {
        this.uvi = uvi;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}