package com.github.vincemann.springrapid.sync.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.vincemann.springrapid.sync.model.SyncStatus;

import java.io.IOException;

public class SyncStatusDeserializer extends JsonDeserializer<SyncStatus> {
    @Override
    public SyncStatus deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        char statusChar = jsonParser.getText().charAt(0);
        return SyncStatus.convert(statusChar);
    }


}
