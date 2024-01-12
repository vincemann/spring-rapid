package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

import java.util.List;

public interface ArgAwareFilter {

    public void setArgs(String... args) throws BadEntityException;
}
