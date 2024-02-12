package com.tenniscourts.schedules;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.tenniscourts.TennisCourtMapper;
import com.tenniscourts.tenniscourts.TennisCourtRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;

    private final TennisCourtRepository tennisCourtRepository;

    private final TennisCourtMapper tennisCourtMapper;



    public ScheduleDTO addSchedule(Long tennisCourtId, CreateScheduleRequestDTO createScheduleRequestDTO) {
        List<Schedule> scheduleList = this.scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(tennisCourtId);

        for (Schedule schedule : scheduleList) {
            long hours = ChronoUnit.HOURS.between(createScheduleRequestDTO.getStartDateTime(), schedule.getStartDateTime());
            if (hours == 0)
                throw new AlreadyExistsEntityException("Schedule time is not available for given tennis court");
        }
        ScheduleDTO newSchedule = ScheduleDTO.builder()
                .tennisCourtId(tennisCourtId)
                .startDateTime(createScheduleRequestDTO.getStartDateTime())
                .endDateTime(createScheduleRequestDTO.getStartDateTime().plusHours(1L))
                .tennisCourt(tennisCourtRepository.findById(tennisCourtId).map(tennisCourtMapper::map).orElseThrow(() -> {
                    throw new EntityNotFoundException("Tennis Court not found.");
                }))
                .build();

        return scheduleMapper.map(scheduleRepository.saveAndFlush(scheduleMapper.map(newSchedule)));
    }

    public List<ScheduleDTO> findSchedulesByDates(LocalDateTime startDate, LocalDateTime endDate) {
        return this.scheduleMapper.map(this.scheduleRepository.findSchedulesByDates(startDate,endDate));
    }

    public ScheduleDTO findSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).map(scheduleMapper::map)
            .orElseThrow(() -> new EntityNotFoundException("Schedule " + scheduleId + " not found."));
    }

    public List<ScheduleDTO> findSchedulesByTennisCourtId(Long tennisCourtId) {
        return scheduleMapper.map(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(tennisCourtId));
    }

    public List<ScheduleDTO> findAllSlots() {
        return scheduleMapper.map(this.scheduleRepository.findAll());
    }
}
