package com.ashes.web.project.dto;

import com.ashes.web.project.model.Animal;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
public class AnimalDto {
    private Long id;
    private  String name;
    private  String location;
    private  String status;
    private  String description;
    private  LocalDate birthdate;

    public AnimalDto(Animal animal) {
        this.id = animal.getId();
        this.name = animal.getName();
        this.location = animal.getLocation().getName();
        this.status = animal.getStatus().getStatus();
        this.description = animal.getDescription();
        this.birthdate = animal.getBirthdate();
    }
}
