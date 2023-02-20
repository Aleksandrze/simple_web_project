package com.ashes.web.project.controller;

import com.ashes.web.project.dto.LocationDto;
import com.ashes.web.project.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @PostMapping()
    public ResponseEntity<String> add(@RequestBody LocationDto locationDto){
        return locationService.saveLocation(locationDto);
    }

    @GetMapping()
    public ResponseEntity<List<LocationDto>> getAll() {
        return locationService.getAllLocations();
    }

    @GetMapping("/get/{name}")
    public ResponseEntity<LocationDto> getByName(@Valid @PathVariable("name") String name) {
        return locationService.getLocationByName(name);
    }

    @PatchMapping()
    public ResponseEntity<String> update(@RequestBody LocationDto locationDto){
        return locationService.modifyLocation(locationDto);
    }
}
