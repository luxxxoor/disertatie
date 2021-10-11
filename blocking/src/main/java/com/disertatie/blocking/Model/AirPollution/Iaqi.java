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
        "h",
        "no2",
        "p",
        "pm10",
        "t",
        "w",
        "wg"
})
@Generated("jsonschema2pojo")
public class Iaqi {

    @JsonProperty("h")
    private H h;
    @JsonProperty("no2")
    private No2 no2;
    @JsonProperty("p")
    private P p;
    @JsonProperty("pm10")
    private Pm10 pm10;
    @JsonProperty("t")
    private T t;
    @JsonProperty("w")
    private W w;
    @JsonProperty("wg")
    private Wg wg;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("h")
    public H getH() {
        return h;
    }

    @JsonProperty("h")
    public void setH(H h) {
        this.h = h;
    }

    @JsonProperty("no2")
    public No2 getNo2() {
        return no2;
    }

    @JsonProperty("no2")
    public void setNo2(No2 no2) {
        this.no2 = no2;
    }

    @JsonProperty("p")
    public P getP() {
        return p;
    }

    @JsonProperty("p")
    public void setP(P p) {
        this.p = p;
    }

    @JsonProperty("pm10")
    public Pm10 getPm10() {
        return pm10;
    }

    @JsonProperty("pm10")
    public void setPm10(Pm10 pm10) {
        this.pm10 = pm10;
    }

    @JsonProperty("t")
    public T getT() {
        return t;
    }

    @JsonProperty("t")
    public void setT(T t) {
        this.t = t;
    }

    @JsonProperty("w")
    public W getW() {
        return w;
    }

    @JsonProperty("w")
    public void setW(W w) {
        this.w = w;
    }

    @JsonProperty("wg")
    public Wg getWg() {
        return wg;
    }

    @JsonProperty("wg")
    public void setWg(Wg wg) {
        this.wg = wg;
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