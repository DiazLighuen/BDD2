package ar.edu.unlp.info.bd2.services;

import ar.edu.unlp.info.bd2.model.*;
import ar.edu.unlp.info.bd2.repositories.spring.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
public class SpringDataBithubService implements BithubService<Long> {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileReviewRepository fileReviewRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    private Object save(JpaRepository repository, Object object) {
        return repository.save(object);
    }

    @Override
    public User createUser(String email, String name) {
        if (getUserByEmail(email) == null) {
            return null;
        }
        return userRepository.save(new User(email, name));

    }

    @Override
    public Branch createBranch(String name) {
        if (getBranchByName(name)== null) {
            return null;
        }
        return branchRepository.save( new Branch(name));
    }

    @Override
    public File createFile(String content, String name) {
        /*if(!getFileByName(name).isPresent()){
            return null;
        }*/
        return fileRepository.save(new File(content, name));
    }

    @Override
    public Review createReview(Branch branch, User user) {
        return reviewRepository.save(new Review(branch, user));
    }

    @Override
    public Commit createCommit(String description, String hash, User author, List<File> list, Branch branch) {
        Commit commit = commitRepository.save(new Commit(description, hash, author, list, branch));
        branch.addCommit(commit);
        branchRepository.save(branch);
        return commit;
    }

    @Override
    public Tag createTagForCommit(String commitHash, String name) throws BithubException {
        Optional<Commit> commit = getCommitByHash(commitHash);
        if (!commit.isPresent()) {
            throw new BithubException("Commit not found");
        }
        return tagRepository.save(new Tag(commitHash, name));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<Branch> getBranchByName(String branchName) {

        return branchRepository.findByName(branchName);
    }

    @Override
    public Optional<Commit> getCommitByHash(String commitHash) {

        return commitRepository.findByHash(commitHash);
    }

    @Override
    public Optional<Tag> getTagByName(String tagName) {

        return tagRepository.findTagByName(tagName);
    }

    @Override
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public FileReview addFileReview(Review review, File file, int lineNumber, String comment) throws BithubException {
        Boolean fileIsInBranch = Boolean.FALSE;
        Iterator<Commit> commits = review.getBranch().getCommits().iterator();
        while (commits.hasNext()){
            fileIsInBranch = commits.next().getFiles().contains(file);
        }
        if(!fileIsInBranch){
            throw new BithubException("File branch not corresponds with review branch");
        }
        FileReview fileReview = (FileReview) save(fileRepository, new FileReview(review, file, lineNumber, comment));
        review.addFileReview(fileReview);
        return fileReview;
    }

    @Override
    public List<Commit> getAllCommitsForUser(Long userId) {
        User user = userRepository.getOne(userId);
        return commitRepository.findByUser(user);
    }

    @Override
    public Map<Long,Long> getCommitCountByUser() {
        List<User> users = userRepository.findAll();
        Map<Long, Long> map = new HashMap<Long, Long>();
        for (User user : users) {
            map.put(user.getId(), Long.valueOf(this.getAllCommitsForUser(user.getId()).size()));
        }
        return map;
    }

    @Override
    public List<User> getUsersThatCommittedInBranch(String branchName) throws BithubException {
        Optional<Branch> branch = getBranchByName(branchName);
        if(!branch.isPresent()){
            throw new BithubException("Branch does not exist");
        }
        return commitRepository.getCommitUsersByBranch(branch.get());
    }
}
