package com.example.restaurantreservationsystem.domain;

import com.example.restaurantreservationsystem.constraints.StoreStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
//@Entity
@NoArgsConstructor
public class Restaurant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer storeId;

    private String name;
    private String address;
    private double x; // 좌표를 계산하여, 현재 유저의 위치랑 가까운 순으로 나열
    private double y;

    private String description; // including greeting, menu
    private LocalDateTime open; //opening time
    private LocalDateTime close; //closing time ---  last reservation time : 1 hour bf closing time
    private StoreStatus status; //open vs close

    private String contacts;
    private Integer tableNum;
    private double ratings = 0.0;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void addReview(Review review){
        this.reviews.add(review);
        calRating();
    }

    public void calRating(){
        ratings = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);

    }

    public void deleteReview(Review review){
        this.reviews.remove(review);
        calRating();
    }


}
