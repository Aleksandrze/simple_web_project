package com.ashes.web.project.service;

import com.ashes.web.project.dto.LocationDto;
import com.ashes.web.project.model.Location;
import com.ashes.web.project.repository.LocationRepository;
import com.ashes.web.project.service.interfaces.LocationServiceInterface;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LocationService implements LocationServiceInterface {

    private final LocationRepository locationRepository;

    @Override
    public ResponseEntity<String> saveLocation(LocationDto locationDto) {
        if (locationDto != null) {
            try {
                Optional<Location> optionalLocation = locationRepository.findByName(locationDto.getName());
                if (optionalLocation.isEmpty()) {
                    if (locationDto.getMaxCapacity() > 0) {
                        locationRepository.save(new Location(locationDto));
                        return ResponseEntity.ok().body("Success");
                    } else {
                        return ResponseEntity.status(HttpStatus.FOUND).body("The storage size cannot be less than 1");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.FOUND).body("Location exists!");
                }
            } catch (PersistenceException e) {
                log.info("Persistence exception occurred while trying to save new location");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error message.");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error message.");
    }

    @Override
    public ResponseEntity<List<LocationDto>> getAllLocations() {
        try {
            return ResponseEntity.ok().body(locationRepository.findAllAndReturnDtos());
        } catch (DataAccessException e) {
            log.info("Error connection DB: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Override
    public ResponseEntity<LocationDto> getLocationByName(String locationName) {
        if (!locationName.isEmpty()) {
            try {
                return locationRepository.findByNameAndReturnDto(locationName)
                        .map(locationDto -> ResponseEntity.ok().body(locationDto))
                        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
            } catch (DataAccessException e) {
                log.info("Error connection DB: \n" + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Override
    public ResponseEntity<String> modifyLocation(LocationDto locationDto) {
        if (locationDto != null) {
            try {
                Optional<Location> optionalLocation = locationRepository.findById(locationDto.getId());
                if (optionalLocation.isPresent()) {
                    Location location = optionalLocation.get();
                    if (!location.getName().equals(locationDto.getName())) {
                        if (locationRepository.findByNameWithDifferentId(locationDto.getName(), location.getId()).isEmpty()) {
                            location.setName(locationDto.getName());
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("It is not possible to change the name. This name is already in use");
                        }
                    }
                    if (location.getMaxCapacity() != locationDto.getMaxCapacity() && location.getFilled() < locationDto.getMaxCapacity()) {
                        location.setMaxCapacity(locationDto.getMaxCapacity());
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("It is not possible to change the number of seats. Exceeded the limit of animals in the location.");
                    }
                    locationRepository.save(location);
                    return ResponseEntity.ok().body("Location modified successfully");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The changeable location does not exist.");
                }
            } catch (DataAccessException e) {
                log.info("Error connection DB: \n" + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fail");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error message.");
    }


    @Override
    public boolean isPlaceAvailable(short maxCapacity, short currentFilled) {
        return maxCapacity > currentFilled;
    }

    public void addAnimalToLocation(Location location) {
        location.setFilled((short) (location.getFilled() + 1));
        locationRepository.save(location);
    }

    public Optional<Location> getLocation(String locationName) {
        if (locationName != null || !locationName.isEmpty()) {
            return locationRepository.findByName(locationName);
        }
        return Optional.empty();
    }


}
