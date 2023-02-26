package com.ashes.web.project.service;

import com.ashes.web.project.dto.RoleDto;
import com.ashes.web.project.model.Role;
import com.ashes.web.project.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceUnitTest {

    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @Test
    void testGetAllRoles() {
        // given
        List<RoleDto> roleDtoList = List.of(new RoleDto(), new RoleDto());
        when(roleRepository.findAllAndReturnDtos()).thenReturn(roleDtoList);

        // when
        ResponseEntity<List<RoleDto>> response = roleService.getAllRoles();

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), hasSize(2));

        verify(roleRepository, times(1)).findAllAndReturnDtos();
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void testGetAllRolesDataBaseNull() {
        // given
        List<RoleDto> roleDtoList = List.of();
        when(roleRepository.findAllAndReturnDtos()).thenReturn(roleDtoList);

        // when
        ResponseEntity<List<RoleDto>> response = roleService.getAllRoles();

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), hasSize(0));

        verify(roleRepository, times(1)).findAllAndReturnDtos();
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void testisRoleExists() {
        // given
        Optional<Role> r = Optional.of(new Role());
        when(roleRepository.findById(anyLong())).thenReturn(r);

        // when
        Boolean response = roleService.isRoleExists(1L);

        // then
        assertThat(response, is(notNullValue()));
        assertThat(String.valueOf(response), true);

        verify(roleRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(roleRepository);
    }
}
