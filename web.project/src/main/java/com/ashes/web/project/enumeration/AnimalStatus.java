package com.ashes.web.project.enumeration;

import lombok.AllArgsConstructor;

/*
IN_SHELTER -> RESERVATION
IN_SHELTER -> ADOPTION
IN_SHELTER -> NOT_AVAILABLE


RESERVATION -> ADOPTION
RESERVATION -> IN_SHELTER

ADOPTION -> IN_SHELTER

NOT_AVAILABLE X ALL
 */
@AllArgsConstructor
public enum AnimalStatus {
    // Change enums and descriptions
    ADOPTED_STATUS("ADOPTED"),
    POSITION_ANIMAL_TO_SHELTER_IN_SHELTER("IN SHELTER"),
    POSITION_ANIMAL_TO_SHELTER_RESERVATION("RESERVATION"),
    POSITION_ANIMAL_TO_SHELTER_NOT_AVAILABLE("NOT AVAILABLE");

    private final String status;

    public String getStatus() {
        return this.status;
    }
}
