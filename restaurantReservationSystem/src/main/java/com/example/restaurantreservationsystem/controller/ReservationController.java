package com.example.restaurantreservationsystem.controller;

import com.example.restaurantreservationsystem.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/reservation")
@AllArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/create")
    public void createReservation(
            @RequestPart Integer restaurantId,
            @RequestPart String userId,
            @RequestPart Integer howMany,
            @RequestPart LocalDateTime reservationTime) {
        reservationService.makeReservation(howMany, reservationTime, userId,restaurantId);

    }

    @PostMapping("/cancel")
    public void cancelReservation(
            @RequestPart Integer reservationId) {
        reservationService.cancel(reservationId);
    }

    @PostMapping("/update")
    public void updateReservation(
            @RequestPart Integer reservationId,
            @RequestPart Integer howMany,
            @RequestPart LocalDateTime reservationTime ) {
        reservationService.update(reservationId,howMany,reservationTime);
    }

    @GetMapping("/search")
    public void searchReservation(@RequestPart Integer reservationId){
        reservationService.searchReservation(reservationId);
    }

}
