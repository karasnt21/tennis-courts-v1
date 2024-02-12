package com.tenniscourts.schedules;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tenniscourts.tenniscourts.TennisCourtDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ScheduleDTO {

    private Long id;

    private TennisCourtDTO tennisCourt;

    @NotNull
    private Long tennisCourtId;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm")
    @NotNull
    private LocalDateTime startDateTime;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDateTime;

}
