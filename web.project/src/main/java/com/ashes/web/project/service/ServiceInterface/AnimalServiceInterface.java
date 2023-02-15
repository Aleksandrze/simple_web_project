package com.ashes.web.project.service.ServiceInterface;

import com.ashes.web.project.dto.AnimalDto;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface AnimalServiceInterface {

    ResponseEntity<String> add(AnimalDto animalDto);
    ResponseEntity<List<AnimalDto>> getAll();
    ResponseEntity<AnimalDto> getByName(String name);
    ResponseEntity<String> editProfile(AnimalDto animalDto);
    ResponseEntity<List<AnimalDto>> getAllByLocation(String location);

}
