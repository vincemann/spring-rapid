package com.github.vincemann.springrapid.coredemo.lib.controller;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ExampleReadDto extends IdentifiableEntityImpl<Long> {
    private String name;
}
