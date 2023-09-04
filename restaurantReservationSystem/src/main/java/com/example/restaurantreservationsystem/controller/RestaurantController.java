package com.example.restaurantreservationsystem.controller;

import com.example.restaurantreservationsystem.domain.User;
import com.example.restaurantreservationsystem.exception.UserException;
import com.example.restaurantreservationsystem.domain.Restaurant;
import com.example.restaurantreservationsystem.repository.UserRepository;
import com.example.restaurantreservationsystem.service.RestaurantService;
import com.example.restaurantreservationsystem.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.restaurantreservationsystem.exception.ErrorCode.DUPLICATED_USER_ID;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final UserRepository userRepository;
    private final UserService userService;
    private User user;

    public RestaurantController(RestaurantService restaurantService, UserRepository userRepository, UserService userService) {
        this.restaurantService = restaurantService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/register")
//    @PreAuthorize("hasRole('PARTNER')")

    public void createRestaurant(
            @RequestParam String userId,
            @RequestParam String name,
            @RequestPart String address,
            @RequestPart double x,
            @RequestPart double y,
            @RequestPart boolean storeStatus,
            @RequestPart String description,
            @RequestPart LocalDateTime open,
            @RequestPart LocalDateTime close,
            @RequestPart String contacts,
            @RequestPart Integer tableNum
    ){

        if(!userService.isRegistered(user.getUserId()))
            restaurantService.registerRestaurant(userId, name, address, x, y, description,open,close,contacts,tableNum);
        else
            throw new UserException(DUPLICATED_USER_ID);
    }

    @GetMapping("/findAll") //모든 매장목록 검색
    public List<Restaurant> findAllShop(){
       return restaurantService.findAllShop();
    }

    @DeleteMapping("/delete")
    public void deleteRestaurant(@RequestPart Integer restaurantId){
        restaurantService.deleteRestaurant(restaurantId);
    }




}
