package com.ashes.web.project.controller;

import com.ashes.web.project.dto.AnimalDto;
import com.ashes.web.project.service.AnimalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/animals")
@RequiredArgsConstructor
public class AnimalController {
    private final AnimalService animalService;

    @PostMapping()
    public ResponseEntity<String> add( @RequestBody AnimalDto animalDto){
        return animalService.add(animalDto);
    }

    @GetMapping()
    public ResponseEntity<List<AnimalDto>> getAll() {
        return animalService.getAll();
    }

    @GetMapping("/get/{name}")
    public ResponseEntity<AnimalDto> getByName(@Valid @PathVariable("name") String name) {
        return animalService.getByName(name);
    }

    @GetMapping("/get/location/{location}")
    public ResponseEntity<List<AnimalDto>> getByLocation(@Valid @PathVariable("location") String location) {
        return animalService.getAllByLocation(location);
    }

    @PatchMapping()
    public ResponseEntity<String> update(@RequestBody AnimalDto animalDto){
        return animalService.editProfile(animalDto);
    }


}
