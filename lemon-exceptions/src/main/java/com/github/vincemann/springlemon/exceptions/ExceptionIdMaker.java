package com.github.vincemann.springlemon.exceptions;

@FunctionalInterface
public interface ExceptionIdMaker {

	String make(Throwable t);
}
