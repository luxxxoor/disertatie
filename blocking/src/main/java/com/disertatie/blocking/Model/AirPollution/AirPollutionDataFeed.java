package com.disertatie.blocking.Model.AirPollution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AirPollutionDataFeed implements Serializable {
    private Integer aqi;
    private Integer idx;

    @JsonUnwrapped
    private List<AirPollutionDataAttributesFeed> attributions;
    private AirPollutionDataCityFeed city;

    public Integer getAqi() {
        return aqi;
    }

    public void setAqi(Integer aqi) {
        this.aqi = aqi;
    }

    public Integer getIdx() {
        return idx;
    }

    public void setIdx(Integer idx) {
        this.idx = idx;
    }

    public List<AirPollutionDataAttributesFeed> getAttributions() {
        return attributions;
    }

    public void setAttributions(List<AirPollutionDataAttributesFeed> attributions) {
        this.attributions = attributions;
    }

    public AirPollutionDataCityFeed getCity() {
        return city;
    }

    public void setCity(AirPollutionDataCityFeed city) {
        this.city = city;
    }
}
