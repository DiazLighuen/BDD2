package ar.edu.unlp.info.bd2.services;

import ar.edu.unlp.info.bd2.repositories.MongoDBBithubRepository;
import org.bson.types.ObjectId;

public class MongoDBBithubServiceImpl extends BithubServiceImpl<ObjectId> {
    private MongoDBBithubRepository repository;

    public MongoDBBithubServiceImpl(MongoDBBithubRepository repository) {
        this.repository = repository;
    }
}
