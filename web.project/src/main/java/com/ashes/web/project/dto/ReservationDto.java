package com.ashes.web.project.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReservationDto {
    private Long id;
    private String name;
    private String userUsername;
    private String animalName;
    private String locationName;
    private LocalDate dateReservation;
    private String employeeName;
    private String accessToken;
}
