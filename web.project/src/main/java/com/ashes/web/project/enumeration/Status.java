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
public enum Status {
    STATUS_ADOPTION("ADOPTION"),
    STATUS_IN_SHELTER("IN SHELTER"),
    STATUS_RESERVATION("RESERVATION"),
    STATUS_NOT_AVAILABLE("NOT AVAILABLE");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
