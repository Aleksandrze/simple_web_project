package com.ashes.web.project.dto;

import com.ashes.web.project.model.Role;
import com.ashes.web.project.model.User;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.stream.Stream;

@Data
@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    @OneToMany
    private Role role;
    private String accessToken;

    public UserDto(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();

    }

    public boolean anyNull(UserDto userDto){
        return Stream.of(userDto).anyMatch(Objects::isNull);
    }


}
