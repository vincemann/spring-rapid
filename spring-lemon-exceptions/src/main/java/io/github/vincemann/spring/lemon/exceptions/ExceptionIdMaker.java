package io.github.vincemann.spring.lemon.exceptions;

@FunctionalInterface
public interface ExceptionIdMaker {

	String make(Throwable t);
}
