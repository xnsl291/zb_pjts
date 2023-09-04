package com.example.restaurantreservationsystem.service;


import com.example.restaurantreservationsystem.constraints.ReservationStatus;
import com.example.restaurantreservationsystem.domain.Reservation;
import com.example.restaurantreservationsystem.domain.Restaurant;
import com.example.restaurantreservationsystem.domain.User;
import com.example.restaurantreservationsystem.exception.UserException;
import com.example.restaurantreservationsystem.repository.ReservationRepository;
import com.example.restaurantreservationsystem.repository.RestaurantRepository;
import com.example.restaurantreservationsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.restaurantreservationsystem.exception.ErrorCode.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {
    private ReservationRepository reservationRepository;
    private  UserRepository userRepository;
    private RestaurantRepository restaurantRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Reservation makeReservation(
            Integer howMany,
            LocalDateTime reservationTime,
            String userId,
            Integer restaurantId
    ){
        User user = this.userRepository.findById(userId).orElseThrow(() -> new UserException(USER_NOT_FOUND));
        Restaurant restaurant = this.restaurantRepository.findById(restaurantId).orElseThrow(() -> new UserException(RESTAURANT_NOT_FOUND));

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRestaurant(restaurant);
        reservation.setHowMany(howMany);
        reservation.setReservationTime(reservationTime);
        reservation.setCreatedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
        return reservation;
    }

    @Transactional
    public Reservation cancel(Integer reservationId){
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new UserException(RESERVATION_NOT_FOUND));
        reservationRepository.deleteById(reservationId);
        return reservation;
    }


    @Transactional
    public Reservation approve (Integer reservationId){
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(()->new UserException(RESERVATION_NOT_FOUND));
        var status = (reservation.isValidReservaion()==true) ? ReservationStatus.APPROVAL: ReservationStatus.DECLINED;

        reservation.setApprovalStatus(status);
        return reservation;
    }


    @Transactional
    public Reservation searchReservation(Integer reservationId){
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new UserException(RESERVATION_NOT_FOUND));
        return reservation;
    }

    @Transactional
    public Reservation update(Integer reservationId, Integer howMany, LocalDateTime reservationTime){
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new UserException(RESERVATION_NOT_FOUND));
        reservation.setHowMany(howMany);
        reservation.setReservationTime(reservationTime);
        return reservation;
    }


}
