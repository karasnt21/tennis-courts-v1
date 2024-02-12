package com.tenniscourts.reservations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.GuestService;
import com.tenniscourts.schedules.ScheduleService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final ReservationMapper reservationMapper;

    private final ScheduleService scheduleService;

    private final GuestService guestService;


    public ReservationDTO bookReservation(CreateReservationRequestDTO createReservationRequestDTO) {
        ReservationDTO reservationDTO = ReservationDTO.builder()
                .deposit(BigDecimal.valueOf(10))
                .price(BigDecimal.valueOf(100))
                .guest(guestService.findById(createReservationRequestDTO.getGuestId()))
                .schedule(scheduleService.findSchedule(createReservationRequestDTO.getScheduleId()))
                .reservationStatus(ReservationStatusEnum.READY_TO_PLAY.name())
                .build();
        return reservationMapper.map(reservationRepository.save(reservationMapper.map(reservationDTO)));
    }

    public ReservationDTO findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservationMapper::map).orElseThrow(() -> new EntityNotFoundException("Reservation not found."));
    }

    public ReservationDTO cancelReservation(Long reservationId) {
        return reservationMapper.map(this.cancel(reservationId));
    }

    private Reservation cancel(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservation -> {

            this.validateCancellation(reservation);

            BigDecimal refundValue = getRefundValue(reservation);
            return this.updateReservation(reservation, refundValue, ReservationStatusEnum.CANCELLED);

        }).orElseThrow(() -> new EntityNotFoundException("Reservation not found."));
    }

    private Reservation updateReservation(Reservation reservation, BigDecimal refundValue, ReservationStatusEnum status) {
        reservation.setReservationStatus(status);
        reservation.setPrice(reservation.getPrice().subtract(refundValue));
        reservation.setRefundValue(refundValue);

        return reservationRepository.save(reservation);
    }

    private void validateCancellation(Reservation reservation) {
        if (!ReservationStatusEnum.READY_TO_PLAY.equals(reservation.getReservationStatus())) {
            throw new IllegalArgumentException("Cannot cancel/reschedule because it's not in ready to play status.");
        }

        if (reservation.getSchedule().getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Can cancel/reschedule only future dates.");
        }
    }

    public BigDecimal getRefundValue(Reservation reservation) {
        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), reservation.getSchedule().getStartDateTime());

        if (hours >= 24) {
            return reservation.getPrice();
        }

        if (hours >= 12) {
            return reservation.getPrice().multiply(BigDecimal.valueOf(0.75));
        }

        if (hours >= 2) {
            return reservation.getPrice().multiply(BigDecimal.valueOf(0.50));
        }
        if (hours >= 0) {
            return reservation.getPrice().multiply(BigDecimal.valueOf(0.25));
        }
        return BigDecimal.ZERO;

    }


    public ReservationDTO rescheduleReservation(Long previousReservationId, Long scheduleId) {

        Reservation previousReservation = reservationMapper.map(findReservation(previousReservationId));

        if (scheduleId.equals(previousReservation.getSchedule().getId())) {
            throw new IllegalArgumentException("Cannot reschedule to the same slot.");
        }

        if (!ReservationStatusEnum.READY_TO_PLAY.equals(previousReservation.getReservationStatus())) {
            throw new IllegalArgumentException("Cannot reschedule: The reserve its not ready to play.");
        }

        BigDecimal refund = getRefundValue(previousReservation);

        previousReservation.setReservationStatus(ReservationStatusEnum.RESCHEDULED);
        previousReservation.setRefundValue(previousReservation.getPrice().subtract(refund));
        previousReservation.setPrice(BigDecimal.ZERO);
        previousReservation.setRefundValue(refund);
        reservationRepository.save(previousReservation);

        ReservationDTO reservation = bookReservation(
                CreateReservationRequestDTO.builder()
                        .guestId(previousReservation.getGuest().getId())
                        .scheduleId(scheduleId)
                        .build());

        reservation.setPreviousReservation(reservationMapper.map(previousReservation));

        return reservation;
    }
}
