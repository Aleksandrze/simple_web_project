package com.ashes.web.project.dto;

import com.ashes.web.project.model.Location;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private Long id;
    private String name;
    private String description;
    private short maxCapacity;
    private short filled;
    private String accessToken;

    public LocationDto(Location location) {
        this.id = location.getId();
        this.description = location.getDescription();
        this.name = location.getName();;
        this.filled = location.getFilled();
        this.maxCapacity = location.getMaxCapacity();
    }

}
