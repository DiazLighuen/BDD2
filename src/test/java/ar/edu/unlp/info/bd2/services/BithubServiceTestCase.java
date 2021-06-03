package ar.edu.unlp.info.bd2.services;

import static org.junit.jupiter.api.Assertions.*;

import ar.edu.unlp.info.bd2.model.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Rollback(true)
public abstract class BithubServiceTestCase<T> {

  BithubService<T> service;

  protected abstract BithubService getService();

  protected abstract T getId(PersistentObject object);

  @BeforeEach
  public void setUp() {
    this.service = this.getService();
  }

  @Test
  public void testCreateBranchAndCommit() {
    Branch master = this.service.createBranch("master");
    assertNotNull(this.getId(master));
    assertEquals("master", master.getName());

    User user = this.service.createUser("user@bithub.bd2.info.unlp.edu.ar", "User");
    assertNotNull(this.getId(user));
    assertEquals("user@bithub.bd2.info.unlp.edu.ar", user.getEmail());
    assertEquals("User", user.getName());

    File file1 = this.service.createFile("System.out.println(\"Hello world\");", "Main.java");
    assertEquals("Main.java", file1.getFilename());
    assertEquals("System.out.println(\"Hello world\");", file1.getContent());
    assertNotNull(this.getId(file1));

    File file2 = this.service.createFile("print(\"Hello world\")", "Main.py");

    Commit commit =
        this.service.createCommit(
            "Initial commit", "ab4f19z", user, Arrays.asList(file1, file2), master);
    assertEquals("Initial commit", commit.getMessage());
    assertEquals("ab4f19z", commit.getHash());
    assertNotNull(this.getId(commit));

    Optional<Commit> commitFromDb = this.service.getCommitByHash("ab4f19z");
    assertTrue(commitFromDb.isPresent());
    assertEquals(this.getId(user), this.getId(commitFromDb.get().getAuthor()));
    assertEquals(2, commitFromDb.get().getFiles().size());

    assertEquals(
        "System.out.println(\"Hello world\");", commitFromDb.get().getFiles().get(0).getContent());
    assertEquals("Main.java", commitFromDb.get().getFiles().get(0).getFilename());

    assertEquals("print(\"Hello world\")", commitFromDb.get().getFiles().get(1).getContent());
    assertEquals("Main.py", commitFromDb.get().getFiles().get(1).getFilename());

    Optional<Branch> branchFromDb = this.service.getBranchByName("master");
    assertTrue(branchFromDb.isPresent());
    assertEquals(1, branchFromDb.get().getCommits().size());
  }

  @Test
  public void testCreateTag() throws BithubException {
    Branch master = this.service.createBranch("master");
    User user = this.service.createUser("user@bithub.bd2.info.unlp.edu.ar", "User");
    File file = this.service.createFile("System.out.println(\"Hello world\");", "Main.java");
    this.service.createCommit(
        "Initial commit", "ab4f19z", user, Collections.singletonList(file), master);

    assertThrows(
        BithubException.class, () -> this.service.createTagForCommit("fakeHash123", "tag1"));

    Tag tag = this.service.createTagForCommit("ab4f19z", "tag1");
    assertNotNull(this.getId(tag));
    assertEquals("tag1", tag.getName());

    Optional<Tag> savedTag = this.service.getTagByName("tag1");
    assertTrue(savedTag.isPresent());
    assertEquals(this.getId(tag), this.getId(savedTag.get()));

    assertFalse(this.service.getTagByName("fakeTag123").isPresent());
  }

  @Test
  void testCreateReview() throws BithubException {
    Branch master = this.service.createBranch("master");
    Branch develop = this.service.createBranch("develop");

    User user1 = this.service.createUser("user1@bithub.bd2.info.unlp.edu.ar", "User One");
    User user2 = this.service.createUser("user2@bithub.bd2.info.unlp.edu.ar", "User Two");

    File fileInMasterBranch =
        this.service.createFile("System.out.println(\"Hello wrld\");", "Main.java");
    this.service.createCommit(
        "Initial commit", "ab4f19z", user1, Collections.singletonList(fileInMasterBranch), master);

    File fileInDevelopBranch =
        this.service.createFile("System.out.println(\"Hello wrld\");", "Main.java");
    this.service.createCommit(
        "Initial commit",
        "ab4f19z",
        user1,
        Collections.singletonList(fileInDevelopBranch),
        develop);

    Review review = this.service.createReview(master, user2);
    assertNotNull(this.getId(review));
    assertEquals(this.getId(master), this.getId(review.getBranch()));
    assertEquals(this.getId(user2), this.getId(review.getAuthor()));

    FileReview fileReview =
        this.service.addFileReview(
            review, fileInMasterBranch, 1, "There is a typo ('wrld' should be 'world')");
    assertNotNull(this.getId(fileReview));
    assertEquals(this.getId(fileInMasterBranch), this.getId(fileReview.getReviewedFile()));
    assertEquals(new Integer(1), fileReview.getLineNumber());
    assertEquals("There is a typo ('wrld' should be 'world')", fileReview.getComment());

    // debe fallar: el branch en el cual se quiere hacer el review no corresponde con el branch en
    // el cual se encuentra
    // el archivo
    assertThrows(
        BithubException.class,
        () ->
            this.service.addFileReview(
                review, fileInDevelopBranch, 1, "There is a typo ('wrld' should be 'world'"));

    Optional<Review> freshReview = this.service.getReviewById(this.getId(review));
    assertTrue(freshReview.isPresent());
    assertEquals(this.getId(user2), this.getId(freshReview.get().getAuthor()));
    assertEquals(this.getId(master), this.getId(freshReview.get().getBranch()));
    assertEquals(1, freshReview.get().getReviews().size());

    assertEquals(this.getId(fileReview), this.getId(freshReview.get().getReviews().get(0)));
    assertEquals(fileReview.getComment(), freshReview.get().getReviews().get(0).getComment());
    assertEquals(fileReview.getLineNumber(), freshReview.get().getReviews().get(0).getLineNumber());
  }

