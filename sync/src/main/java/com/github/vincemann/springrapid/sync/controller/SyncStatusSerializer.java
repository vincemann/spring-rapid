package com.github.vincemann.springrapid.sync.controller;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.vincemann.springrapid.sync.model.SyncStatus;

import java.io.IOException;

public class SyncStatusSerializer extends JsonSerializer<SyncStatus> {

    @Override
    public void serialize(SyncStatus syncStatus, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        Character c = SyncStatus.convert(syncStatus);
        jsonGenerator.writeString(c.toString());
    }
}
