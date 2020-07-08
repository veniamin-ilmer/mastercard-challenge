package com.mastercard.challenge;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CityController {
    Connections connections = new Connections("city.txt");

    /**
     * Example: {@literal http://localhost:8080/connected?origin=Boston@&destination=Newark}
     * @param requestParams Expected to contain origin and destination.
     * @return if connencted, returns "yes", else returns "no"
     */
    @GetMapping("/connected")
    public String greeting(@RequestParam Map<String,String> requestParams) {
        if(connections.isConnected(requestParams.get("origin"), requestParams.get("destination"), null))
            return "yes";
        return "no";
    }
}
