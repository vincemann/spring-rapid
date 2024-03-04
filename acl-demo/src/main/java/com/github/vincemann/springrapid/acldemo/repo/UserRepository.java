package com.github.vincemann.springrapid.acldemo.repo;

import com.github.vincemann.springrapid.acldemo.model.abs.User;
import com.github.vincemann.springrapid.auth.model.AbstractUserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends AbstractUserRepository<User,Long> {
    Optional<User> findByLastName(String name);
}
