package com.ashes.web.project.model;

import com.ashes.web.project.dto.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String name;
    private String lastname;
    private String password;
    private String email;
    @OneToMany
    private List<Role> role;
    private LocalDate birthdate;
    private short age;
    private String phone;

}
