package com.zb.weatherApp.repository;

import com.zb.weatherApp.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryRepository extends
        JpaRepository<Diary, Integer> {
    @Transactional(readOnly = true)
    List<Diary> findAllByDate(LocalDate date);
    List<Diary> findAllByDateBetween(LocalDate startDate,LocalDate endDate);

    Diary getFirstByDate(LocalDate date);

    @Transactional
    void deleteAllByDate(LocalDate date);
}
