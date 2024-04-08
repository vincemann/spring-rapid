package com.github.vincemann.springrapid.authdemo;

import com.github.vincemann.springrapid.auth.AbstractUserRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends AbstractUserRepository<User, Long> {

}