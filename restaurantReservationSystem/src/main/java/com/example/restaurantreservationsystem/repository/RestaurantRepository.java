package com.example.restaurantreservationsystem.repository;


import com.example.restaurantreservationsystem.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant,Integer> {
    void deleteAllById(Integer id);
    List<Restaurant> findAllByShopName(String name);
    Optional<Restaurant> findById(Integer id);

    boolean existByUserId(String userId);
}
