package com.github.vincemann.springrapid.acldemo.model.abs;

import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.auth.model.AuthenticatingEntity;

public interface UserAwareEntity extends AuthenticatingEntity<Long> {
    User getUser();
    void setUser(User user);

}
