package com.example.restaurantreservationsystem.repository;


import com.example.restaurantreservationsystem.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,String> {


    User findAllById(String userId);

    void deleteAllById(String userId);

}