  @Test
  void testGetCommitsFromUser() {
    Branch master = this.service.createBranch("master");
    Branch develop = this.service.createBranch("develop");

    User user = this.service.createUser("user@bithub.bd2.info.unlp.edu.ar", "User");

    File fileInMasterBranch =
        this.service.createFile("System.out.println(\"Hello world\");", "Main.java");
    Commit commitInMaster =
        this.service.createCommit(
            "Initial commit",
            "ab4f19z",
            user,
            Collections.singletonList(fileInMasterBranch),
            master);

    File fileInDevelopBranch =
        this.service.createFile("System.out.println(\"Hello wrld\");", "Main.java");
    Commit commitInDevelop =
        this.service.createCommit(
            "Another commit",
            "12g455",
            user,
            Collections.singletonList(fileInDevelopBranch),
            develop);

    List<Commit> commits = this.service.getAllCommitsForUser(this.getId(user));
    assertEquals(2, commits.size());
    assertEquals(
        1, commits.stream().filter(s -> this.getId(s).equals(this.getId(commitInDevelop))).count());
    assertEquals(
        1, commits.stream().filter(s -> this.getId(s).equals(this.getId(commitInMaster))).count());
  }

  @Test
  void testGetTotalNumberOfCommitsPerUser() {
    Branch master = this.service.createBranch("master");

    User user1 = this.service.createUser("user1@bithub.bd2.info.unlp.edu.ar", "User One");
    User user2 = this.service.createUser("user2@bithub.bd2.info.unlp.edu.ar", "User Two");

    File file1 = this.service.createFile("System.out.println(\"Hello world\");", "Main.java");
    this.service.createCommit(
        "Initial commit", "xyz123", user1, Collections.singletonList(file1), master);

    File file2 = this.service.createFile("System.out.println(\"Goodbye world\");", "Main.java");
    this.service.createCommit(
        "Initial commit", "rew838", user2, Collections.singletonList(file2), master);

    File file3 = this.service.createFile("System.out.println(\"Ciao mondo\");", "Main.java");
    this.service.createCommit(
        "Initial commit", "troz12", user2, Collections.singletonList(file3), master);

    Map<T, Long> commitCount = this.service.getCommitCountByUser();

    assertEquals(new Long(1), commitCount.get(this.getId(user1)));
    assertEquals(new Long(2), commitCount.get(this.getId(user2)));
  }

  @Test
  void testUsersThatCommitedInBranch() throws BithubException {
    Branch master = this.service.createBranch("master");
    Branch develop = this.service.createBranch("develop");

    User user1 = this.service.createUser("user1@bithub.bd2.info.unlp.edu.ar", "User One");
    User user2 = this.service.createUser("user2@bithub.bd2.info.unlp.edu.ar", "User Two");
    User user3 = this.service.createUser("user3@bithub.bd2.info.unlp.edu.ar", "User Three");

    File file1 = this.service.createFile("System.out.println(\"Hello world\");", "Main.java");
    this.service.createCommit(
        "Initial commit", "xyz123", user1, Collections.singletonList(file1), master);

    File file2 = this.service.createFile("System.out.println(\"Goodbye world\");", "Main.java");
    this.service.createCommit(
        "Initial commit", "rew838", user2, Collections.singletonList(file2), master);

    File file3 = this.service.createFile("System.out.println(\"Ciao mondo\");", "Main.java");
    this.service.createCommit(
        "Initial commit", "troz12", user1, Collections.singletonList(file3), develop);

    File file4 = this.service.createFile("System.out.println(\"Chau mundo\");", "Main.java");
    this.service.createCommit(
        "Initial commit", "troz12", user1, Collections.singletonList(file3), master);

    assertThrows(
        BithubException.class,
        () -> this.service.getUsersThatCommittedInBranch("non-existent-branch"));

    List<User> users = this.service.getUsersThatCommittedInBranch("master");

    assertEquals(2, users.size());
    assertTrue(
        users.stream().anyMatch(u -> u.getEmail().equals("user1@bithub.bd2.info.unlp.edu.ar")));
    assertTrue(
        users.stream().anyMatch(u -> u.getEmail().equals("user2@bithub.bd2.info.unlp.edu.ar")));
    assertFalse(
        users.stream().anyMatch(u -> u.getEmail().equals("user3@bithub.bd2.info.unlp.edu.ar")));
  }
}
