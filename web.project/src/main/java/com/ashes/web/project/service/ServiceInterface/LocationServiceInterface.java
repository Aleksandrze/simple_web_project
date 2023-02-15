package com.ashes.web.project.service.ServiceInterface;
import com.ashes.web.project.dto.AnimalDto;
import com.ashes.web.project.dto.LocationDto;
import com.ashes.web.project.model.Location;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface LocationServiceInterface {
    ResponseEntity<String> add(LocationDto locationDto);
    ResponseEntity<List<LocationDto>> getAll();
    ResponseEntity<LocationDto> getByName(String name);
    ResponseEntity<String> editLocation(LocationDto locationDto);
    Boolean checkExistLocation(String locationName);
    Boolean checkCapacity(Short currentSizeBox, Short currentFilled);

}
