package com.naturalprogrammer.spring.lemon.authdemo.repositories;

import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUserRepository;
import com.naturalprogrammer.spring.lemon.authdemo.domain.User;

public interface UserRepository extends AbstractUserRepository<User, Long> {

}