package com.github.vincemann.springrapid.acldemo.model.abs;

import com.github.vincemann.springrapid.acldemo.model.User;

public interface UserAware {
    User getUser();
    void setUser(User user);
}
