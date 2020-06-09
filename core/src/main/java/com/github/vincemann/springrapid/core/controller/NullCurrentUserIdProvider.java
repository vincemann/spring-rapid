package com.github.vincemann.springrapid.core.controller;

import com.github.vincemann.springrapid.core.controller.rapid.CurrentUserIdProvider;

public class NullCurrentUserIdProvider implements CurrentUserIdProvider {

    @Override
    public String find() {
        return null;
    }
}
