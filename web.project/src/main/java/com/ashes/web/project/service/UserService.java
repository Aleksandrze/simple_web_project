package com.ashes.web.project.service;


import com.ashes.web.project.component.JwtProvider;
import com.ashes.web.project.domain.JwtRequest;
import com.ashes.web.project.domain.JwtResponse;
import com.ashes.web.project.dto.RoleDto;
import com.ashes.web.project.dto.UserDto;
import com.ashes.web.project.model.User;
import com.ashes.web.project.repository.UserRepository;
import com.ashes.web.project.service.interfaces.UserServiceInterface;
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
    public ResponseEntity<String> saveUser(UserDto userDto) {
        // temporary realization
        userRepository.save(new User(userDto));
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Override
    public ResponseEntity<JwtResponse> login(JwtRequest authRequest) {
        if (!authRequest.getLogin().isEmpty() && !authRequest.getPassword().isEmpty()) {
            try {
                Optional<User> optionalUser = userRepository.findByLogin(authRequest.getLogin());
                if(optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    if (user.getPassword().equals(authRequest.getPassword())) {
                        return ResponseEntity.ok().body(new JwtResponse(jwtProvider.generateAccessToken(user)));
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }

            } catch (DataAccessException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


    @Override
    public Optional<User> getUserByLogin(String login) {
        try {
            return userRepository.findByLogin(login);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public ResponseEntity<UserDto> getUserByLoginAndReturnDto(String login) {
        if (!login.isEmpty()) {
            try {
                return userRepository.findByLoginAndReturnDto(login)
                        .map(userDto -> ResponseEntity.ok().body(userDto))
                        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
            } catch (DataAccessException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Override
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            return ResponseEntity.ok().body(userRepository.findAllAndReturnDtos());
        } catch (DataAccessException e) {
            log.info("Error connection DB: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Override
    public ResponseEntity<List<UserDto>> getAllUsesByRole(RoleDto roleDto) {
        if(roleDto != null){
           String username =  jwtProvider.decodeJwt(roleDto.getAccessToken());
           Optional<User> user = userRepository.findByLogin(username);
           if(user.isPresent()){
               if(!user.get().getRole().getName().equals("USER")){
                   return ResponseEntity.ok().body(userRepository.findAllByRoleAndReturnDtos(roleDto.getId()));
               }else {
                   return null;
               }
           }else {
               return null;
           }
        }
        return null;
    }

    @Override
    public ResponseEntity<String> changeUsersRole(UserDto userDto, RoleDto newRoleDto) {
        return null;
    }

    @Override
    public ResponseEntity<UserDto> modifyUser(UserDto userDto) {
        return null;
    }

}