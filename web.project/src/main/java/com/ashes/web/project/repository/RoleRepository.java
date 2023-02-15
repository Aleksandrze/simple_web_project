package com.ashes.web.project.repository;

import com.ashes.web.project.dto.AnimalDto;
import com.ashes.web.project.dto.RoleDto;
import com.ashes.web.project.model.Location;
import com.ashes.web.project.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("select new com.ashes.web.project.dto.RoleDto(r) from Role a")
    List<RoleDto> findAllAndReturnDto();

    @Query("select r from Role r where r.id = ?1")
    Optional<Role> findById(Long id);
}