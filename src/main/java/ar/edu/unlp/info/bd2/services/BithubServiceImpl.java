package ar.edu.unlp.info.bd2.services;

import ar.edu.unlp.info.bd2.model.*;
import ar.edu.unlp.info.bd2.repositories.BithubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional
public abstract class BithubServiceImpl<IdType> implements BithubService<IdType>{

    @Autowired
    private BithubRepository repository;

    public User createUser(String email, String name) {
        if (this.repository.existEmailUser(email)){
            return null;
        }
        return this.repository.saveUser(new User(email,name));

    }

    public Optional<User> getUserByEmail(String email) {

        return Optional.ofNullable(this.repository.getUserByEmail(email));
    }

    public Branch createBranch(String name) {
        if (this.repository.existNameBranch(name)){
            return null;
        }
        return this.repository.saveBranch(new Branch(name));
    }

    public Commit createCommit(String description, String hash, User author, List<File> files, Branch branch) {
        Commit commit = this.repository.saveCommit(new Commit(description, hash, author, files, branch));
        branch.addCommit(commit);
        repository.saveBranch(branch);
        return commit;
    }

    public Tag createTagForCommit(String commitHash, String name) throws BithubException {
        Optional<Commit> commit = getCommitByHash(commitHash);
        if(!commit.isPresent()){
            throw new BithubException("Commit not found");
        }
        return this.repository.saveTag(new Tag(commit.get().getHash(), name));
    }

    public Optional<Commit> getCommitByHash(String commitHash) {
        return Optional.ofNullable(this.repository.getCommitByHash(commitHash));
    }

    public File createFile(String content, String name) {
        return this.repository.saveFile(new File(content, name));
    }

    public Optional<Tag> getTagByName(String tagName) {
        return this.repository.getTagByName(tagName);
    }

    public Review createReview(Branch branch, User user) {
        return this.repository.saveReview(new Review(branch, user));
    }

    public Optional<Review> getReviewById(IdType id) {
        return this.repository.getReviewById(id);
    }

    public List<Commit> getAllCommitsForUser(IdType userId) {
        return this.repository.getAllCommitsForUser(userId);
    }

    public Map<IdType, Long> getCommitCountByUser() {
        return this.repository.getCommitCountByUser();
    }

    public List<User> getUsersThatCommittedInBranch(String branchName) throws BithubException {
        Optional<Branch> branch = getBranchByName(branchName);
        if(!branch.isPresent()){
            throw new BithubException("Branch does not exist");
        }
        return this.repository.getUsersThatCommittedInBranch(branchName);
    }

    public Optional<Branch> getBranchByName(String branchName) {
        return Optional.ofNullable(this.repository.getBranchByName(branchName));
    }

    public FileReview addFileReview(Review review, File file, int lineNumber, String comment) throws BithubException{
        Boolean fileIsInBranch = Boolean.FALSE;
        Iterator<Commit> commits = review.getBranch().getCommits().iterator();
        while (commits.hasNext()){
            fileIsInBranch = commits.next().getFiles().contains(file);
        }
        if(!fileIsInBranch){
            throw new BithubException("File branch not corresponds with review branch");
        }
        FileReview fileReview = repository.saveFileReview(new FileReview(review, file, lineNumber, comment));
        review.addFileReview(fileReview);
        return fileReview;
    }
}
