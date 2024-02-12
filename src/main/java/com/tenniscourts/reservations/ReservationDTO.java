package com.tenniscourts.reservations;

import java.math.BigDecimal;

import com.tenniscourts.guests.GuestDTO;
import com.tenniscourts.schedules.ScheduleDTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
public class ReservationDTO {

    private Long id;

    private ScheduleDTO schedule;

    private GuestDTO guest;

    private String reservationStatus;

    private ReservationDTO previousReservation;

    private BigDecimal refundValue;

    private BigDecimal price;

    private BigDecimal deposit;


    @NotNull
    private Long scheduledId;

    @NotNull
    private Long guestId;
}
