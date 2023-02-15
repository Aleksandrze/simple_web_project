package com.ashes.web.project.service;

import com.ashes.web.project.dto.RoleDto;
import com.ashes.web.project.model.Role;
import com.ashes.web.project.repository.RoleRepository;
import com.ashes.web.project.service.ServiceInterface.RoleServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoleService  implements RoleServiceInterface {

    private final RoleRepository roleRepository;

    @Override
    public ResponseEntity<List<RoleDto>> getAll() {
        try {
            return ResponseEntity.ok().body(roleRepository.findAllAndReturnDto());
        } catch (DataAccessException e) {
            log.info("Error connection DB: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Override
    public Boolean checkExistRole(Long id) {
        if (id == null) {
            return false;
        }
        try {
            return roleRepository.findById(id).isPresent();
        } catch (DataAccessException e) {
            log.info("Error connection DB: \n" + e.getMessage());
            return false;
        }
    }

    // temp method
    @Override
    public ResponseEntity<Role> add(Role role) {
        return ResponseEntity.ok().body(roleRepository.save(role));
    }
}
