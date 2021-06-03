package ar.edu.unlp.info.bd2.config;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import ar.edu.unlp.info.bd2.repositories.MongoDBBithubRepository;
import ar.edu.unlp.info.bd2.services.BithubService;
import ar.edu.unlp.info.bd2.services.MongoDBBithubServiceImpl;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class MongoDBConfiguration {

  @Bean
  public BithubService mongoDBService() {
    MongoDBBithubRepository repository = this.createMongoDBRepository();
    return new MongoDBBithubServiceImpl(repository);
  }

  @Bean
  public MongoDBBithubRepository createMongoDBRepository() {
    return new MongoDBBithubRepository();
  }

  @Bean
  public MongoDatabase mongoDatabase() {
    try (MongoClient client = MongoClients.create("mongodb://localhost:27017")) {
      return client.getDatabase("bd2");
    }
  }

  @Bean
  @Qualifier("mongoPojoDatabase")
  public MongoClient mongoPojoDatabase() {
    CodecRegistry pojoCodecRegistry =
        fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    MongoClient client =
        MongoClients.create(MongoClientSettings.builder().codecRegistry(pojoCodecRegistry).build());
    return client;
  }

  @Bean
  public PlatformTransactionManager dummyTransactionManager() {
    return new PlatformTransactionManager() {
      @Override
      public TransactionStatus getTransaction(TransactionDefinition transactionDefinition)
          throws TransactionException {
        return null;
      }

      @Override
      public void commit(TransactionStatus transactionStatus) throws TransactionException {}

      @Override
      public void rollback(TransactionStatus transactionStatus) throws TransactionException {}
    };
  }
}
