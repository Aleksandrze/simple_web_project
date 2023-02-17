package com.ashes.web.project.service;

import com.ashes.web.project.dto.LocationDto;
import com.ashes.web.project.model.Location;
import com.ashes.web.project.repository.LocationRepository;
import com.ashes.web.project.service.interfaces.LocationServiceInterface;
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
public class LocationService implements LocationServiceInterface {
    private final LocationRepository locationRepository;

    // ToDo change parameter in checkExistLocation Name -> id
    @Override
    public ResponseEntity<String> add(LocationDto locationDto) {
        if (Stream.of(locationDto).allMatch(Objects::nonNull)) {
            if (isLocationExists(locationDto.getName())) {
                return ResponseEntity.status(HttpStatus.FOUND).body("Location exists!");
            }
            try {
                Location newLocation = new Location(locationDto);
                newLocation.setFilled((short) 0);
                locationRepository.save(newLocation);
                return ResponseEntity.ok().body("Success");
            } catch (DataAccessException e) {
                log.info("Error connection DB: \n" + e.getMessage());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Fail");
            }
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Required fields are not filled in!");
    }

    @Override
    public ResponseEntity<List<LocationDto>> getAll() {
        try {
            return ResponseEntity.ok().body(locationRepository.findAllAndReturnDto());
        } catch (DataAccessException e) {
            log.info("Error connection DB: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Override
    public ResponseEntity<LocationDto> getByName(String name) {
        if (name == null || name.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if (!isLocationExists(name)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok().body(locationRepository.findByNameAndReturnDto(name).get());
        } catch (DataAccessException e) {
            log.info("Error connection DB: \n" + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


    @Override
    public ResponseEntity<String> editLocation(LocationDto locationDto) {
        if (Stream.of(locationDto).allMatch(Objects::nonNull)) {
            if (!isLocationExists(locationDto.getName())) {
                return ResponseEntity.status(HttpStatus.FOUND).body("Location exists!");
            }
            try {
                Optional<Location> oldOptionalLocation = locationRepository.findById(locationDto.getId());
                if (oldOptionalLocation.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Location name NOT_FOUND in system!");
                } else {
                    Location oldLocation = oldOptionalLocation.get();
                    Location editLocation = new Location();
                    if (!oldLocation.getName().equals(locationDto.getName())) {
                        if (locationRepository.findByNameWithDifferentId(locationDto.getName(), oldLocation.getId()).isEmpty()) {
                            editLocation = editLocation.convert(editLocation, locationDto);
                        } else {
                            return ResponseEntity.status(HttpStatus.FOUND).body("Location name exists in system. Change name!");
                        }
                    }
                    if (oldLocation.getMaxCapacity() != locationDto.getMaxCapacity() && !isPlaceAvailable(locationDto.getMaxCapacity(), oldLocation.getFilled())) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Changing the size of the box is not possible. Exceeded the number of animals in it!");
                    }
                    locationRepository.save(editLocation.convert(editLocation, locationDto));
                    return ResponseEntity.ok().body("Success");
                }

            } catch (DataAccessException e) {
                log.info("Error connection DB: \n" + e.getMessage());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Fail");
            }
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Required fields are not filled in!");
    }

    @Override
    public boolean isLocationExists(String locationName) {
        if (locationName == null || locationName.isEmpty()) {
            return false;
        }
        return locationRepository.findByName(locationName).isPresent();
    }

    @Override
    public boolean isPlaceAvailable(short maxCapacity, short currentFilled) {
        return maxCapacity > currentFilled;
    }

    public Optional<LocationDto> getLocationDto(String nameLocation) {
        return locationRepository.findByNameAndReturnDto(nameLocation);
    }

    public void addAnimalToLocation(Location location) {
        location.setFilled((short) (location.getFilled()+1));
        locationRepository.save(location);
    }

    public Optional<Location> getLocation(String locationName) {
        if (locationName != null || !locationName.isEmpty()) {
            return locationRepository.findByName(locationName);
        }
        return Optional.empty();
    }


}
