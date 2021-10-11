package com.disertatie.blocking.Model.AirPollution;

import java.util.HashMap;
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
        "s",
        "tz",
        "v",
        "iso"
})
@Generated("jsonschema2pojo")
public class Time {

    @JsonProperty("s")
    private String s;
    @JsonProperty("tz")
    private String tz;
    @JsonProperty("v")
    private Integer v;
    @JsonProperty("iso")
    private String iso;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("s")
    public String getS() {
        return s;
    }

    @JsonProperty("s")
    public void setS(String s) {
        this.s = s;
    }

    @JsonProperty("tz")
    public String getTz() {
        return tz;
    }

    @JsonProperty("tz")
    public void setTz(String tz) {
        this.tz = tz;
    }

    @JsonProperty("v")
    public Integer getV() {
        return v;
    }

    @JsonProperty("v")
    public void setV(Integer v) {
        this.v = v;
    }

    @JsonProperty("iso")
    public String getIso() {
        return iso;
    }

    @JsonProperty("iso")
    public void setIso(String iso) {
        this.iso = iso;
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