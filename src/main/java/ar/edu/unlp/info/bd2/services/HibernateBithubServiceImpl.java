package ar.edu.unlp.info.bd2.services;

import ar.edu.unlp.info.bd2.repositories.HibernateBithubRepository;

public class HibernateBithubServiceImpl extends BithubServiceImpl<Long> {
    private HibernateBithubRepository repository;

    public HibernateBithubServiceImpl(HibernateBithubRepository repository) {
        this.repository = repository;
    }
}
