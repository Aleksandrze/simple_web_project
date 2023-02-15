package com.ashes.web.project.model;

import com.ashes.web.project.dto.AnimalDto;
import com.ashes.web.project.enumeration.Status;
import jakarta.persistence.*;
import lombok.*;

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
    private String name;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
    private Status status;
    private String description;
    private LocalDate birthdate;
    //private String category;

    public Animal(AnimalDto animalDto, Location location) {
        this.id = animalDto.getId();
        this.name = animalDto.getName();
        this.location = location;
        this.status = Status.valueOf(animalDto.getStatus());
        this.description =animalDto.getDescription();
        this.birthdate = animalDto.getBirthdate();
    }


    public Animal convert(Animal animal, AnimalDto animalDto){
        animal.id = animalDto.getId();
        animal.name = animalDto.getName();;
        animal.status = Status.valueOf(animalDto.getStatus());
        animal.description =animalDto.getDescription();
        animal.birthdate = animalDto.getBirthdate();
        return animal;
    }


}
