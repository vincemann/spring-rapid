package io.github.vincemann.demo.controllers.springAdapter;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExampleReadDto extends IdentifiableEntityImpl<Long> {
    private String name;
}
