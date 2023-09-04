package com.example.restaurantreservationsystem.controller;


import com.example.restaurantreservationsystem.domain.Review;
import com.example.restaurantreservationsystem.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private  ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/create") //리뷰 등록
    public void registerReview(@RequestPart Integer reservationId, @RequestPart Integer rating,  @RequestPart String comment){
        reviewService.registerReview(reservationId,rating,comment);
    }

    @GetMapping("/read")
    public List<Review> readReviews(){
        return reviewService.EntireReview();

    }




}