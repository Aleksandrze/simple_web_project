package com.ashes.web.project.model;

import com.ashes.web.project.dto.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    @ManyToOne
    private Role role;

    /*private String email;
    private LocalDate birthdate;
    private short age;
    private String phone;*/

    public User(UserDto userDto) {
        this.id = userDto.getId();
        this.username = userDto.getUsername();
        this.password = userDto.getPassword();
        this.role = userDto.getRole();
        this.firstName = userDto.getFirstName();
        this.lastName = userDto.getLastName();
    }

}
