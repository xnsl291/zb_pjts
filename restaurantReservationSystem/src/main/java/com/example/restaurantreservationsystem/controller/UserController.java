package com.example.restaurantreservationsystem.controller;

import com.example.restaurantreservationsystem.constraints.UserRole;
import com.example.restaurantreservationsystem.domain.User;
import com.example.restaurantreservationsystem.exception.ErrorCode;
import com.example.restaurantreservationsystem.exception.UserException;
import com.example.restaurantreservationsystem.repository.UserRepository;
import com.example.restaurantreservationsystem.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.example.restaurantreservationsystem.exception.ErrorCode.PASSWORD_UNMATCHED;


@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private UserRepository userRepository;
    @PostMapping("/sign-up")
    public void SignUp(
            @RequestPart String userId,
            @RequestPart String name,
            @RequestPart String password,
            @RequestPart String phoneNumber,
            @RequestPart UserRole role
    ) {
        userService.saveUser(userId, name, password, phoneNumber,role);
    }

    @GetMapping("/login-in")
    public boolean SignIn(
            @RequestPart String userId,
            @RequestPart String password)
    {
        User tmpUser = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
        if (password.equals(tmpUser.getPassword()))
            return true;
        else
            throw new UserException(PASSWORD_UNMATCHED);
    }

    @PostMapping("/update")
    public void updateUser(String userId, @RequestParam String name, @RequestParam String password, @RequestParam String phoneNumber) {
        userService.updateUser(userId, name, password, phoneNumber);

    }

    @DeleteMapping("/delete")
    public void deleteUser(@RequestParam String userId) {
        userService.deleteUser(userId);
    }


}
