package ar.edu.unlp.info.bd2.repositories;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;

import ar.edu.unlp.info.bd2.model.*;
import ar.edu.unlp.info.bd2.mongo.Association;
import com.mongodb.client.*;
import java.util.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

public class MongoDBBithubRepository implements BithubRepository<ObjectId>{

    public MongoDBBithubRepository(){

    }

    @Autowired
    private MongoClient client;

    @Autowired
    private MongoDatabase database = client.getDatabase("bd2");

    private MongoCollection<User> userMongoCollection = database.getCollection("user", User.class);
    private MongoCollection<Branch> branchMongoCollection = database.getCollection("branch", Branch.class);
    private MongoCollection<File> fileMongoCollection = database.getCollection("file", File.class);
    private MongoCollection<Commit> commitMongoCollection = database.getCollection("commit", Commit.class);
    private MongoCollection<Tag> tagMongoCollection = database.getCollection("tag", Tag.class);
    private MongoCollection<Review> reviewMongoCollection = database.getCollection("review", Review.class);
    private MongoCollection<FileReview> fileReviewMongoCollection = database.getCollection("fileReview", FileReview.class);
    private MongoCollection<Association> branchCommitAssociationMongoCollection = database.getCollection("associationBC", Association.class);
    private MongoCollection<Association> fileCommitAssociationMongoCollection = database.getCollection("associationFC", Association.class);
    private MongoCollection<Association> commitUserAssociationMongoCollection = database.getCollection("associationCU", Association.class);
    //private MongoCollection<Association> branchUserAssociationMongoCollection = database.getCollection("associationBU", Association.class);
    private MongoCollection<Association> branchReviewAssociationMongoCollection = database.getCollection("associationBR", Association.class);
    private MongoCollection<Association> reviewUserAssociationMongoCollection = database.getCollection("associationRU", Association.class);
    private MongoCollection<Association> reviewFileReviewAssociationMongoCollection = database.getCollection("associationRFr", Association.class);
    private MongoCollection<Association> FileReviewFileAssociationMongoCollection = database.getCollection("associationFrF", Association.class);


    @Override
    public User saveUser (User user) {
        userMongoCollection.insertOne(user);
        return user;
    }

    @Override
    public Branch saveBranch (Branch branch) {
        for (Commit commit: branch.getCommits()){
            this.saveCommit(commit);
            branchCommitAssociationMongoCollection.insertOne( new Association(branch.getObjectId(),commit.getObjectId()));
        }
        branchMongoCollection.insertOne(branch);
        return branch;
    }

    @Override
    public File saveFile (File file) {
        fileMongoCollection.insertOne(file);
        return file;
    }

    @Override
    public Commit saveCommit (Commit commit){
        commitUserAssociationMongoCollection.insertOne( new Association(commit.getObjectId(),commit.getAuthor().getObjectId()));
        branchCommitAssociationMongoCollection.insertOne( new Association(commit.getBranch().getObjectId(),commit.getObjectId()));
        for (File file: commit.getFiles()){
            fileCommitAssociationMongoCollection.insertOne( new Association(file.getObjectId(),commit.getObjectId()));
        }
        commitMongoCollection.insertOne(commit);
        return commit;
    }

    @Override
    public Tag saveTag(Tag tag) {
        tagMongoCollection.insertOne(tag);
        return tag;
    }

    @Override
    public Review saveReview(Review review) {
        reviewUserAssociationMongoCollection.insertOne( new Association(review.getObjectId(),review.getAuthor().getObjectId()));
        branchReviewAssociationMongoCollection.insertOne( new Association(review.getBranch().getObjectId(),review.getObjectId()));
        for (FileReview fileReview: review.getReviews()){
            reviewFileReviewAssociationMongoCollection.insertOne( new Association(review.getObjectId(),fileReview.getObjectId()));
        }
        reviewMongoCollection.insertOne(review);
        return review;
    }

    @Override
    public FileReview saveFileReview(FileReview fileReview) {
        FileReviewFileAssociationMongoCollection.insertOne( new Association(fileReview.getObjectId(),fileReview.getReviewedFile().getObjectId()));
        reviewFileReviewAssociationMongoCollection.insertOne( new Association(fileReview.getReview().getObjectId(),fileReview.getObjectId()));
        fileReviewMongoCollection.insertOne(fileReview);
        return fileReview;
    }

    @Override
    public Boolean existNameBranch (String name) {
        Branch result =  branchMongoCollection.find(eq("name",name)).first();
        if (result.getObjectId() != null){
            return true;
        }
        return false;
    }

    @Override
    public Boolean existEmailUser (String email) {
        User result =  userMongoCollection.find(eq("email",email)).first();
        if (result.getId() != null){
            return true;
        }
        return false;
    }

    @Override
    public Commit getCommitByHash (String hash) {
        Commit result =  commitMongoCollection.find(eq("hash",hash)).first();
        if (result.getObjectId()!= null){
            result = this.recreateCommit(result);
            return result;
        }
        return null;
    }

    @Override
    public Branch getBranchByName (String name) {
        Branch result =  branchMongoCollection.find(eq("name",name)).first();
        if (result.getId() != null){
            result = this.recreateBranch(result);
            return result;
        }
        return null;
    }

