package com.zb.weatherApp;

import com.zb.weatherApp.domain.Memo;
import com.zb.weatherApp.repository.JdbcMemoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class JdbcMemoRepositoryTest {

    @Autowired
    JdbcMemoRepository jdbcMemoRepository;

    @Test
    void insertMemoTest(){
        Memo newMemo = new Memo(2,"ahahaha");
        jdbcMemoRepository.save(newMemo);
    }

}
