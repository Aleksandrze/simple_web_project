package com.ashes.web.project.dto;

import com.ashes.web.project.dto.RoleDto;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserDto {
    private  String username;
    private  String name;
    private  String lastname;
    private  String email;
    private  String phone;
    private  short age;
}
