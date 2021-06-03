package ar.edu.unlp.info.bd2.model;


import org.bson.codecs.pojo.annotations.BsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Review extends PersistentObject {

    @BsonIgnore
    @OneToOne
    private Branch branch;

    @BsonIgnore
    @OneToOne
    private User user;

    @BsonIgnore
    @OneToMany
    private List<FileReview> fileReviewList;

    public Review (){}

    public Review(Branch branch, User user) {
        this.branch = branch;
        this.user = user;
        this.fileReviewList = new ArrayList<FileReview>();
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public User getAuthor() {
        return user;
    }

    public void setAuthor(User user) {
        this.user = user;
    }

    public void addFileReview(FileReview fileReview){
        this.fileReviewList.add(fileReview);
    }

    public List<FileReview> getReviews() {
        return fileReviewList;
    }
}
