package com.ashes.web.project.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationDto {
    private  String name;
    private  String userUsername;
    private  String animalName;
    private  LocalDate dateReservation;
    private  LocalDate dateAdaptation;
    private  String locationName;
}
