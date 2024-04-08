package com.github.vincemann.springrapid.lemon.exceptions;

@FunctionalInterface
public interface ExceptionIdMaker {

	String make(Throwable t);
}
