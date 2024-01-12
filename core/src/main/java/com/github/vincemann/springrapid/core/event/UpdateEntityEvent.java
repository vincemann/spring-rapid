package com.github.vincemann.springrapid.core.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
public class UpdateEntityEvent {
    private Set<String> fields;

    public UpdateEntityEvent(Set<String> fields) {
        this.fields = fields;
    }
}
