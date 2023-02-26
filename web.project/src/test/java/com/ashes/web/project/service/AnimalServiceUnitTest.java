package com.ashes.web.project.service;

import com.ashes.web.project.component.JwtProvider;
import com.ashes.web.project.dto.AnimalDto;
import com.ashes.web.project.model.Animal;
import com.ashes.web.project.model.Location;
import com.ashes.web.project.repository.AnimalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnimalServiceUnitTest {
    @InjectMocks
    private AnimalService animalService;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private LocationService locationService;

    @Mock
    private UserService userService;

    @Mock
    private JwtProvider jwtProvider;

    @Captor
    private ArgumentCaptor<Animal> captor;

    @Test
    void testSaveAnimal() {
        // given
        AnimalDto animalDto = new AnimalDto();
        animalDto.setStatus("IN SHELTER");
        System.out.println(animalDto.getStatus());
        when(jwtProvider.decodeJwt(animalDto.getAccessToken())).thenReturn("ADMIN");
        when(userService.checkUserPrivileges(anyString())).thenReturn("ADMIN");
        Location location = new Location();
        when(locationService.getLocation(animalDto.getLocation())).thenReturn(Optional.of(location));
        location.setFilled((short) 4);
        location.setMaxCapacity((short) 5);
        when(locationService.isPlaceAvailable(location.getMaxCapacity(), location.getFilled())).thenReturn(true);
        Animal animal = new Animal();
        when(animalRepository.findByShelterIdentifier(animalDto.getShelterIdentifier())).thenReturn(Optional.empty());
        animalDto.setBirthdate(LocalDate.parse("2022-01-01"));
        // when
        ResponseEntity<String> response = animalService.saveAnimal(animalDto);

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is("Animal information saved successfully."));

        // verify(animalRepository, times(1)).findById(anyLong());
        verify(animalRepository, times(1)).save(captor.capture());
        verifyNoMoreInteractions(animalRepository);
    }

    @Test
    void testSaveAnimalOnFound() {
        // given
        AnimalDto animalDto = new AnimalDto();
        animalDto.setStatus("IN SHELTER");
        System.out.println(animalDto.getStatus());
        when(jwtProvider.decodeJwt(animalDto.getAccessToken())).thenReturn("ADMIN");
        when(userService.checkUserPrivileges(anyString())).thenReturn("ADMIN");
        Location location = new Location();
        when(locationService.getLocation(animalDto.getLocation())).thenReturn(Optional.of(location));
        location.setFilled((short) 4);
        location.setMaxCapacity((short) 5);
        when(locationService.isPlaceAvailable(location.getMaxCapacity(), location.getFilled())).thenReturn(true);
        Animal animal = new Animal();
        when(animalRepository.findByShelterIdentifier(animalDto.getShelterIdentifier())).thenReturn(Optional.of(animal));

        // when
        ResponseEntity<String> response = animalService.saveAnimal(animalDto);

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getBody(), is("An animal with this shelter ID already exists."));

        verifyNoMoreInteractions(animalRepository);
    }

    @Test
    void testSaveAnimalWrongLocation() {
        // given
        AnimalDto animalDto = new AnimalDto();
        animalDto.setStatus("IN SHELTER");
        System.out.println(animalDto.getStatus());
        when(jwtProvider.decodeJwt(animalDto.getAccessToken())).thenReturn("ADMIN");
        when(userService.checkUserPrivileges(anyString())).thenReturn("ADMIN");
        Location location = new Location();
        when(locationService.getLocation(animalDto.getLocation())).thenReturn(Optional.empty());

        // when
        ResponseEntity<String> response = animalService.saveAnimal(animalDto);

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), is("Location does not exists!"));

        verifyNoMoreInteractions(animalRepository);
    }

    @Test
    void testSaveAnimalFullLocation() {
        // given
        AnimalDto animalDto = new AnimalDto();
        animalDto.setStatus("IN SHELTER");
        System.out.println(animalDto.getStatus());
        when(jwtProvider.decodeJwt(animalDto.getAccessToken())).thenReturn("ADMIN");
        when(userService.checkUserPrivileges(anyString())).thenReturn("ADMIN");
        Location location = new Location();
        when(locationService.getLocation(animalDto.getLocation())).thenReturn(Optional.of(location));
        location.setFilled((short) 5);
        location.setMaxCapacity((short) 4);
        when(locationService.isPlaceAvailable(location.getMaxCapacity(), location.getFilled())).thenReturn(false);

        // when
        ResponseEntity<String> response = animalService.saveAnimal(animalDto);

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), is("There is no available place in selected location."));

        verifyNoMoreInteractions(animalRepository);
    }

    @Test
    void testGetAllAnimals() {
        // given
        List<AnimalDto> animalsDto = List.of(new AnimalDto(), new AnimalDto(), new AnimalDto());
        when(animalRepository.findAllAndReturnDtos()).thenReturn(animalsDto);

        // when
        ResponseEntity<List<AnimalDto>> response = animalService.getAllAnimals();

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), hasSize(3));

        verify(animalRepository, times(1)).findAllAndReturnDtos();
        verifyNoMoreInteractions(animalRepository);
    }

    @Test
    void testGetAnimalByShelterIdentifier() {
        // given
        AnimalDto animalDto = new AnimalDto();
        when(animalRepository.findByNameAndReturnDto(anyString())).thenReturn(java.util.Optional.of(animalDto));

        when(jwtProvider.decodeJwt(anyString())).thenReturn("ADMIN");
        when(userService.checkUserPrivileges(anyString())).thenReturn("ADMIN");

        // when
        ResponseEntity<AnimalDto> response = animalService.getAnimalByShelterIdentifier("token", anyString());

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(animalDto));

        verify(animalRepository, times(1)).findByNameAndReturnDto(anyString());
        verifyNoMoreInteractions(animalRepository);
    }

    @Test
    void testGetAnimalByShelterIdentifierIsNull() {
        // given

        // when
        ResponseEntity<AnimalDto> response = animalService.getAnimalByShelterIdentifier(null, anyString());

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));

        verifyNoMoreInteractions(animalRepository);
    }

    @Test
    void testGetAllAnimalsByLocation() {
        // given
        List<AnimalDto> animalDtoList = List.of(new AnimalDto(), new AnimalDto(), new AnimalDto());
        when(locationService.getLocation(anyString())).thenReturn(java.util.Optional.of(new Location()));
        when(jwtProvider.decodeJwt(anyString())).thenReturn("ADMIN");
        when(userService.checkUserPrivileges(anyString())).thenReturn("ADMIN");

        when(animalRepository.findAllByLocationName(anyString())).thenReturn(animalDtoList);

        // when
        ResponseEntity<List<AnimalDto>> response = animalService.getAllAnimalsByLocation("token", anyString());

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), hasSize(3));

        verify(animalRepository, times(1)).findAllByLocationName(anyString());
        verifyNoMoreInteractions(animalRepository);
    }

    @Test
    void testGetAllAnimalsByLocationNotExist() {
        // given
        when(locationService.getLocation(anyString())).thenReturn(java.util.Optional.empty());

        // when
        ResponseEntity<List<AnimalDto>> response = animalService.getAllAnimalsByLocation("token", anyString());

        // then
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));;

        verifyNoMoreInteractions(animalRepository);
    }

    @Test
    void testModifyAnimalWithoutModify() {
        // given
        AnimalDto animalDto = new AnimalDto();
        when(jwtProvider.decodeJwt(animalDto.getAccessToken())).thenReturn("ADMIN");
        when(userService.checkUserPrivileges(anyString())).thenReturn("ADMIN");
        Animal animal = new Animal();
        animalDto.setShelterIdentifier("A-01");
        animal.setShelterIdentifier("A-01");
        animalDto.setLocation("A");
        Location location = new Location();
        location.setName("A");
        animal.setLocation(location);
        animalDto.setBirthdate(LocalDate.now());
        animal.setBirthdate(LocalDate.now());

        when(animalRepository.findById(animalDto.getId())).thenReturn(Optional.of(animal));

        // when
        ResponseEntity<String> response = animalService.modifyAnimal(animalDto);

        // thens
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is("Success"));

        // verify(animalRepository, times(1)).findById(anyLong());
        verify(animalRepository, times(1)).save(captor.capture());
        verifyNoMoreInteractions(animalRepository);
    }

    @Test
    void testModifyAnimalExistShelterIdentifier() {
        // given
        AnimalDto animalDto = new AnimalDto();
        when(jwtProvider.decodeJwt(animalDto.getAccessToken())).thenReturn("ADMIN");
        when(userService.checkUserPrivileges(anyString())).thenReturn("ADMIN");
        Animal animal = new Animal();
        animalDto.setShelterIdentifier("A-02");
        animal.setShelterIdentifier("A-01");
        animalDto.setLocation("A");
        Location location = new Location();
        location.setName("A");
        animal.setLocation(location);
        animalDto.setBirthdate(LocalDate.now());
        animal.setBirthdate(LocalDate.now());
        when(animalRepository.findById(animalDto.getId())).thenReturn(Optional.of(animal));
        when(animalRepository.findByNameWithDifferentId(animalDto.getShelterIdentifier(), animal.getId())).thenReturn(Optional.of(new Animal()));


        // when
        ResponseEntity<String> response = animalService.modifyAnimal(animalDto);

        // thens
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getBody(), is("Animals name exists in system. Change name!"));

        verify(animalRepository, times(1)).findByNameWithDifferentId(animalDto.getShelterIdentifier(), animal.getId());
        verifyNoMoreInteractions(animalRepository);
    }


}
