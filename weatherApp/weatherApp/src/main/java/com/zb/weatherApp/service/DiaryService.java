package com.zb.weatherApp.service;

import com.zb.weatherApp.domain.DateWeather;
import com.zb.weatherApp.domain.Diary;
import com.zb.weatherApp.repository.DateWeatherRepository;
import com.zb.weatherApp.repository.DiaryRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DiaryService {
    @Value("${openweathermap.key}")
    private String apiKey;

    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;
    public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) {
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        //날씨 데이터 가지고 오기
        DateWeather dateWeather = getDateWeather(date);

        Diary diary = new Diary();
        diary.setDateWeather(dateWeather);
        diary.setText(text);
        diaryRepository.save(diary);
    }

    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
        if(dateWeatherListFromDB.size() == 0 )
        {
            // 새로 api에서 날씨 정보 가지고 오기 -> 현재 날씨 가지고 오기
            return getWeatherFromApi();
        }
        else
            return dateWeatherListFromDB.get(0);
    }

    private String getWeatherString(){
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid="+apiKey;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            BufferedReader br;
            if (responseCode == 200)
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            else
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));

            String inputLine;
            StringBuilder response = new StringBuilder();
            while((inputLine = br.readLine())!= null)
                response.append(inputLine);
            br.close();

            return response.toString();

        } catch (IOException e) {
            return "failed to get response";
        }
    }

    private Map<String, Object> parseWeather(String jsonString){
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try{
            jsonObject = (JSONObject) jsonParser.parse(jsonString);

        }catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> resultMap =new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp",mainData.get("temp"));

        JSONArray weatherArray = (JSONArray)jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main",weatherData.get("main"));
        resultMap.put("icon",weatherData.get("icon"));
        return resultMap;

    }
    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        return diaryRepository.findAllByDate(date);
    }

    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate,endDate);
    }

    public void updateDiary(LocalDate date, String text) {
        Diary diary = diaryRepository.getFirstByDate(date);
        diary.setText(text);
        diaryRepository.save(diary);
    }

    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }

    private DateWeather getWeatherFromApi(){
        String weatherData = getWeatherString();
        Map<String,Object> parsedWeather = parseWeather(weatherData);
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((double) parsedWeather.get("temp"));
        return dateWeather;
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate(){
        dateWeatherRepository.save(getWeatherFromApi());
    }

}
