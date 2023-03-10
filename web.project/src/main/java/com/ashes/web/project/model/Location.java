package com.ashes.web.project.model;

import com.ashes.web.project.dto.LocationDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String name;
    private String description;
    private short maxCapacity;
    private short filled = 0;

    public Location(LocationDto locationDto){
        this.id = locationDto.getId();
        this.description = locationDto.getDescription();
        this.name = locationDto.getName();
        this.maxCapacity = locationDto.getMaxCapacity();
        this.filled = locationDto.getFilled();
    }
}
