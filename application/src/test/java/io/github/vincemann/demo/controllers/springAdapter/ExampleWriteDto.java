package io.github.vincemann.demo.controllers.springAdapter;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ExampleWriteDto extends IdentifiableEntityImpl<Long> {
    private String name;
}
