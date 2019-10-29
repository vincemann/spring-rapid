package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity;

import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.lang.Nullable;

public class RequestEntityMapper {

    private RequestEntityMapper(){}

    public static <T>RequestEntity<T> map(TestRequestEntity testRequestEntity, @Nullable T body){
        return new RequestEntity<>(body, testRequestEntity.getHeaders(), HttpMethod.resolve(testRequestEntity.getMethod().name()), testRequestEntity.getUrl());
    }
}
