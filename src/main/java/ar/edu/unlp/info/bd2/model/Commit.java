package ar.edu.unlp.info.bd2.model;

import org.bson.codecs.pojo.annotations.BsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Commit extends PersistentObject {

    @Column(name = "hash")
    private String hash;

    @Column(name = "message")
    private String message;

    @BsonIgnore
    @OneToOne
    private User author;

    @BsonIgnore
    @OneToOne
    private Branch branch;

    @BsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    private List<File> files;

    public Commit(){

    }

    public Commit(String description, String hash, User author, List<File> files, Branch branch) {
        this.message = description;
        this.hash = hash;
        this.author = author;
        this.branch = branch;
        this.files = files;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}

