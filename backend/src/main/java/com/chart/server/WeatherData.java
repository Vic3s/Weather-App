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

    String city;

    @Value("${weather.api.key}")
    String apiKey;

    @Value("${weather.api.url}")
    String apiURL;

    String uriDay = "/packages/basic-1h_?lat=42.6975&lon=23.3242&apikey=";
    String uriWeek = "/packages/basic-day_?lat=42.6975&lon=23.3242&apikey=";

    public void setCityName(String city) {
        this.city = city;
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

    //    @PostMapping("/post-city-name")
//    public ResponseEntity<String> postCityName(@RequestBody CityName request){
//        String cityName = request.getCityName();
//        System.out.println(cityName);
//        return ResponseEntity.ok("Received: " + cityName);
//    }
//    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/curr-time-weather")
    public ResponseEntity<ObjectNode> getCurrentWeatherInfo() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        //api string
        String URL = String.format("%s%s%s", apiURL, uriDay, apiKey);

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

        int index = 0;
        for (Object item : dateTimeList) {
            String formatedItem = item.toString();
            if (formatedItem.substring(0, formatedItem.length() - 3).equals(currDateTime)) {

                break;
            }
            index++;
        }

        //Create response data object
        data.put("degrees", (int) temperatureList.get(index));
        data.put("rain", rainList.get(index).toString().substring(0, 1));
        data.put("humidity", (int) humidityList.get(index));
        data.put("wind", (int) windSpeedList.get(index));
        data.put("dow", dayOfWeek);
        data.put("time", dateTimeList.get(index).toString().split(" ")[1]);

        return ResponseEntity.ok(data);
    }

    @GetMapping("/current-hourly-chart-data")
    public ResponseEntity<ArrayList<ObjectNode>> ChartData() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<ObjectNode> chartDataList = new ArrayList<>();

        //api string
        String URL = String.format("%s%s%s", apiURL, uriDay, apiKey);

        WebClient.Builder builder = WebClient.builder();

        //api call
        JsonNode fullDataJson = builder.build()
                .get()
                .uri(URL)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        String currTime = LocalTime.now().toString().substring(0, 2);

        //Needed json data from request
        JsonNode chartDataJson = fullDataJson.get("data_1h").get("felttemperature");
        //Getting the time data to map from current hour to the next 7 hours skipping by 3
        JsonNode dateTimeJson = fullDataJson.get("data_1h").get("time");

        //Mapped json data to array
        ArrayList<Object> dateTimeList = this.TranslateToList(dateTimeJson, "");
        ArrayList<Object> temperatureList = this.TranslateToList(chartDataJson, 0);

        int index = 0;
        for (Object item : dateTimeList) {
            String formatedItem = item.toString().split(" ")[1].substring(0, 2);
            if (Integer.parseInt(formatedItem) == Integer.parseInt(currTime)) {
                for (int i = 0; i < 7; i++) {
                    ObjectNode chartNode = mapper.createObjectNode();
                    chartNode.put("degrees", (int) temperatureList.get(index));
                    chartDataList.add(chartNode);
                    index++;
                }
                break;
            }
            index++;
        }

        return ResponseEntity.ok(chartDataList);
    }

    @GetMapping("/current-hours-data")
    public ResponseEntity<ArrayList<Integer>> HourlyChartData() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Integer> HoursChartDataList = new ArrayList<>();

        //api string
        String URL = String.format("%s%s%s", apiURL, uriDay, apiKey);

        WebClient.Builder builder = WebClient.builder();

        //api call
        JsonNode fullDataJson = builder.build()
                .get()
                .uri(URL)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        String currTime = LocalTime.now().toString().substring(0, 2);

        //Needed json data from request
        //Getting the time data to map from current hour to the next 7 hours skipping by 3
        JsonNode dateTimeJson = fullDataJson.get("data_1h").get("time");

        //Mapped json data to array
        ArrayList<Object> dateTimeList = this.TranslateToList(dateTimeJson, "");

        for (int i = 0; i < dateTimeList.size(); i++) {
            String formatedItem = dateTimeList.get(i).toString().split(" ")[1].substring(0, 2);

            if (Integer.parseInt(formatedItem) == Integer.parseInt(currTime)) {
                for (int j = 0; j < 7; j++) {
                    HoursChartDataList
                            .add(Integer.parseInt(dateTimeList
                                    .get(i+j)
                                    .toString()
                                    .split(" ")[1]));
                }
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

        //full url
        String URL = String.format("%s%s%s", apiURL, uriWeek, apiKey);

        //api get request
        JsonNode fullData = builder.
                build()
                .get()
                .uri(URL)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        //Neaded Data
        JsonNode dataDay = fullData.get("data_day");

        //Json separate properties
        JsonNode daysJson = dataDay.get("time");
        JsonNode maxTempJson = dataDay.get("temperature_max");
        JsonNode minTempJson = dataDay.get("temperature_min");

        //Translate properties in to Java readable format
        ArrayList<Object> daysList = TranslateToList(daysJson, "");
        ArrayList<Object> maxTempList = TranslateToList(maxTempJson, 0);
        ArrayList<Object> minTempList = TranslateToList(minTempJson, 0);

        //Format the date to a first two letters of day of the week
        ArrayList<String> daysLetters = new ArrayList<>();
        for(Object item : daysList){
            LocalDate Day = LocalDate.parse(item.toString());
            daysLetters.add(Day.getDayOfWeek().toString().substring(0,2));
        }

        //fill response with data
        for(int i=0; i<7; i++){
            ObjectNode dataObject = mapper.createObjectNode();
            dataObject.put("day", daysLetters.get(i));
            dataObject.put("deg_hi", (int)maxTempList.get(i));
            dataObject.put("deg_lo", (int)minTempList.get(i));
            dataList.add(dataObject);
        }

        return ResponseEntity.ok(dataList);
    }
}