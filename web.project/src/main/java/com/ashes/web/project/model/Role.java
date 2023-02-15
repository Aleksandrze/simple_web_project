package com.ashes.web.project.model;

import com.ashes.web.project.dto.RoleDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    public Role(RoleDto roleDto){
        this.id = roleDto.getId();
        this.name = roleDto.getName();
    }
}
