package com.github.vincemann.springrapid.acldemo.user;

import com.github.vincemann.springrapid.auth.AbstractUserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
public interface UserRepository extends AbstractUserRepository<User,Long> {
    Optional<User> findByLastName(String name);
}
