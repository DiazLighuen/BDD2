package ar.edu.unlp.info.bd2.repositories.spring.data.custom;

import ar.edu.unlp.info.bd2.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TagRepositoryCustom {
    @Query("SELECT t FROM Tag t WHERE t.name = :tagName")
    Optional<Tag> findTagByName( @Param("tagName") String tagName);
}
