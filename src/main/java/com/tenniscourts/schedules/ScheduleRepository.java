package com.tenniscourts.schedules;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByTennisCourt_IdOrderByStartDateTime(Long tennisCourtId);


    @Query(value = "select * from schedule where start_date_time between :startDate and :endDate",nativeQuery = true)
    List<Schedule> findSchedulesByDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}