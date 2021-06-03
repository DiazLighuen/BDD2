package ar.edu.unlp.info.bd2.model;

import javax.persistence.*;

@Entity
public class File extends PersistentObject {

    @Column(name="filename")
    private String filename;

    @Column(name="content")
    private String content;

    public File (){

    }

    public File(String content, String filename) {
        this.content = content;
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
