package com.chart.server;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.ui.Model;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class WeatherData{

    String city;

    public void setCityName(String city){
        this.city = city;
    }

//    @PostMapping("/post-city-name")
//    public ResponseEntity<String> postCityName(@RequestBody CityName request){
//        String cityName = request.getCityName();
//        System.out.println(cityName);
//        return ResponseEntity.ok("Received: " + cityName);
//    }
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/curr-time-weather")
    public ResponseEntity<ObjectNode> getCurrentWeatherInfo(){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        String URL = "https://my.meteoblue.com/packages/basic-1h_basic-day?lat=23.3242&lon=42.6975&apikey=b8vrUCeDxJ2LdtWy";

        WebClient.Builder builder = WebClient.builder();

        JsonNode weatherData = builder.build()
                .get()
                .uri(URL)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        String dayOfWeek = LocalDate.now().getDayOfWeek().toString();
        String time = LocalTime.now().toString().substring(0,5);
        JsonNode timeSync = weatherData.get("data_1h").get("time");

        Iterator<Map.Entry<String, JsonNode>> fields = timeSync.fields();

        while(fields.hasNext()){
            Map.Entry<String, JsonNode> entry = fields.next();
//            String i = entry.getValue();
        }


        data.put("degrees", 24);
        data.put("rain", "TestString");
        data.put("humidity", "TestString");
        data.put("wind", "TestString");
        data.put("dow", dayOfWeek);
        data.put("time", time);
        data.put("condition", "TestString");



        return ResponseEntity.ok(data);
    }

}