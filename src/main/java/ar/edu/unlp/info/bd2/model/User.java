package ar.edu.unlp.info.bd2.model;


import javax.persistence.*;

@Entity
public class User extends PersistentObject{

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    public User (){

    }

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
