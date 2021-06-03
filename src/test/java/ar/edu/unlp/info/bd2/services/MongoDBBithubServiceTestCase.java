package ar.edu.unlp.info.bd2.services;

import ar.edu.unlp.info.bd2.config.MongoDBConfiguration;
import ar.edu.unlp.info.bd2.model.PersistentObject;
import com.mongodb.client.MongoClient;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {MongoDBConfiguration.class},
    loader = AnnotationConfigContextLoader.class)
public class MongoDBBithubServiceTestCase extends BithubServiceTestCase<ObjectId> {

  @Autowired private MongoClient client;

  @BeforeEach
  public void setUp() {
    super.setUp();
    this.client.getDatabase("bd2").drop();
  }

  @Autowired
  @Qualifier("mongoDBService")
  private BithubService service;

  @Override
  protected BithubService getService() {
    return this.service;
  }

  @Override
  protected ObjectId getId(PersistentObject object) {
    return object.getObjectId();
  }
}
