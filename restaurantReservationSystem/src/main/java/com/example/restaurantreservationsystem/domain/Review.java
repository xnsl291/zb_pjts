package com.example.restaurantreservationsystem.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reviewId;

    private Integer rating;
    private String comment;

    @OneToOne
    @JoinColumn(name = "reservationId")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "restaurantId")
    private Restaurant restaurant;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static Review create(Integer rating, String comment){
        return Review.builder()
                .rating(rating)
                .comment(comment)
                .build();
    }

    public Review update(Integer rating, String comment){
        this.rating = rating;
        this.comment = comment;
        return this;
    }
}
