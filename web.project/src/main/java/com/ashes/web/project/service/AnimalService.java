package com.ashes.web.project.service;

import com.ashes.web.project.component.JwtProvider;
import com.ashes.web.project.dto.AnimalDto;
import com.ashes.web.project.enumeration.AnimalStatus;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AnimalService implements AnimalServiceInterface {

    private final AnimalRepository animalRepository;
    private final LocationService locationService;
    private final JwtProvider jwtProvider;
    private final UserService userService;

    // modify SecurityConfig
    @Override
    public ResponseEntity<String> saveAnimal(AnimalDto animalDto) {
        if (animalDto != null) {
            if (!userService.checkUserPrivileges(jwtProvider.decodeJwt(animalDto.getAccessToken())).equals("USER")) {
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
                                    animal.setAnimalStatus(AnimalStatus.POSITION_ANIMAL_TO_SHELTER_IN_SHELTER);
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
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error message.");
    }

    @Override
    public ResponseEntity<List<AnimalDto>> getAllAnimals() {
        try {
            return ResponseEntity.ok().body(animalRepository.findAllAndReturnDtos());
        } catch (DataAccessException e) {
            log.info("Error connection DB: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // modify SecurityConfig
    @Override
    public ResponseEntity<AnimalDto> getAnimalByShelterIdentifier(String shelterIdentifier) {
        if (!shelterIdentifier.isEmpty()) {
            try {
                return animalRepository.findByNameAndReturnDto(shelterIdentifier)
                        .map(animal -> ResponseEntity.ok().body(animal))
                        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
            } catch (DataAccessException e) {
                log.info("Error connection DB: \n" + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // modify SecurityConfig
    @Override
    public ResponseEntity<List<AnimalDto>> getAllAnimalsByLocation(String location) {
        if (locationService.getLocation(location).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            return ResponseEntity.ok().body(animalRepository.findAllByLocationName(location));
        } catch (DataAccessException e) {
            log.info("Error connection DB: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // modify SecurityConfig
    @Override
    public ResponseEntity<String> modifyAnimal(AnimalDto animalDto) {
        if (animalDto != null) {
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
                    if (!checkAnimalStatus(animal.getAnimalStatus(), AnimalStatus.valueOf(animalDto.getStatus()))) {
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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error message.");
    }

    public Boolean checkAnimalStatus(AnimalStatus oldAnimalStatus, AnimalStatus newAnimalStatus) {
        return switch (oldAnimalStatus) {
            case POSITION_ANIMAL_TO_SHELTER_NOT_AVAILABLE -> true;
            case POSITION_ANIMAL_TO_SHELTER_IN_SHELTER -> true;
            case POSITION_ANIMAL_TO_SHELTER_RESERVATION -> newAnimalStatus.equals(AnimalStatus.ADOPTED_STATUS) || newAnimalStatus.equals(AnimalStatus.POSITION_ANIMAL_TO_SHELTER_IN_SHELTER);
            case ADOPTED_STATUS -> newAnimalStatus.equals(AnimalStatus.POSITION_ANIMAL_TO_SHELTER_IN_SHELTER);
        };
    }
}