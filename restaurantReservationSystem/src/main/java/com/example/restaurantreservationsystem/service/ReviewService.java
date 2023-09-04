package com.example.restaurantreservationsystem.service;


import com.example.restaurantreservationsystem.domain.Reservation;
import com.example.restaurantreservationsystem.domain.Restaurant;
import com.example.restaurantreservationsystem.domain.Review;
import com.example.restaurantreservationsystem.domain.User;
import com.example.restaurantreservationsystem.exception.UserException;
import com.example.restaurantreservationsystem.repository.ReservationRepository;
import com.example.restaurantreservationsystem.repository.RestaurantRepository;
import com.example.restaurantreservationsystem.repository.ReviewRepository;
import com.example.restaurantreservationsystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.restaurantreservationsystem.exception.ErrorCode.*;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private UserRepository userRepository;
    private RestaurantRepository restaurantRepository;
    private ReservationRepository reservationRepository;
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public List<Review> EntireReview(){
        return reviewRepository.findAll();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Review registerReview(Integer reservationId, Integer rating, String comment){

        Restaurant restaurant = reservationRepository.findById(reservationId).get().getRestaurant();

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new UserException(RESERVATION_NOT_FOUND));

        User user = reservationRepository.findById(   reservationId   ).get().getUser();

        Review review = Review. create(rating, comment);

        restaurant.addReview(review);
        user.addReview(review);
        reservation.addReview(review);

        return reviewRepository.save(review);
    }

    @Transactional
    public Review updateReview(Integer reservationId, Integer rating, String comment){
        Restaurant restaurant = reservationRepository.findById(reservationId).get().getRestaurant();

        Review review = reservationRepository.findById(reservationId).get().getReview();

        review.update(rating,comment);

        restaurant.calRating();
        return reviewRepository.save(review);
    }

    @Transactional
    public Integer deleteReview(Integer reservationId){
        Restaurant restaurant = reservationRepository.findById(reservationId).get().getRestaurant();
        Review review = reservationRepository.findById(reservationId).get().getReview();

        restaurant.deleteReview(review);
        return reservationId;
    }

}