    @Override
    public Optional<Tag> getTagByName(String tagName) {
        Tag result =  tagMongoCollection.find(eq("name",tagName)).first();
        if (result.getId() != null){
            return Optional.ofNullable(result);
        }
        return null;
    }

    @Override
    public Optional<Review> getReviewById(ObjectId id) {
        Review result =  reviewMongoCollection.find(eq("_id",id)).first();
        if (result.getId() != null){
            result = this.recreateReview(result);
            return Optional.ofNullable(result);
        }
        return null;
    }

    @Override
    public List<Commit> getAllCommitsForUser(ObjectId userId) {
        List<Commit> result = new LinkedList<>();
        Iterator<Association> commitIterator = commitUserAssociationMongoCollection.find(eq("destination",userId)).iterator();
        while (commitIterator.hasNext()){
            result.add(this.getCommitById(commitIterator.next().getSource()));
        }
        return result;
    }

    @Override
    public Map<ObjectId, Long> getCommitCountByUser() {
        List<User> users = new LinkedList<>();
        Iterator<User> userIterator = userMongoCollection.find().iterator();
        while (userIterator.hasNext()){
            users.add(userIterator.next());
        }
        Map<ObjectId, Long> map = new HashMap<ObjectId, Long>();
        for (User i : users) map.put(i.getObjectId(),Long.valueOf(this.getAllCommitsForUser(i.getObjectId()).size()));
        return map;
    }

    @Override
    public List<User> getUsersThatCommittedInBranch(String branchName) {
        Branch branch = this.getBranchByName(branchName);
        Set<User> set = new HashSet<>();
        Iterator<Association> associationIterator = branchCommitAssociationMongoCollection.find(eq("source",branch.getObjectId())).iterator();
        while (associationIterator.hasNext()){
            set.add(this.getCommitById(associationIterator.next().getDestination()).getAuthor());
        }
        List<User> result = new ArrayList<User>(set);
        return result;
    }

    @Override
    public User getUserByEmail(String email) {
        User result =  userMongoCollection.find(eq("email",email)).first();
        if (result.getId() != null){
            return result;
        }
        return null;
    }

    private Commit getCommitById(ObjectId id){
        Commit result =  commitMongoCollection.find(eq("_id",id)).first();
        if (result.getObjectId() != null){
            result = this.recreateCommit(result);
            return result;
        }
        return null;
    }

    private Branch getBranchById(ObjectId id){
        Branch result =  branchMongoCollection.find(eq("_id",id)).first();
        if (result.getObjectId() != null){
            result = this.recreateBranch(result);
            return result;
        }
        return null;
    }

    private File getFileById(ObjectId id){
        File result =  fileMongoCollection.find(eq("_id",id)).first();
        if (result.getObjectId() != null){
            return result;
        }
        return null;
    }

    private User getUserById(ObjectId id){
        User result =  userMongoCollection.find(eq("_id",id)).first();
        if (result.getObjectId() != null){
            return result;
        }
        return null;
    }

    private Commit recreateCommit(Commit commit){
        //files
        List<File> fileList = null;
        Iterator<Association> associationIteratorBC =branchCommitAssociationMongoCollection.find(eq("destination",commit.getObjectId())).iterator();
        while (associationIteratorBC.hasNext()){
            fileList.add(this.getFileById(associationIteratorBC.next().getDestination()));
        }
        commit.setFiles(fileList);

        //author
        Association associationCU =commitUserAssociationMongoCollection.find(eq("source",commit.getObjectId())).first();
        commit.setAuthor(this.getUserById(associationCU.getDestination()));

        //branch
        Association associationBC =branchCommitAssociationMongoCollection.find(eq("destination",commit.getObjectId())).first();
        commit.setBranch(this.getBranchById(associationBC.getSource()));

        return commit;
    }

    private Branch recreateBranch(Branch branch){
        //commits
        List<Commit> commitList = null;
        Iterator<Association> associationIterator =branchCommitAssociationMongoCollection.find(eq("destination",branch.getObjectId())).iterator();
        while (associationIterator.hasNext()){
            commitList.add(this.getCommitById(associationIterator.next().getDestination()));
        }
        branch.setCommits(commitList);

        return branch;
    }

    private FileReview recreateFileReview(FileReview fileReview){
        //review
        Association associationRFr =reviewFileReviewAssociationMongoCollection.find(eq("destination",fileReview.getObjectId())).first();
        fileReview.setReview(this.getReviewById(associationRFr.getDestination()).get());

        //file
        Association associationFrF =FileReviewFileAssociationMongoCollection.find(eq("source",fileReview.getObjectId())).first();
        fileReview.setFile(this.getFileById(associationFrF.getDestination()));

        return fileReview;
    }

    private Review recreateReview(Review review){
        //author
        Association associationRU =reviewUserAssociationMongoCollection.find(eq("source",review.getObjectId())).first();
        review.setAuthor(this.getUserById(associationRU.getDestination()));

        //branch
        Association associationBR =branchReviewAssociationMongoCollection.find(eq("destination",review.getObjectId())).first();
        review.setBranch(this.getBranchById(associationBR.getSource()));

        return review;
    }


}
