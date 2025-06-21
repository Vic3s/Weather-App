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
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


@RestController
@RequestMapping("/api")
public class WeatherData {

    String cityName;

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    public String getCityName(){
        return cityName;
    }

    @Value("${weather.api.key}")
    String apiKey;

    @Value("${weather.api.url}")
    String apiURL;

    public String uriCurrDay(String city){
        return String.format("%sv1/current.json?key=%s&q=%s&aqi=no", apiURL, apiKey, city);
    }
    public String uriDays(String city, int days){
        return String.format("%sv1/forecast.json?key=%s&q=%s&days=%s&aqi=no&alerts=no", apiURL, apiKey, city, days);
    }

    public ArrayList<Object> TranslateToList(JsonNode jsonNode, Object type) {
        ArrayList<Object> List_ = new ArrayList<>();
        if (type.getClass().toString().equals("class java.lang.String")) {
            for (JsonNode node : jsonNode) {
                List_.add(node.asText());
            }
            return List_;
        } else if (type.getClass().toString().equals("class java.lang.Integer")) {
            for (JsonNode node : jsonNode) {
                List_.add(node.asInt());
            }
        }
        return List_;
    }

    @PostMapping("/post-city-name")
    public ResponseEntity<String> postCityName(@RequestBody CityName request){
        setCityName(request.getCityName());
        String cityName = getCityName();
        return ResponseEntity.ok(cityName);
    }
    @GetMapping("/curr-time-weather")
    public ResponseEntity<ObjectNode> getCurrentWeatherInfo() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        //api string
        String URL = "No Specified Value";
        if(getCityName() != null){
            URL = uriCurrDay(getCityName());
        }

        //Create the webclient builder
        WebClient.Builder builder = WebClient.builder();

        //api get request
        JsonNode weatherData = builder.build()
                .get()
                .uri(URL)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block().get("current");

        //Get all raw data needed
        String dayOfWeek = LocalDate.now().getDayOfWeek().toString();

        //Create data object for C°
        Integer currTemperature = weatherData.get("temp_c").asInt();
        Double rainMm = weatherData.get("precip_mm").asDouble();
        Integer humidity = weatherData.get("humidity").asInt();
        Double windSpeed = weatherData.get("wind_kph").asDouble();
        String time = weatherData.get("last_updated").asText().split(" ")[1];
        String condition = weatherData.get("condition").get("text").asText();

        response.put("degrees", currTemperature);
        response.put("rain", rainMm);
        response.put("humidity", humidity);
        response.put("wind", windSpeed);
        response.put("dow", dayOfWeek);
        response.put("time", time);
        response.put("condition", condition);


        return ResponseEntity.ok(response);
    }

    @GetMapping("/current-hourly-chart-data")
    public ResponseEntity<ArrayList<ObjectNode>> ChartData() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<ObjectNode> chartDataList = new ArrayList<>();

        //api string
        String URL = "No Specified Value";
        if(getCityName() != null){
            URL = uriDays(getCityName(), 1);
        }

        WebClient.Builder builder = WebClient.builder();

        JsonNode response = builder.build()
                .get()
                .uri(URL)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
        //get current time as a comparison variable
        Integer currTime = Integer.parseInt(LocalTime.now().toString().substring(0,2));

        //foreacst for day
        JsonNode forecastJsonList = response.get("forecast").get("forecastday").get(0).get("hour");

        int index = 0;
        for(JsonNode node : forecastJsonList){
            int nodeFiltered = Integer.parseInt(node.get("time").toString().split(" ")[1].substring(0, 2));
            if( nodeFiltered >= currTime){
                ObjectNode degNode = mapper.createObjectNode();
                degNode.put("degrees", (node.get("temp_c").asInt()));
                chartDataList.add(degNode);
                index++;
            }
            if(index == 7){
                break;
            }
        }

        return ResponseEntity.ok(chartDataList);
    }

    @GetMapping("/current-hours-data")
    public ResponseEntity<ArrayList<Integer>> HourlyChartData() {
        ArrayList<Integer> HoursChartDataList = new ArrayList<>();

        //api string
        String URL = "No Specified Value";
        if(getCityName() != null){
            URL = uriDays(getCityName(), 1);
        }

        WebClient.Builder builder = WebClient.builder();

        JsonNode response = builder.build()
                .get()
                .uri(URL)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
        //get current time as a comparison variable
        Integer currTime = Integer.parseInt(LocalTime.now().toString().substring(0,2));

        //foreacst for day
        JsonNode forecastJsonList = response.get("forecast").get("forecastday").get(0).get("hour");

        int index = 0;
        for(JsonNode node : forecastJsonList){
            int nodeFiltered = Integer.parseInt(node.get("time").toString().split(" ")[1].substring(0, 2));
            if( nodeFiltered >= currTime){
                HoursChartDataList.add(nodeFiltered);
                index++;
            }
            if(index == 7){
                break;
            }
        }
        return ResponseEntity.ok(HoursChartDataList);
    }
    @GetMapping("next-seven-days-data")
    ResponseEntity<ArrayList<ObjectNode>> NextSevenDays(){
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<ObjectNode> dataList = new ArrayList<>();

        WebClient.Builder builder = WebClient.builder();

        //api string
        String URL = "No Specified Value";
        if(getCityName() != null){
            URL = uriDays(getCityName(), 7);
        }

        //api get request
        JsonNode fullData = builder.
                build()
                .get()
                .uri(URL)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        //Neaded Data
        JsonNode dataDaysRaw = fullData.get("forecast").get("forecastday");

        //Json separate properties
        for(JsonNode node: dataDaysRaw){
            ObjectNode dataNode = mapper.createObjectNode();
            LocalDate Day = LocalDate.parse(node.get("date").asText());
            int maxTemp = node.get("day").get("maxtemp_c").asInt();
            int minTemp = node.get("day").get("mintemp_c").asInt();
            String weatherIcon = node.get("day").get("condition").get("icon").asText();

            dataNode.put("day", Day.getDayOfWeek().toString().substring(0,2));
            dataNode.put("maxTemp", maxTemp);
            dataNode.put("minTemp", minTemp);
            dataNode.put("weatherIcon", weatherIcon);
            dataList.add(dataNode);
        }

        return ResponseEntity.ok(dataList);
    }
}