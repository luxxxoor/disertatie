package com.disertatie.blocking.Model.AirPollution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AirPollutionFeed implements Serializable {
    private String status;
    private AirPollutionDataFeed data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AirPollutionDataFeed getData() {
        return data;
    }

    public void setData(AirPollutionDataFeed data) {
        this.data = data;
    }
}
