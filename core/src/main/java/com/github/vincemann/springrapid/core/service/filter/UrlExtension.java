package com.github.vincemann.springrapid.core.service.filter;

import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

import java.io.Serializable;

// generic parameter is only set for type safety in AbstractEntityController's addAllowedExtensions
public interface UrlExtension<E> {
    public String getName();
    public void setArgs(String... args) throws BadEntityException;

    default Long parseLong(String arg) throws BadEntityException {
        try {
            return Long.valueOf(arg);
        }catch (NumberFormatException e){
            throw new BadEntityException("Invalid arg type: " + arg);
        }
    }

    default <T extends Serializable> T parseId(String arg, IdConverter<T> idConverter) throws BadEntityException {
        try {
            return idConverter.toId(arg);
        }catch (ClassCastException|NumberFormatException e){
            throw new BadEntityException("Invalid id type for " + arg +". Need " + idConverter.getIdType().getSimpleName());
        }
    }

    default void assertAmountArgs(int amount, String[] args) throws BadEntityException {
        if (args.length != amount)
            throw new BadEntityException("Need exactly " + amount + " args, supplied " + args.length);
    }
}
