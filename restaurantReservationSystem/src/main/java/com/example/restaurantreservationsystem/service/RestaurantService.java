package com.example.restaurantreservationsystem.service;

import com.example.restaurantreservationsystem.constraints.StoreStatus;
import com.example.restaurantreservationsystem.constraints.UserRole;
import com.example.restaurantreservationsystem.domain.Restaurant;
import com.example.restaurantreservationsystem.exception.ErrorCode;
import com.example.restaurantreservationsystem.exception.UserException;
import com.example.restaurantreservationsystem.repository.RestaurantRepository;
import com.example.restaurantreservationsystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.restaurantreservationsystem.exception.ErrorCode.NO_AUTHORITY;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private UserRepository userRepository;
    private final UserService userService;

    public RestaurantService(RestaurantRepository restaurantRepository, UserService userService, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userService = userService;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Restaurant registerRestaurant(
            String userId,
            String name,
            String address,
            double x,
            double y,
            String description,
            LocalDateTime open,
            LocalDateTime close,
            String contacts,
            Integer tableNum
    ) {
        Restaurant restaurant = new Restaurant();
        restaurant.setUser( userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND)) );
        restaurant.setName(name);
        restaurant.setAddress(address);
        restaurant.setX(x);
        restaurant.setY(y);
        restaurant.setDescription(description);
        restaurant.setOpen(open);
        restaurant.setClose(close);
        restaurant.setContacts(contacts);
        restaurant.setTableNum(tableNum);
        restaurant.setStatus(StoreStatus.CLOSED);
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant.setUpdatedAt(LocalDateTime.now());
        restaurantRepository.save(restaurant);

        return restaurant;
    }

    @Transactional
    public List<Restaurant> findAllShop() {
        return restaurantRepository.findAll();
    }

    @Transactional
    public void deleteRestaurant(Integer restaurantId) {
        // role이 Partner인 경우
        if (restaurantRepository.findById(restaurantId).orElseThrow(() -> new UserException(ErrorCode.RESTAURANT_NOT_FOUND)).getUser().getRole() == UserRole.PARTNER)
            restaurantRepository.deleteAllById(restaurantId);
        else
            throw new UserException(NO_AUTHORITY);

    }

}
