package com.example.restaurantreservationsystem.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.restaurantreservationsystem.constraints.UserRole;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
//@Entity
@NoArgsConstructor
public class User {
    @Column(unique = true)
    private String userId; //email
    private String name;
    private String password;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Review> reviews = new ArrayList<>();
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void addReview(Review review){
        this.reviews.add(review);
    }

}
