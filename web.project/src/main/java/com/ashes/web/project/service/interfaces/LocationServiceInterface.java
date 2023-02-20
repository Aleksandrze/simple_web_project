package com.ashes.web.project.service.interfaces;

import com.ashes.web.project.dto.LocationDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LocationServiceInterface {

    ResponseEntity<String> saveLocation(LocationDto locationDto);

    ResponseEntity<List<LocationDto>> getAllLocations();

    ResponseEntity<LocationDto> getLocationByName(String locationName);

    ResponseEntity<String> modifyLocation(LocationDto locationDto);

    boolean isPlaceAvailable(short currentSizeBox, short currentFilled);

}
