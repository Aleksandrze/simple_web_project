package com.ashes.web.project.model;

import com.ashes.web.project.dto.AnimalDto;
import com.ashes.web.project.enumeration.AnimalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String shelterIdentifier;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
    private AnimalStatus animalStatus;
    private String description;
    private LocalDate birthdate;
    //private String category;
    private String firstName="-";

    public Animal(AnimalDto animalDto, Location location) {
        this.id = animalDto.getId();
        this.shelterIdentifier = animalDto.getShelterIdentifier();
        this.location = location;
        this.animalStatus = AnimalStatus.valueOf(animalDto.getStatus());
        this.description = animalDto.getDescription();
        this.birthdate = animalDto.getBirthdate();
        this.firstName = animalDto.getFirstName();
    }


}
