package com.github.vincemann.springrapid.acldemo.repo;

import com.github.vincemann.springrapid.auth.model.AbstractUserRepository;
import com.github.vincemann.springrapid.acldemo.model.User;

import java.util.Optional;

public interface UserRepository extends AbstractUserRepository<User, Long> {
    public Optional<User> findByUuid(String uuid);
}