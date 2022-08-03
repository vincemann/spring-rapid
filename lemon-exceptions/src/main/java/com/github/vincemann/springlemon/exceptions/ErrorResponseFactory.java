package com.github.vincemann.springlemon.exceptions;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;

import java.util.Optional;

public interface ErrorResponseFactory<T extends Throwable>
        extends AopLoggable {

    @LogInteraction
    public Optional<ErrorResponse> create(T ex);

}
