package com.ashes.web.project.service.interfaces;


import com.ashes.web.project.domain.JwtRequest;
import com.ashes.web.project.domain.JwtResponse;
import com.ashes.web.project.dto.RoleDto;
import com.ashes.web.project.dto.UserDto;
import jakarta.security.auth.message.AuthException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface UserServiceInterface {

    ResponseEntity<JwtResponse> login(JwtRequest authRequest) throws AuthException;
    Optional<UserDto> getByLogin(String login);
    ResponseEntity<UserDto> getUserById(Long id);
    ResponseEntity<List<UserDto>> getAllUser();
    ResponseEntity<String> registration(UserDto userDto);
    ResponseEntity<List<UserDto>> getAllByRole(RoleDto roleDto);
    ResponseEntity<String> changeRole(UserDto userDto, RoleDto newRoleDto);
    ResponseEntity<UserDto> changeProfile(UserDto userDto);

    // ToDo resetPassword(String email);


}


