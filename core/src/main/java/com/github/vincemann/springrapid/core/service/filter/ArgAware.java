package com.github.vincemann.springrapid.core.service.filter;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

public interface ArgAware {
    public void setArgs(String... args) throws BadEntityException;
}
