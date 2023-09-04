package com.example.restaurantreservationsystem.service;

import com.example.restaurantreservationsystem.constraints.UserRole;
import com.example.restaurantreservationsystem.domain.User;
import com.example.restaurantreservationsystem.exception.UserException;
import com.example.restaurantreservationsystem.repository.RestaurantRepository;
import com.example.restaurantreservationsystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.restaurantreservationsystem.exception.ErrorCode.BUSINESS_EXISTS;

@Service
public class UserService {
    private UserRepository userRepository;
    private  RestaurantRepository restaurantRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public boolean isRegistered(String UserId) {
        return userRepository.existsById(UserId);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public User saveUser(
            String userId, String name, String password,
            String phoneNumber, UserRole role )
    {
        User user = new User();
        user.setUserId(userId);
        user.setName(name);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return user;
    }

    public void updateUser(String userId,String name, String password,String phoneNumber){
        User user = userRepository.findAllById(userId);
        user.setName(name);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 사업주의 경우, 사업장을 모두 삭제해야 아이디 삭제 가능
     * @param userId
     */
    @Transactional
    public void deleteUser(String userId) {
        if(restaurantRepository.existByUserId(userId))
            throw new UserException( BUSINESS_EXISTS );
        userRepository.deleteAllById(userId);
    }


}
