package com.ashes.web.project.domain;

import com.ashes.web.project.dto.RoleDto;
import com.ashes.web.project.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleRequest {
    UserDto userDto;
    RoleDto roleDto;
}
