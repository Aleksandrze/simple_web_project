package com.ashes.web.project.repository;

import com.ashes.web.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.ashes.web.project.dto.UserDto;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select new com.ashes.web.project.dto.UserDto(a) from User a where a.login = :login")
    Optional<UserDto> findByLogin(String Login);
}