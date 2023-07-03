package com.zb.weatherApp.repository;

import com.zb.weatherApp.domain.Memo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMemoRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcMemoRepository (DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Memo save(Memo memo)
    {
        String sql = "insert into memo values(?,?)";
        jdbcTemplate.update(sql,memo.getId(), memo.getText());
        return memo;
    }

    public List<Memo> findAll(){
        String sql = "select * from memo";
        return jdbcTemplate.query(sql, memoRowMapper());
    }

    public Optional<Memo> findById(int id){ //해당 아이디 값이 없을경우 null값 방지 위해 optional처리
        String sql = "select * from memo where id = ?";
        return jdbcTemplate.query(sql, memoRowMapper(),id).stream().findFirst();
    }
    private RowMapper<Memo> memoRowMapper(){
        return (rs, rowNum) -> new Memo(rs.getInt("id"),rs.getString("text"));
    }

}
