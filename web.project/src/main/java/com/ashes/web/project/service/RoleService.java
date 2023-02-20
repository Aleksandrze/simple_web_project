package com.ashes.web.project.service;

import com.ashes.web.project.dto.RoleDto;
import com.ashes.web.project.model.Role;
import com.ashes.web.project.repository.RoleRepository;
import com.ashes.web.project.service.interfaces.RoleServiceInterface;
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
public class RoleService implements RoleServiceInterface {

    private final RoleRepository roleRepository;

    @Override
    // name
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        try {
            return ResponseEntity.ok().body(roleRepository.findAllAndReturnDtos());
        } catch (DataAccessException e) {
            log.info("Error connection DB: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Override
    public Boolean isRoleExists(Long id) {
        try {
            return Optional.ofNullable(id).map(roleId -> roleRepository.findById(roleId).isPresent()).orElse(false);
        } catch (DataAccessException e) {
            log.info("Error connection DB: \n" + e.getMessage());
            return false;
        }
    }

    // temp method
    @Override
    public ResponseEntity<Role> saveRole(Role tRole) {
        return ResponseEntity.ok().body(roleRepository.save(tRole));
    }

}
