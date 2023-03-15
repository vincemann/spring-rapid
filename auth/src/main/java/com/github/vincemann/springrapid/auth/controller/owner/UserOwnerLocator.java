package com.github.vincemann.springrapid.auth.controller.owner;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * The owner of a user is always the user itself. So return its {@link AbstractUser#getContactInformation()};
 */
@Order(100)
public class UserOwnerLocator implements OwnerLocator<AbstractUser<?>> {

    @Override
    public boolean supports(Class clazz) {
        return AbstractUser.class.isAssignableFrom(clazz);
    }

    @Override
    public Optional<String> find(AbstractUser<?> entity) {
        return Optional.ofNullable(entity.getContactInformation());
    }
}
