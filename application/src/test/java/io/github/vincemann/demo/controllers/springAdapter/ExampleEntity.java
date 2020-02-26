package io.github.vincemann.demo.controllers.springAdapter;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ExampleEntity extends IdentifiableEntityImpl<Long> {
    private String name;
}
