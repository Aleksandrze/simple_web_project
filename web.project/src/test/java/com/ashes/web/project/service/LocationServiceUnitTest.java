package com.ashes.web.project.service;

import com.ashes.web.project.component.JwtProvider;
import com.ashes.web.project.dto.LocationDto;
import com.ashes.web.project.model.Location;
import com.ashes.web.project.repository.LocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceUnitTest {

    @InjectMocks
    private LocationService locationService;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private UserService userService;

    @Mock
    private JwtProvider jwtProvider;

    @Captor
    private ArgumentCaptor<Location> captor;

    @Test
    void testSaveNewLocation() {
        // given
        LocationDto newLocationDto = new LocationDto();
        newLocationDto.setMaxCapacity((short) 1);

        when(jwtProvider.decodeJwt(newLocationDto.getAccessToken())).thenReturn("ADMIN");

        when(userService.checkUserPrivileges(anyString())).thenReturn("ADMIN");

        when(locationRepository.findByName(newLocationDto.getName())).thenReturn(Optional.empty());


        // when
        ResponseEntity<String> response = locationService.saveLocation(newLocationDto);

        // then
        System.out.println(response.getBody());
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertEquals(response.getBody(), "Success");

        verify(locationRepository, times(1)).save(captor.capture());
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    void testSaveNewLocationOnFound() {
        // given
        LocationDto newLocationDto = new LocationDto();
        newLocationDto.setMaxCapacity((short) 1);

        when(jwtProvider.decodeJwt(newLocationDto.getAccessToken())).thenReturn("ADMIN");

        when(userService.checkUserPrivileges(anyString())).thenReturn("ADMIN");

        when(locationRepository.findByName(newLocationDto.getName())).thenReturn(Optional.of(new Location()));

        // when
        ResponseEntity<String> response = locationService.saveLocation(newLocationDto);

        // then
        System.out.println(response.getBody());
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertEquals(response.getBody(), "Location exists!");

    }

    @Test
    void testGetAllLocation() {
        // given
        List<LocationDto> locationDto = List.of(new LocationDto(), new LocationDto(), new LocationDto());
        when(locationRepository.findAllAndReturnDtos()).thenReturn(locationDto);

        // when
        ResponseEntity<List<LocationDto>> response = locationService.getAllLocations();

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), hasSize(3));

        verify(locationRepository, times(1)).findAllAndReturnDtos();
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    void testGetLocationByName() {
        // given
        LocationDto locationDto = new LocationDto();
        when(locationRepository.findByNameAndReturnDto("name")).thenReturn(Optional.of(locationDto));

        // when
        ResponseEntity<LocationDto> response = locationService.getLocationByName("name");

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(locationDto));

        verify(locationRepository, times(1)).findByNameAndReturnDto(anyString());
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    void testModifyLocationWithoutModify() {
        // given
        LocationDto locationDto = new LocationDto();
        locationDto.setId(1L);
        when(jwtProvider.decodeJwt(locationDto.getAccessToken())).thenReturn("ADMIN");

        when(userService.checkUserPrivileges(anyString())).thenReturn("ADMIN");

        Location location = new Location();
        when(locationRepository.findById(locationDto.getId())).thenReturn(Optional.of(location));


        location.setName("Admin");
        locationDto.setName("Admin");
        location.setMaxCapacity((short) 4);
        locationDto.setMaxCapacity((short) 4);
        location.setFilled((short) 0);

        // findByNameWithDifferentId


        // when
        ResponseEntity<String> response = locationService.modifyLocation(locationDto);

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is("Location modified successfully"));

        verify(locationRepository, times(1)).findById(anyLong());
        verify(locationRepository, times(1)).save(captor.capture());
        verifyNoMoreInteractions(locationRepository);
    }



    @Test
    void temp() {
        // given

        // when


        // then
    }
}
