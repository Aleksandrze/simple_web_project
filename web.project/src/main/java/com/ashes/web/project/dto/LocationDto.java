package com.ashes.web.project.dto;

import com.ashes.web.project.model.Location;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class LocationDto {
    private Long id;
    private String name;
    private String description;
    private short maxNumbers;
    private short filled;

    public LocationDto(Location location) {
        this.id = location.getId();
        this.description = location.getDescription();
        this.name = location.getName();;
        this.filled = location.getFilled();
        this.maxNumbers = location.getMaxNumbers();
    }
}
