package ar.edu.unlp.info.bd2.repositories.spring.data.custom;

import ar.edu.unlp.info.bd2.model.User;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepositoryCustom {
    @Query("select u from User u where u.email = ?1")
    Optional<User> findByEmail(String email);
}
