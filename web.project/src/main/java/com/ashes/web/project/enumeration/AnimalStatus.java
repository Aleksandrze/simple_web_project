package com.ashes.web.project.enumeration;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum AnimalStatus {
    // Change enums and descriptions
    ADOPTED_STATUS_ADOPTED("ADOPTED"),
    ANIMAL_STATUS_SHELTER("IN SHELTER"),
    ANIMAL_STATUS_RESERVATION("RESERVATION"),
    ANIMAL_STATUS_UNAVAILABLE("UNAVAILABLE");

    private final String status;

    public static AnimalStatus valueOfLabel(String label) {
        for (AnimalStatus e : values()) {
            if (e.status.equals(label)) {
                return e;
            }
        }
        return null;
    }

    public String getStatus() {
        return this.status;
    }
}
