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
        ObjectNode data = mapper.createObjectNode();
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


        Integer currTemperature = weatherData.get("temp_c").asInt();
        Double rainMm = weatherData.get("precip_mm").asDouble();
        Integer humidity = weatherData.get("humidity").asInt();
        Double windSpeed = weatherData.get("wind_kph").asDouble();
        String time = weatherData.get("last_updated").asText().split(" ")[1];
        String condition = weatherData.get("condition").get("text").asText();

        //Create response data object
        data.put("degrees", currTemperature);
        data.put("rain", rainMm);
        data.put("humidity", humidity);
        data.put("wind", windSpeed);
        data.put("dow", dayOfWeek);
        data.put("time", time);
        data.put("condition", condition);

        return ResponseEntity.ok(data);
    }

//    @GetMapping("/current-hourly-chart-data")
//    public ResponseEntity<ArrayList<Integer>> ChartData() {
//        ObjectMapper mapper = new ObjectMapper();
//        ArrayList<Integer> chartDataList = new ArrayList<>();
//
//        //api string
//        String URL = "No Specified Value";
//        if(getCityName() != null){
//            URL = uriDays(getCityName(), 1);
//        }
//
//        WebClient.Builder builder = WebClient.builder();
//
//        JsonNode fulldataRequest = builder.build()
//                .get()
//                .uri(URL)
//                .retrieve()
//                .bodyToMono(JsonNode.class)
//                .block();
//
//
//        return ResponseEntity.ok(chartDataList);
//    }
//
//    @GetMapping("/current-hours-data")
//    public ResponseEntity<ArrayList<String>> HourlyChartData() {
//        ObjectMapper mapper = new ObjectMapper();
//        ArrayList<String> HoursChartDataList = new ArrayList<>();
//
//        //api string
//        String URL = String.format("%s%s%s", apiURL, uriDay, apiKey);
//
//        WebClient.Builder builder = WebClient.builder();
//
//        //api call
//        JsonNode fullDataJson = builder.build()
//                .get()
//                .uri(URL)
//                .retrieve()
//                .bodyToMono(JsonNode.class)
//                .block();
//
//        String currTime = LocalTime.now().toString().substring(0, 2);
//
//        //Needed json data from request
//        //Getting the time data to map from current hour to the next 7 hours skipping by 3
//        JsonNode dateTimeJson = fullDataJson.get("data_1h").get("time");
//
//        //Mapped json data to array
//        ArrayList<Object> dateTimeList = this.TranslateToList(dateTimeJson, "");
//
//        for (int i = 0; i < dateTimeList.size(); i++) {
//            String formatedItem = dateTimeList.get(i).toString().split(" ")[1].substring(0, 2);
//
//            if (formatedItem.equals(currTime)) {
//                for (int j = 0; j < 7; j++) {
//                    HoursChartDataList
//                            .add(dateTimeList
//                                    .get(i+j)
//                                    .toString()
//                                    .split(" ")[1]);
//                }
//                break;
//            }
//        }
//        return ResponseEntity.ok(HoursChartDataList);
//    }
//    @GetMapping("next-seven-days-data")
//    ResponseEntity<ArrayList<ObjectNode>> NextSevenDays(){
//        ObjectMapper mapper = new ObjectMapper();
//        ArrayList<ObjectNode> dataList = new ArrayList<>();
//
//        WebClient.Builder builder = WebClient.builder();
//
//        //full url
//        String URL = String.format("%s%s%s", apiURL, uriWeek, apiKey);
//
//        //api get request
//        JsonNode fullData = builder.
//                build()
//                .get()
//                .uri(URL)
//                .retrieve()
//                .bodyToMono(JsonNode.class)
//                .block();
//
//        //Neaded Data
//        JsonNode dataDay = fullData.get("data_day");
//
//        //Json separate properties
//        JsonNode daysJson = dataDay.get("time");
//        JsonNode maxTempJson = dataDay.get("temperature_max");
//        JsonNode minTempJson = dataDay.get("temperature_min");
//
//        //Translate properties in to Java readable format
//        ArrayList<Object> daysList = TranslateToList(daysJson, "");
//        ArrayList<Object> maxTempList = TranslateToList(maxTempJson, 0);
//        ArrayList<Object> minTempList = TranslateToList(minTempJson, 0);
//
//        //Format the date to a first two letters of day of the week
//        ArrayList<String> daysLetters = new ArrayList<>();
//        for(Object item : daysList){
//            LocalDate Day = LocalDate.parse(item.toString());
//            daysLetters.add(Day.getDayOfWeek().toString().substring(0,2));
//        }
//
//        //fill response with data
//        for(int i=0; i<7; i++){
//            ObjectNode dataObject = mapper.createObjectNode();
//            dataObject.put("day", daysLetters.get(i));
//            dataObject.put("deg_hi", (int)maxTempList.get(i));
//            dataObject.put("deg_lo", (int)minTempList.get(i));
//            dataList.add(dataObject);
//        }
//
//        return ResponseEntity.ok(dataList);
//    }
}