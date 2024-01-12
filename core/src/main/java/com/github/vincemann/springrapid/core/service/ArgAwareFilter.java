package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

public interface ArgAwareFilter {

    public void setArgs(String... args) throws BadEntityException;
}
