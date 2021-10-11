package com.disertatie.blocking.Service;

import com.disertatie.blocking.Model.AirPollution.AirPollutionFeed;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AirPollutionService {
    private static final String AIR_POLLUTION_API_URL = "https://api.waqi.info/feed/";
    private static final String AIR_POLLUTION_API_TOKEN = "91c0bebff0a4cf0667902862781e9702f2908a88";
    private static final String AIR_POLLUTION_API_QUERY_TOKEN = "/?token=" + AIR_POLLUTION_API_TOKEN;

    public void cityFeed(final String cityName) {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = AIR_POLLUTION_API_URL + cityName + AIR_POLLUTION_API_QUERY_TOKEN;
        ResponseEntity<AirPollutionFeed> response = restTemplate.getForEntity(resourceUrl, AirPollutionFeed.class);
        System.out.println(response);
    }
}
