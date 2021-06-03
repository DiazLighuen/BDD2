package ar.edu.unlp.info.bd2.model;

import javax.persistence.*;

@Entity
public class Tag extends PersistentObject {


    @Column(name = "commitHash")
    private String commitHash;

    @Column(name = "name")
    private String name;

    public Tag (){

    }

    public Tag(String commitHash, String name) {
        this.commitHash = commitHash;
        this.name = name;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
