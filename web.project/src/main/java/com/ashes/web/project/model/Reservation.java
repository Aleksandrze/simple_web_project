package com.ashes.web.project.model;

import com.ashes.web.project.dto.ReservationDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToOne
    @JoinColumn(name = "animals_id")
    private Animal animal;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
    private LocalDate dateReservation;
    private String employeeName;

    public Reservation(ReservationDto reservationDto, User user, Animal animal, Location location){
        this.id = reservationDto.getId();
        this.name = reservationDto.getName();
        this.user = user;
        this.animal = animal;
        this.location = location;
        this.dateReservation = reservationDto.getDateReservation();
        this.employeeName = this.getEmployeeName();
    }
}
