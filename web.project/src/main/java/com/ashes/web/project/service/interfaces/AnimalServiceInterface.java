package com.ashes.web.project.service.interfaces;

import com.ashes.web.project.dto.AnimalDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AnimalServiceInterface {

    ResponseEntity<String> saveAnimal(AnimalDto animalDto);

    ResponseEntity<List<AnimalDto>> getAllAnimals();

    ResponseEntity<AnimalDto> getAnimalByShelterIdentifier(String shelterIdentifier, String accessToken);

    ResponseEntity<String> modifyAnimal(AnimalDto animalDto);

    ResponseEntity<List<AnimalDto>> getAllAnimalsByLocation(String location, String accessToken);

}
