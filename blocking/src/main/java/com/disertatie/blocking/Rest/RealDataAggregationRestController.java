package com.disertatie.blocking.Rest;

import com.disertatie.blocking.Service.AirPollutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import static org.springframework.http.HttpStatus.OK;

@Controller
public class RealDataAggregationRestController {
    private AirPollutionService airPollutionService;

    @Autowired
    public RealDataAggregationRestController(AirPollutionService airPollutionService) {
        this.airPollutionService = airPollutionService;
    }

    @GetMapping("/ceva")
    public ResponseEntity<Void> ceva() {
        airPollutionService.cityFeed("bucharest");
        return new ResponseEntity<>(OK);
    }
}
