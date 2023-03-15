package com.github.vincemann.springrapid.authdemo.repository;

import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.auth.model.AbstractUserRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends AbstractUserRepository<User, Long> {

}