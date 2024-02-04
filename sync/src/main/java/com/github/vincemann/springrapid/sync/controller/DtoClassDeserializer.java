package com.github.vincemann.springrapid.sync.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.vincemann.springrapid.sync.DtoClassRegistry;

import java.io.IOException;
import java.util.function.Supplier;

public class DtoClassDeserializer extends JsonDeserializer<Class<?>> {

    private Supplier<DtoClassRegistry> registry;

    public DtoClassDeserializer(Supplier<DtoClassRegistry> registry) {
        this.registry = registry;
    }

    @Override
    public Class<?> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String key = p.getText();
        return registry.get().find(key);
    }
}