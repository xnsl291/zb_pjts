package com.zb.weatherApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.zb.weatherApp.domain.Memo;

@Repository
public interface JpaMemoRepository extends JpaRepository<Memo,Integer> {

}
