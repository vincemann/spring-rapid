package io.github.vincemann.demo.lib.controller;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ExampleReadDto extends IdentifiableEntityImpl<Long> {
    private String name;
}
