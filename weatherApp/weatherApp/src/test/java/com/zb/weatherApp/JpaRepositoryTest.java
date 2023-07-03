package com.zb.weatherApp;

import com.zb.weatherApp.domain.Memo;
import com.zb.weatherApp.repository.JpaMemoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
public class JpaRepositoryTest {
    @Autowired
    JpaMemoRepository jpaMemoRepository;

    @Test
    void insertMemoTest(){
        Memo newMemo = new Memo(10,"this is jpa");

        jpaMemoRepository.save(newMemo);

        List<Memo> memoList = jpaMemoRepository.findAll();
        assertTrue(memoList.size() > 0 );
    }

    @Test
    void findByIDTest(){
        Memo NewMemo = new Memo(11,"jps");
        Memo memo = jpaMemoRepository.save(NewMemo);
        Optional<Memo> result = jpaMemoRepository.findById(memo.getId());
        assertEquals(result.get().getText(),"jps");
    }
}
