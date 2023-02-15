package com.ashes.web.project.service;

import com.ashes.web.project.dto.AnimalDto;
import com.ashes.web.project.dto.LocationDto;
import com.ashes.web.project.enumeration.Status;
import com.ashes.web.project.model.Animal;
import com.ashes.web.project.model.Location;
import com.ashes.web.project.repository.AnimalRepository;
import com.ashes.web.project.repository.LocationRepository;
import com.ashes.web.project.service.ServiceInterface.AnimalServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    public ResponseEntity<String> add(AnimalDto animalDto) {
        if (Stream.of(animalDto).allMatch(Objects::nonNull)) {
            try {
                Optional<AnimalDto> optionalAnimals = animalRepository.findByNameAndReturnDto(animalDto.getName());
                if (optionalAnimals.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.FOUND).body("An animal with this name already exists!");
                }
                if (!locationService.checkExistLocation(animalDto.getLocation())) {
                    return ResponseEntity.status(HttpStatus.FOUND).body("Location no exists!");
                }
                Optional<LocationDto> optionalLocationDto = locationService.getLocationDto(animalDto.getLocation());
                if (!locationService.checkCapacity(optionalLocationDto.get().getMaxNumbers(), (short) (optionalLocationDto.get().getFilled() + 1))) {
                    return ResponseEntity.status(HttpStatus.FOUND).body("Required fields are not filled in!!");
                }else {
                    locationService.addNewAnimalsForLocation(new Location(optionalLocationDto.get()),  (short) (optionalLocationDto.get().getFilled() + 1));
                }
                optionalAnimals.get().setStatus(Status.STATUS_IN_SHELTER.getStatus());
                animalRepository.save(new Animal(animalDto, new Location(optionalLocationDto.get())));
                return ResponseEntity.ok().body("Success");
            } catch (DataAccessException e) {
                log.info("Error connection DB: \n" + e.getMessage());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Fail");
            } catch (ArithmeticException e) {
                log.info("ArithmeticException:  \n" + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ArithmeticException");
            }
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Required fields are not filled in!");
    }

    @Override
    public ResponseEntity<List<AnimalDto>> getAll() {
        try {
            return ResponseEntity.ok().body(animalRepository.findAllAndReturnDto());
        } catch (DataAccessException e) {
            log.info("Error connection DB: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Override
    public ResponseEntity<AnimalDto> getByName(String name) {
        if (name == null || name.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            Optional<AnimalDto> optionalAnimal = animalRepository.findByNameAndReturnDto(name);
            if (optionalAnimal.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FOUND).build();
            }
            return ResponseEntity.ok().body(optionalAnimal.get());
        } catch (DataAccessException e) {
            log.info("Error connection DB: \n" + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Override
    public ResponseEntity<List<AnimalDto>> getAllByLocation(String location) {
        if (!locationService.checkExistLocation(location)) {
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
    public ResponseEntity<String> editProfile(AnimalDto animalDto) {
        if (Stream.of(animalDto).allMatch(Objects::nonNull)) {
            try {
                Optional<Animal> oldOptionalAnimal = animalRepository.findById(animalDto.getId());
                if (oldOptionalAnimal.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Animals name NOT_FOUND in system!");
                } else {
                    Animal oldAnimal = oldOptionalAnimal.get();
                    Animal editAnimal = new Animal();
                    if (!oldAnimal.getName().equals(animalDto.getName())) {
                        if (animalRepository.findByNameWithDifferentId(animalDto.getName(), oldAnimal.getId()).isEmpty()) {
                            editAnimal = editAnimal.convert(editAnimal, animalDto);
                        } else {
                            return ResponseEntity.status(HttpStatus.FOUND).body("Animals name exists in system. Change name!");
                        }
                    }
                    if(!oldAnimal.getLocation().getName().equals(animalDto.getName())){
                        if(locationService.checkExistLocation(animalDto.getLocation())){
                            Optional<LocationDto> newOptionalLocation = locationService.getLocationDto(animalDto.getLocation());
                            if(locationService.checkCapacity(newOptionalLocation.get().getMaxNumbers(), (short) (newOptionalLocation.get().getFilled()+1))){
                                editAnimal.setLocation(new Location(newOptionalLocation.get()));
                                locationService.addNewAnimalsForLocation(new Location(newOptionalLocation.get()),  (short) (newOptionalLocation.get().getFilled() + 1));
                            } else {
                                return ResponseEntity.status(HttpStatus.FOUND).body("Required fields are not filled in!!");
                            }
                        }else{
                            return ResponseEntity.status(HttpStatus.FOUND).body("Location no exists!");
                        }
                    }
                    if(!checkAnimalStatus(oldAnimal.getStatus(), Status.valueOf(animalDto.getStatus()))){
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong Status change!!");
                    }
                    animalRepository.save(editAnimal.convert(editAnimal, animalDto));
                    return ResponseEntity.ok().body("Success");
                }
            } catch (DataAccessException e) {
                log.info("Error connection DB: \n" + e.getMessage());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Required fields are not filled in!");
            }
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Required fields are not filled in!");
    }

    public Boolean checkAnimalStatus(Status oldStatus, Status newStatus){
        return switch (oldStatus) {
            case STATUS_NOT_AVAILABLE -> false;
            case STATUS_IN_SHELTER -> true;
            case STATUS_RESERVATION -> newStatus.equals(Status.STATUS_ADOPTION) || newStatus.equals(Status.STATUS_IN_SHELTER);
            case STATUS_ADOPTION -> newStatus.equals(Status.STATUS_IN_SHELTER);
        };
    }
}