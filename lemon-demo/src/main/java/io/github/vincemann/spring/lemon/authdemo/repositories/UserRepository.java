package io.github.vincemann.spring.lemon.authdemo.repositories;

import io.github.spring.lemon.auth.domain.AbstractUserRepository;
import io.github.vincemann.spring.lemon.authdemo.domain.User;

public interface UserRepository extends AbstractUserRepository<User, Long> {

}