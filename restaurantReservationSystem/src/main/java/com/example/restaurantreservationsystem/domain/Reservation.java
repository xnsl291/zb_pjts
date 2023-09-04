package com.example.restaurantreservationsystem.domain;

import com.example.restaurantreservationsystem.constraints.ReservationStatus;
import com.example.restaurantreservationsystem.constraints.StoreStatus;
import com.example.restaurantreservationsystem.exception.UserException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.restaurantreservationsystem.exception.ErrorCode.INVALID_TIME;


//@Entity
@Getter
@Setter
@NoArgsConstructor
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reservationId;

    @Enumerated(EnumType.STRING)
    private ReservationStatus approvalStatus;

    private Integer howMany; //인원 수
    private LocalDateTime reservationTime;
    private boolean visitedStatus; //10분 전 도착확인


    /**
     updatedAt
     defalut = createdAt
     update =  when status has changed from pending to approval/denied
     **/

    @ManyToOne
    @JoinColumn(name = "userId",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "restaurantId", nullable = false)
    private Restaurant restaurant;

    @OneToOne(mappedBy = "reservation")
    private Review review;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Review> reviews = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.approvalStatus = ReservationStatus.PENDING;
    }

    public void timeValidation(){
        // 시간형식 체크 (0~24사이)
        if (restaurant.getOpen().getHour() < 0 || restaurant.getOpen().getHour() > 24 ||
            restaurant.getClose().getHour() < 0 || restaurant.getClose().getHour() > 24)
            throw new UserException(INVALID_TIME);

        // 예약시간은 오픈시간과 마감시간 사이에
        if (restaurant.getOpen().isAfter(this.reservationTime) || restaurant.getClose().isBefore(this.reservationTime))
            throw new UserException(INVALID_TIME);
    }
    public boolean isValidReservaion(){
        timeValidation(); //시간형식체크

        // 마감 1시간 전 부터는 예약 불가
        if (this.reservationTime.plusHours(1).isAfter(restaurant.getClose())) {
            this.approvalStatus = ReservationStatus.DECLINED;
            this.updatedAt = LocalDateTime.now();
        }
        else {
            this.approvalStatus = ReservationStatus.APPROVAL;
            this.updatedAt = LocalDateTime.now();
            return true;
        }
        return false;
    }

    public void checkIn(){ // 10분전 체크인
        LocalDateTime now = LocalDateTime.now();
        int PREV_TIME = 10; //minutes

        if (this.reservationTime.minusMinutes(PREV_TIME).isBefore(now)) // 시간초과
            this.approvalStatus = ReservationStatus.DECLINED;
        else {
            this.visitedStatus = true;
            this.updatedAt = now;
        }
    }

    public void addReview(Review review){
        this.reviews.add(review);
    }
}
