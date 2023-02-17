package com.ashes.web.project.service;

import com.ashes.web.project.dto.AnimalDto;
import com.ashes.web.project.enumeration.PositionAnimalToShelter;
import com.ashes.web.project.model.Animal;
import com.ashes.web.project.model.Location;
import com.ashes.web.project.repository.AnimalRepository;
import com.ashes.web.project.service.interfaces.AnimalServiceInterface;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AnimalService implements AnimalServiceInterface {

    private final AnimalRepository animalRepository;
    private final LocationService locationService;

    @Override
    public ResponseEntity<String> saveAnimal(AnimalDto animalDto) {
        if (Stream.of(animalDto).allMatch(Objects::nonNull)) {
            try {
                Optional<Location> optionalLocation = locationService.getLocation(animalDto.getLocation());
                if (optionalLocation.isPresent()) {
                    Location location = optionalLocation.get();
                    if (locationService.isPlaceAvailable(location.getMaxCapacity(), location.getFilled())) {
                        locationService.addAnimalToLocation(location);
                        Optional<Animal> optionalAnimal = animalRepository.findByShelterIdentifier(animalDto.getShelterIdentifier());
                        if (optionalAnimal.isEmpty()) {
                            if (animalDto.getBirthdate().isBefore(LocalDate.now())) {
                                Animal animal = new Animal(animalDto, location);
                                animal.setPositionAnimalToShelter(PositionAnimalToShelter.POSITION_ANIMAL_TO_SHELTER_IN_SHELTER);
                                animalRepository.save(animal);
                                return ResponseEntity.ok().body("Animal information saved successfully.");
                            } else {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The date of birth cannot be in the future.");
                            }
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("An animal with this shelter ID already exists.");
                        }
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no available place in selected location.");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Location does not exists!");
                }
            } catch (DataAccessException e) {
                log.info("Error connection DB: \n" + e.getMessage());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Fail");
            } catch (PersistenceException e) {
                log.info("Persistence exception occurred while trying to save new animal");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error message.");
            }
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Required fields are not filled in!");
    }

    @Override
    public ResponseEntity<List<AnimalDto>> getAllAnimals() {
        try {
            return ResponseEntity.ok().body(animalRepository.findAllAndReturnDto());
        } catch (DataAccessException e) {
            log.info("Error connection DB: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Override
    public ResponseEntity<AnimalDto> getAnimalByShelterIdentifier(String shelterIdentifier) {
        if (shelterIdentifier == null || shelterIdentifier.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            return animalRepository.findByNameAndReturnDto(shelterIdentifier)
                    .map(animal -> ResponseEntity.ok().body(animal))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (DataAccessException e) {
            log.info("Error connection DB: \n" + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Override
    public ResponseEntity<List<AnimalDto>> getAllAnimalsByLocation(String location) {
        if (!locationService.isLocationExists(location)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            return ResponseEntity.ok().body(animalRepository.findAllByLocation(location));
        } catch (DataAccessException e) {
            log.info("Error connection DB: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Override
    public ResponseEntity<String> modifyAnimal(AnimalDto animalDto) {
        if (Stream.of(animalDto).allMatch(Objects::nonNull)) {
            try {
                Optional<Animal> optionalAnimal = animalRepository.findById(animalDto.getId());
                if (optionalAnimal.isPresent()) {
                    Animal animal = optionalAnimal.get();
                    if (!animal.getShelterIdentifier().equals(animalDto.getShelterIdentifier())) {
                        if (animalRepository.findByNameWithDifferentId(animalDto.getShelterIdentifier(), animal.getId()).isEmpty()) {
                            animal.setShelterIdentifier(animalDto.getShelterIdentifier());
                        } else {
                            return ResponseEntity.status(HttpStatus.FOUND).body("Animals name exists in system. Change name!");
                        }
                    }
                    if (!animal.getLocation().getName().equals(animalDto.getLocation())) {
                        Optional<Location> optionalLocation = locationService.getLocation(animalDto.getLocation());
                        if (optionalLocation.isPresent()) {
                            Location location = optionalLocation.get();
                            if (locationService.isPlaceAvailable(location.getMaxCapacity(), (location.getFilled()))) {
                                animal.setLocation(location);
                                locationService.addAnimalToLocation(location);
                            } else {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no available place in selected location.");
                            }
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Location not found.");
                        }
                    }
                    if (!animal.getBirthdate().equals(animalDto.getBirthdate())) {
                        if (animalDto.getBirthdate().isBefore(LocalDate.now())) {
                            animal.setBirthdate(animalDto.getBirthdate());
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The date of birth cannot be in the future.");
                        }
                    }
                    if (!checkAnimalStatus(animal.getPositionAnimalToShelter(), PositionAnimalToShelter.valueOf(animalDto.getStatus()))) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong Status change!!");
                    }
                    animalRepository.save(animal);
                    return ResponseEntity.ok().body("Success");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Animals with such name not found.");
                }
            } catch (DataAccessException e) {
                log.info("Error connection DB: \n" + e.getMessage());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Required fields are not filled in!");
            }
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Required fields are not filled in!");
    }

    public Boolean checkAnimalStatus(PositionAnimalToShelter oldPositionAnimalToShelter, PositionAnimalToShelter newPositionAnimalToShelter) {
        return switch (oldPositionAnimalToShelter) {
            case POSITION_ANIMAL_TO_SHELTER_NOT_AVAILABLE -> true;
            case POSITION_ANIMAL_TO_SHELTER_IN_SHELTER -> true;
            case POSITION_ANIMAL_TO_SHELTER_RESERVATION -> newPositionAnimalToShelter.equals(PositionAnimalToShelter.POSITION_ANIMAL_TO_SHELTER_ADOPTION) || newPositionAnimalToShelter.equals(PositionAnimalToShelter.POSITION_ANIMAL_TO_SHELTER_IN_SHELTER);
            case POSITION_ANIMAL_TO_SHELTER_ADOPTION -> newPositionAnimalToShelter.equals(PositionAnimalToShelter.POSITION_ANIMAL_TO_SHELTER_IN_SHELTER);
        };
    }
}