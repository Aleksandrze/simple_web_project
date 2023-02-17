package com.ashes.web.project.service.interfaces;
import com.ashes.web.project.dto.LocationDto;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface LocationServiceInterface {
    ResponseEntity<String> add(LocationDto locationDto);

    ResponseEntity<List<LocationDto>> getAll();

    ResponseEntity<LocationDto> getByName(String name);

    ResponseEntity<String> editLocation(LocationDto locationDto);

    boolean isLocationExists(String locationName);

    boolean isPlaceAvailable(short currentSizeBox, short currentFilled);

}
