package com.ashes.web.project.model;

import com.ashes.web.project.dto.LocationDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private String name;
    private String description;
    private short maxCapacity;
    private short filled;

    public Location(LocationDto locationDto){
        this.id = locationDto.getId();
        this.description = locationDto.getDescription();
        this.name = locationDto.getName();
        this.maxCapacity = locationDto.getMaxCapacity();
        this.filled = locationDto.getFilled();
    }

    public Location convert(Location location, LocationDto locationDto){
        location.id = locationDto.getId();
        location.description = locationDto.getDescription();
        location.name = locationDto.getName();
        location.maxCapacity = locationDto.getMaxCapacity();
        location.filled = locationDto.getFilled();
        return location;
    }
}
