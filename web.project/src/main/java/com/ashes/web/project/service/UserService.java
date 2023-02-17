package com.ashes.web.project.service;


import com.ashes.web.project.component.JwtProvider;
import com.ashes.web.project.domain.JwtRequest;
import com.ashes.web.project.domain.JwtResponse;
import com.ashes.web.project.dto.RoleDto;
import com.ashes.web.project.dto.UserDto;
import com.ashes.web.project.model.User;
import com.ashes.web.project.repository.UserRepository;
import com.ashes.web.project.service.interfaces.UserServiceInterface;
import jakarta.security.auth.message.AuthException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public ResponseEntity<JwtResponse> login(JwtRequest authRequest) throws AuthException {
        try {
            Optional<UserDto> userDto = getByLogin(authRequest.getLogin());
            if (userDto.get().getPassword().equals(authRequest.getPassword())) {
                return ResponseEntity.ok().body(new JwtResponse(jwtProvider.generateAccessToken(new User(userDto.get()))));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    @Override
    public Optional<UserDto> getByLogin(String login) {
        try {
            return userRepository.findByLogin(login);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public ResponseEntity<UserDto> getUserById(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<List<UserDto>> getAllUser() {
        return null;
    }

    @Override
    public ResponseEntity<String> registration(UserDto userDto) {
        return null;
    }

    @Override
    public ResponseEntity<List<UserDto>> getAllByRole(RoleDto roleDto) {
        return null;
    }

    @Override
    public ResponseEntity<String> changeRole(UserDto userDto, RoleDto newRoleDto) {
        return null;
    }

    @Override
    public ResponseEntity<UserDto> changeProfile(UserDto userDto) {
        return null;
    }
}