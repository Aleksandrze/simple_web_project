package com.ashes.web.project.service;

import com.ashes.web.project.dto.ReservationDto;
import com.ashes.web.project.service.interfaces.ReservationServiceInterface;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ReservationService implements ReservationServiceInterface {

    @Override
    public ResponseEntity<List<ReservationDto>> getAllReservationAndReturnDto() {
        return null;
    }

    @Override
    public ResponseEntity<String> saveReservation(ReservationDto reservationDto) {
        return null;
    }

    @Override
    public boolean deleteReservation(Long id) {
        return false;
    }
}
