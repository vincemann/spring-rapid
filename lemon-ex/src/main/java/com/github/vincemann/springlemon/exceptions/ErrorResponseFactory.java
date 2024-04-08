package com.github.vincemann.springlemon.exceptions;



import java.util.Optional;

public interface ErrorResponseFactory<T extends Throwable>
{

    public Optional<ErrorResponse> create(T ex);

}
