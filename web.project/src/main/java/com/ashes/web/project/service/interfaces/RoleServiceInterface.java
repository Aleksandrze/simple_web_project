package com.ashes.web.project.service.interfaces;

import com.ashes.web.project.dto.RoleDto;
import com.ashes.web.project.model.Role;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RoleServiceInterface {

    ResponseEntity<List<RoleDto>> getAllRoles();

    Boolean isRoleExists(Long id);
    // temp
    ResponseEntity<Role> saveRole(Role Role);
}
