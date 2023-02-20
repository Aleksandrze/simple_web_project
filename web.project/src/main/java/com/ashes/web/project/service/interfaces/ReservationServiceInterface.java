package com.ashes.web.project.service.interfaces;

import com.ashes.web.project.dto.AnimalDto;
import com.ashes.web.project.dto.UserDto;
import com.ashes.web.project.dto.ReservationDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ReservationServiceInterface {

    ResponseEntity<List<ReservationDto>> getAllReservationAndReturnDto();

    ResponseEntity<String> saveReservation(AnimalDto animalDto, UserDto userDto);

}
