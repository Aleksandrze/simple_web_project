package com.ashes.web.project.service.interfaces;


import com.ashes.web.project.domain.JwtRequest;
import com.ashes.web.project.domain.JwtResponse;
import com.ashes.web.project.dto.RoleDto;
import com.ashes.web.project.dto.UserDto;
import com.ashes.web.project.model.User;
import jakarta.security.auth.message.AuthException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface UserServiceInterface {

    ResponseEntity<JwtResponse> login(JwtRequest authRequest) throws AuthException;
    Optional<User> getUserByLogin(String login);
    ResponseEntity<UserDto> getUserByLoginAndReturnDto(String login);
    ResponseEntity<List<UserDto>> getAllUsers();
    ResponseEntity<String> saveUser(UserDto userDto);
    ResponseEntity<List<UserDto>> getAllUsesByRole(RoleDto roleDto);
    ResponseEntity<String> changeUsersRole(UserDto userDto, RoleDto newRoleDto);
    ResponseEntity<UserDto> modifyUser(UserDto userDto);

    // ToDo resetPassword(String email);


}


