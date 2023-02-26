package com.ashes.web.project.service;


import com.ashes.web.project.component.JwtProvider;
import com.ashes.web.project.domain.JwtRequest;
import com.ashes.web.project.domain.JwtResponse;
import com.ashes.web.project.dto.RoleDto;
import com.ashes.web.project.dto.UserDto;
import com.ashes.web.project.model.Role;
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
    private final RoleService roleService;
    private final JwtProvider jwtProvider;

    @Override
    public ResponseEntity<String> saveUser(UserDto userDto) {
        if (userDto != null) {
            try {
                if (userRepository.findByUsername(userDto.getUsername()).isEmpty()) {
                    if (!userDto.anyNull(userDto)) {
                        User newUser = new User(userDto);
                        newUser.setRole(roleService.getRoleByName("USER"));
                        userRepository.save(newUser);
                        return ResponseEntity.status(HttpStatus.OK).body("Success");
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.FOUND).build();
                }
            } catch (DataAccessException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Override
    public ResponseEntity<JwtResponse> login(JwtRequest authRequest) {
        if (!authRequest.getLogin().isEmpty() && !authRequest.getPassword().isEmpty()) {
            try {
                Optional<User> optionalUser = userRepository.findByUsername(authRequest.getLogin());
                if (optionalUser.isPresent()) {
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
    public Optional<User> getUserByUsername(String username) {
        try {
            return userRepository.findByUsername(username);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public ResponseEntity<UserDto> getUserByUsernameAndReturnDto(String username) {
        if (!username.isEmpty()) {
            try {
                return userRepository.findByLoginAndReturnDto(username)
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
        if (roleDto != null) {
            String username = jwtProvider.decodeJwt(roleDto.getAccessToken());
            try {
                Optional<User> user = userRepository.findByUsername(username);
                if (user.isPresent()) {
                    if (!user.get().getRole().getName().equals("USER")) {
                        return ResponseEntity.ok().body(userRepository.findAllByRoleAndReturnDtos(roleDto.getId()));
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
            } catch (DataAccessException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Override
    public ResponseEntity<String> changeUsersRole(UserDto userDto, RoleDto newRoleDto) {
        if (userDto != null && newRoleDto != null) {
            String adminUser = jwtProvider.decodeJwt(userDto.getAccessToken());
            try {
                Optional<User> optionalAdminUser = userRepository.findByUsername(adminUser);
                if (optionalAdminUser.isPresent()) {
                    if (optionalAdminUser.get().getRole().getName().equals("ADMIN")) {
                        Optional<User> optionalUser = userRepository.findByUsername(userDto.getUsername());
                        if (optionalUser.isPresent() && !optionalUser.get().getUsername().equals(optionalAdminUser.get().getUsername())) {
                            User modifyUser = optionalUser.get();
                            modifyUser.setRole(new Role(newRoleDto));
                            userRepository.save(modifyUser);
                            return ResponseEntity.status(HttpStatus.OK).build();
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                        }
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
            } catch (DataAccessException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Override
    public ResponseEntity<UserDto> modifyUser(UserDto userDto) {
        if (userDto != null) {
            try {
                Optional<User> optionalUser = userRepository.findById(userDto.getId());
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    if (!user.getUsername().equals(userDto.getUsername())) {
                        if (userRepository.findByNameWithDifferentId(userDto.getUsername(), user.getId()).isEmpty()) {
                            user.setUsername(userDto.getUsername());
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                        }
                    }
                    userRepository.save(user);
                    return ResponseEntity.ok().body(new UserDto(user));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
            } catch (DataAccessException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    public String checkUserPrivileges(String accessToken) {
        String adminUser = jwtProvider.decodeJwt(accessToken);
        if (!adminUser.isEmpty()) {
            try {
                Optional<User> optionalUser = userRepository.findByUsername(adminUser);
                if (optionalUser.isPresent()) {
                    return optionalUser.get().getRole().getName();
                } else {
                    return "USER";
                }
            } catch (DataAccessException e) {
                return "USER";
            }
        }
        return "USER";
    }
}