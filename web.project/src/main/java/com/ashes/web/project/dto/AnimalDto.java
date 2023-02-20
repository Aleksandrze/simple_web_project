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
    private String shelterIdentifier;
    private String location;
    private String status;
    private String description;
    private LocalDate birthdate;
    private String firstName;
    private String accessToken;

    public AnimalDto(Animal animal) {
        this.id = animal.getId();
        this.shelterIdentifier = animal.getShelterIdentifier();
        this.location = animal.getLocation().getName();
        this.status = animal.getAnimalStatus().getStatus();
        this.description = animal.getDescription();
        this.birthdate = animal.getBirthdate();
        this.firstName = animal.getFirstName();
    }
}
