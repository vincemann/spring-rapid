package com.github.vincemann.springrapid.authdemo.repositories;

import com.github.vincemann.springrapid.authdemo.domain.User;
import com.github.vincemann.springrapid.auth.domain.AbstractUserRepository;

public interface UserRepository extends AbstractUserRepository<User, Long> {

}