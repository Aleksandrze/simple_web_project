package com.ashes.web.project.service;

import com.ashes.web.project.component.JwtProvider;
import com.ashes.web.project.domain.JwtRequest;
import com.ashes.web.project.domain.JwtResponse;
import com.ashes.web.project.dto.RoleDto;
import com.ashes.web.project.dto.UserDto;
import com.ashes.web.project.model.Role;
import com.ashes.web.project.model.User;
import com.ashes.web.project.repository.RoleRepository;
import com.ashes.web.project.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private JwtProvider jwtProvider;

    @Captor
    private ArgumentCaptor<User> captor;

    @Test
    void testSaveNewUser() {
        // given
        UserDto userDto = new UserDto();
        userDto.setUsername("notAdmin");
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());
        when(roleService.getRoleByName("USER")).thenReturn(any(Role.class));

        // when
        ResponseEntity<String> response = userService.saveUser(userDto);

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is("Success"));

        verify(userRepository, times(1)).findByUsername(anyString());
        verify(userRepository, times(1)).save(captor.capture());
        verifyNoMoreInteractions(userRepository);
    }


    @Test
    void testLogin() {
        // given
        JwtRequest jwtRequest = new JwtRequest("Admin", "1234");
        User newUser = new User();
        newUser.setPassword("1234");
        when(userRepository.findByUsername(jwtRequest.getLogin())).thenReturn(Optional.of(newUser));
        when(jwtProvider.generateAccessToken(newUser)).thenReturn("thisYouToken");

        // when
        ResponseEntity<JwtResponse> response = userService.login(jwtRequest);

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(Objects.requireNonNull(response.getBody()).getAccessToken(), is("thisYouToken"));

        verify(userRepository, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(userRepository);
    }


    @Test
    void testGetUserByUsername() {
        // given
        when(userRepository.findByUsername("ADMIN")).thenReturn(Optional.of(new User()));
        // when
        Optional<User> optionalUser = userService.getUserByUsername("ADMIN");

        // then
        assertThat(optionalUser, is(notNullValue()));

        verify(userRepository, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testGetUserByUsernameAndReturnDto() {
        // given
        UserDto userDto = new UserDto();
        when(userRepository.findByLoginAndReturnDto("ADMIN")).thenReturn(Optional.of(userDto));
        // when
        ResponseEntity<UserDto> response = userService.getUserByUsernameAndReturnDto("ADMIN");

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(userDto));

        verify(userRepository, times(1)).findByLoginAndReturnDto(anyString());
        verifyNoMoreInteractions(userRepository);


    }

    @Test
    void testGetAllUsers() {
        // given
        List<UserDto> userDtoList = List.of(new UserDto(), new UserDto(), new UserDto());
        when(userRepository.findAllAndReturnDtos()).thenReturn(userDtoList);
        // when
        ResponseEntity<List<UserDto>> response = userService.getAllUsers();

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), hasSize(3));

        verify(userRepository, times(1)).findAllAndReturnDtos();
        verifyNoMoreInteractions(userRepository);

    }

    @Test
    void testGetAllUsesByRole() {
        // given
        RoleDto roleDto = new RoleDto();
        roleDto.setId(1L);
        when(jwtProvider.decodeJwt(roleDto.getAccessToken())).thenReturn("ADMIN");
        User user = new User();
        when(userRepository.findByUsername("ADMIN")).thenReturn(Optional.of(user));
        user.setRole(new Role(1L, "ADMIN"));
        List<UserDto> userDtoList = List.of(new UserDto(), new UserDto(), new UserDto());
        when(userRepository.findAllByRoleAndReturnDtos(roleDto.getId())).thenReturn(userDtoList);
        // when
        ResponseEntity<List<UserDto>> response = userService.getAllUsesByRole(roleDto);

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), hasSize(3));

        verify(userRepository, times(1)).findByUsername(anyString());
        verify(userRepository, times(1)).findAllByRoleAndReturnDtos(anyLong());
        verifyNoMoreInteractions(userRepository);
    }


    @Test
    void testChangeUsersRole() {
        // given
        RoleDto roleDto = new RoleDto();
        roleDto.setId(1L);
        roleDto.setName("ADMIN");
        UserDto userDto = new UserDto();
        userDto.setUsername("USER");
        when(jwtProvider.decodeJwt(userDto.getAccessToken())).thenReturn("ADMIN");
        User adminUser = new User();
        when(userRepository.findByUsername("ADMIN")).thenReturn(Optional.of(adminUser));
        adminUser.setRole(new Role(1L,"ADMIN"));
        User changeUser = new User();
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.of(changeUser));
        changeUser.setUsername("USER");
        userDto.setUsername("USER");
        // when
        ResponseEntity<String> response = userService.changeUsersRole(userDto,roleDto);

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));


        verify(userRepository, times(2)).findByUsername(anyString());
        verify(userRepository, times(1)).save(captor.capture());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testModifyUser() {
        // given
        UserDto userDto = new UserDto();
        User user = new User();
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
        user.setUsername("A");
        userDto.setUsername("A");

        // when
        ResponseEntity<UserDto> response = userService.modifyUser(userDto);

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(userDto));

        verify(userRepository, times(1)).findById(userDto.getId());
        verify(userRepository, times(1)).save(captor.capture());
        verifyNoMoreInteractions(userRepository);
    }
}
