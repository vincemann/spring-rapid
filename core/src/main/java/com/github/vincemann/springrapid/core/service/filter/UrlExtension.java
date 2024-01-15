package com.github.vincemann.springrapid.core.service.filter;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

public interface UrlExtension {
    public String getName();
    public void setArgs(String... args) throws BadEntityException;
}
