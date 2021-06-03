package ar.edu.unlp.info.bd2.model;

import javax.persistence.*;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

@MappedSuperclass
public abstract class PersistentObject {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  protected Long id;

  @BsonId private ObjectId objectId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ObjectId getObjectId() {
    return objectId;
  }

  public void setObjectId(ObjectId objectId) {
    this.objectId = objectId;
  }
}
