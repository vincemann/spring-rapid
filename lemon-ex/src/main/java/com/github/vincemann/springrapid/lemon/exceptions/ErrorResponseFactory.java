package com.github.vincemann.springrapid.lemon.exceptions;



import java.util.Optional;

public interface ErrorResponseFactory<T extends Throwable>
{

    public Optional<ErrorResponse> create(T ex);

}
