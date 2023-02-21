package com.ashes.web.project.controller;

import com.ashes.web.project.domain.JwtRequest;
import com.ashes.web.project.domain.JwtResponse;
import com.ashes.web.project.domain.UserRoleRequest;
import com.ashes.web.project.dto.RoleDto;
import com.ashes.web.project.dto.UserDto;
import com.ashes.web.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping()
    public ResponseEntity<String> add(@RequestBody UserDto userDto) {
        return userService.saveUser(userDto);
    }

    @GetMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest authRequest) {
        return userService.login(authRequest);
    }

    @GetMapping()
    public ResponseEntity<UserDto> getByName(@RequestParam String name) {
        return userService.getUserByLoginAndReturnDto(name);
    }

    @GetMapping("/role")
    public ResponseEntity<List<UserDto>> getByRole(@RequestBody RoleDto roleDto) {
        return userService.getAllUsesByRole(roleDto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAll() {
        return userService.getAllUsers();
    }

    @PutMapping("/role")
    public ResponseEntity<String> update(@RequestBody UserRoleRequest userRoleRequest) {
        return userService.changeUsersRole(userRoleRequest.getUserDto(), userRoleRequest.getRoleDto());
    }

    @PutMapping()
    public ResponseEntity<UserDto> update(@RequestBody UserDto userDto) {
        return userService.modifyUser(userDto);
    }

}