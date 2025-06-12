package com.chart.server;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.ui.Model;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


@RestController
@RequestMapping("/api")
public class WeatherData{

    String city;

    @Value("${weather.api.key}")
    String apiKey;

    @Value("${weather.api.url}")
    String apiURL;


    public void setCityName(String city){
        this.city = city;
    }
    public  ArrayList<Object> TranslateToList(JsonNode jsonNode, Object type){
        ArrayList <Object> List_ = new ArrayList<>();
        if(type.getClass().toString().equals("class java.lang.String")){
            for(JsonNode node : jsonNode){
                List_.add(node.asText());
            }
            return List_;
        }else if(type.getClass().toString().equals("class java.lang.Integer")){
            for(JsonNode node : jsonNode){
                List_.add(node.asInt());
            }
        }
        return List_;
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
        //api string
        String URL = String.format("%s/packages/basic-1h_basic-day?lat=23.3242&lon=42.6975&apikey=%s", apiURL, apiKey);

        WebClient.Builder builder = WebClient.builder();

        //api call
        JsonNode weatherData = builder.build()
                .get()
                .uri(URL)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        //Get all raw data needed
        String dayOfWeek = LocalDate.now().getDayOfWeek().toString();
        
        JsonNode dateTimeJsonList = weatherData.get("data_1h").get("time");
        JsonNode windSpeedJsonList = weatherData.get("data_1h").get("windspeed");
        JsonNode rainJsonList = weatherData.get("data_1h").get("rainspot");
        JsonNode temperatureJsonList = weatherData.get("data_1h").get("felttemperature");
        JsonNode humidityJsonList = weatherData.get("data_1h").get("relativehumidity");

        // Make data usable
        ArrayList<Object> dateTimeList = this.TranslateToList(dateTimeJsonList, "");
        ArrayList<Object> rainList = this.TranslateToList(rainJsonList, "");
        ArrayList<Object> windSpeedList = this.TranslateToList(windSpeedJsonList, 0);
        ArrayList<Object> temperatureList = this.TranslateToList(temperatureJsonList, 0);
        ArrayList<Object> humidityList = this.TranslateToList(humidityJsonList, 0);

        //get current formated date Time to use in compare
        String currDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH"));

        System.out.println(currDateTime);

        int index = 0;
        for(Object item : dateTimeList){
            String formatedItem = item.toString();
            if(formatedItem.substring(0, formatedItem.length()-3).equals(currDateTime)){

                break;
            }
            index++;
        }

        //Create response data object
        data.put("degrees", (int)temperatureList.get(index));
        data.put("rain", rainList.get(index).toString().substring(0, 1));
        data.put("humidity", (int)humidityList.get(index));
        data.put("wind", (int)windSpeedList.get(index));
        data.put("dow", dayOfWeek);
        data.put("time", dateTimeList.get(index).toString().split(" ")[1]);

        return ResponseEntity.ok(data);
    }

}