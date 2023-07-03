package com.zb.weatherApp.repository;

import com.zb.weatherApp.domain.DateWeather;
import com.zb.weatherApp.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DateWeatherRepository extends JpaRepository<DateWeather, LocalDate> {
    List<DateWeather> findAllByDate(LocalDate date);
}
