package com.ashes.web.project.enumeration;

/*
IN_SHELTER -> RESERVATION
IN_SHELTER -> ADOPTION
IN_SHELTER -> NOT_AVAILABLE


RESERVATION -> ADOPTION
RESERVATION -> IN_SHELTER

ADOPTION -> IN_SHELTER

NOT_AVAILABLE X ALL
 */
public enum PositionAnimalToShelter {
    POSITION_ANIMAL_TO_SHELTER_ADOPTION("ADOPTION"),
    POSITION_ANIMAL_TO_SHELTER_IN_SHELTER("IN SHELTER"),
    POSITION_ANIMAL_TO_SHELTER_RESERVATION("RESERVATION"),
    POSITION_ANIMAL_TO_SHELTER_NOT_AVAILABLE("NOT AVAILABLE");

    private final String status;

    PositionAnimalToShelter(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
