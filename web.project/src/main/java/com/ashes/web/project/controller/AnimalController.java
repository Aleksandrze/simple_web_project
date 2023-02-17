package com.ashes.web.project.controller;

import com.ashes.web.project.dto.AnimalDto;
import com.ashes.web.project.service.AnimalService;
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
        return animalService.saveAnimal(animalDto);
    }

    @GetMapping()
    public ResponseEntity<List<AnimalDto>> getAll() {
        return animalService.getAllAnimals();
    }

    @GetMapping("/get")
    public ResponseEntity<AnimalDto> getByName(@RequestParam String name) {
        return animalService.getAnimalByShelterIdentifier(name);
    }

    @GetMapping("/get/location")
    public ResponseEntity<List<AnimalDto>> getByLocation(@RequestParam String location) {
        return animalService.getAllAnimalsByLocation(location);
    }

    @PutMapping()
    public ResponseEntity<String> update(@RequestBody AnimalDto animalDto){
        return animalService.modifyAnimal(animalDto);
    }


}
