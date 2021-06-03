package ar.edu.unlp.info.bd2.repositories;

import ar.edu.unlp.info.bd2.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BithubRepository<IdType> {

    User saveUser(User user);

    Branch saveBranch(Branch branch);

    File saveFile(File file);

    Commit saveCommit(Commit commit);

    Tag saveTag(Tag tag);

    Review saveReview(Review review);

    FileReview saveFileReview (FileReview fileReview);

    Boolean existNameBranch(String name);

    Boolean existEmailUser(String email);

    Commit getCommitByHash(String hash);

    Branch getBranchByName(String name);

    Optional<Tag> getTagByName(String tagName);

    Optional<Review> getReviewById(IdType id);

    List<Commit> getAllCommitsForUser(IdType userId);

    Map<IdType, Long> getCommitCountByUser();

    List<User> getUsersThatCommittedInBranch(String branchName);

    User getUserByEmail(String email);
}
