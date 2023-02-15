package com.ashes.web.project.dto;

import com.ashes.web.project.model.Role;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class RoleDto {
    private Long id;
    private  String name;

    public RoleDto(Role Role){
        this.id = Role.getId();
        this.name = Role.getName();
    }
}
