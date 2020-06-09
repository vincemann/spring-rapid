package com.github.vincemann.springlemon.demo.repositories;

import com.github.vincemann.springlemon.demo.domain.User;
import com.github.vincemann.springlemon.auth.domain.AbstractUserRepository;

public interface UserRepository extends AbstractUserRepository<User, Long> {

}