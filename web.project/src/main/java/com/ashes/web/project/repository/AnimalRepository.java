package com.ashes.web.project.repository;

import com.ashes.web.project.dto.AnimalDto;
import com.ashes.web.project.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    @Query("select new com.ashes.web.project.dto.AnimalDto(a) from Animal a")
    List<AnimalDto> findAllAndReturnDto();

    @Query("select new com.ashes.web.project.dto.AnimalDto(a) from Animal a where a.name = :name")
    Optional<AnimalDto> findByNameAndReturnDto(String name);

    @Query("select new com.ashes.web.project.dto.AnimalDto(a) from Animal a join a.location l where l.name = :locationName")
    List<AnimalDto> findAllByLocation(String locationName);

    Optional<Animal> findById(Long id);

    @Query("select a from Animal a where a.name= :name and a.id <> :id")
    Optional<Animal> findByNameWithDifferentId(String name, Long id);
}