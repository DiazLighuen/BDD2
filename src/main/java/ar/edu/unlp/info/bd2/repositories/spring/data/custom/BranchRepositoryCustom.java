package ar.edu.unlp.info.bd2.repositories.spring.data.custom;

import ar.edu.unlp.info.bd2.model.Branch;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BranchRepositoryCustom {
    @Query("select b from Branch b where b.name = ?1")
    Optional<Branch> findByName(String branchName);
}
