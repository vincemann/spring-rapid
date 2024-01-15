package com.github.vincemann.springrapid.core.service.filter;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

// generic parameter is only set for type safety in AbstractEntityController's addAllowedExtensions
public interface UrlExtension<E> {
    public String getName();
    public void setArgs(String... args) throws BadEntityException;
}
