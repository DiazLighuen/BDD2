package ar.edu.unlp.info.bd2.model;


import org.bson.codecs.pojo.annotations.BsonIgnore;

import javax.persistence.*;

@Entity
public class FileReview extends PersistentObject {

    @BsonIgnore
    @OneToOne(optional = false)
    private Review review;

    @BsonIgnore
    @OneToOne(optional = false)
    private File file;

    @Column(name = "lineNumber")
    private Integer lineNumber;

    @Column(name = "comment")
    private String comment;

    public FileReview (){}

    public FileReview(Review review, File file, int lineNumber, String comment) {
        this.review = review;
        this.file = file;
        this.lineNumber = lineNumber;
        this.comment = comment;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public File getReviewedFile(){
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
