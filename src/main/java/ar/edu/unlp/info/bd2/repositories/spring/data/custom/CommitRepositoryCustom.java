package ar.edu.unlp.info.bd2.repositories.spring.data.custom;

import ar.edu.unlp.info.bd2.model.*;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommitRepositoryCustom {
    @Query("select c from Commit c where c.hash = ?1")
    Optional<Commit> findByHash(String commitHash);

    @Query("select c from Commit c where c.author = ?1")
    List<Commit> findByUser(User user);

    @Query("select distinct c.author from Commit c where c.branch = ?1")
    List<User> getCommitUsersByBranch(Branch branch);
}
