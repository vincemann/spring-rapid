package io.github.vincemann.springlemon.demo.repositories;

import io.github.vincemann.springlemon.demo.domain.User;
import io.github.vincemann.springlemon.auth.domain.AbstractUserRepository;

public interface UserRepository extends AbstractUserRepository<User, Long> {

}