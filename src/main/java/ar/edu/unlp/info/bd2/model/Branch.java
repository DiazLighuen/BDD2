package ar.edu.unlp.info.bd2.model;

import org.bson.codecs.pojo.annotations.BsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Branch extends PersistentObject {

    @Column(name = "name")
    private String name;

    @BsonIgnore
    @OneToMany(mappedBy = "branch")
    private List<Commit> commits;

    public Branch (){

    }

    public Branch(String name) {
        this.name = name;
        this.commits = new ArrayList<Commit>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCommits(List<Commit> commits) {
        this.commits = commits;
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public void addCommit (Commit commit) {
        this.commits.add(commit);
    }
}
