package com.github.vincemann.springrapid.acldemo.repositories;

import com.github.vincemann.springrapid.auth.domain.AbstractUserRepository;
import com.github.vincemann.springrapid.acldemo.model.User;

import java.util.Optional;

public interface UserRepository extends AbstractUserRepository<User, Long> {
    public Optional<User> findByUuid(String uuid);
}