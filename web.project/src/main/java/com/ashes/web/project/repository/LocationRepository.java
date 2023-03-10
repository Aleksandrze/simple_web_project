package com.ashes.web.project.repository;

import com.ashes.web.project.dto.LocationDto;
import com.ashes.web.project.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByName(String name);

    @Query("select new com.ashes.web.project.dto.LocationDto(l) from Location l where l.name = :name")
    Optional<LocationDto> findByNameAndReturnDto(String name);

    @Query("select new com.ashes.web.project.dto.LocationDto(l) from Location l")
    List<LocationDto> findAllAndReturnDtos();

    Optional<Location> findById(Long id);

    @Query("select a from Location a where a.name= :name and a.id <> :id")
    Optional<Location> findByNameWithDifferentId(String name, Long id);



}