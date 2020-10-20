package com.github.vincemann.springrapid.demo.lib.controller;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ExampleWriteDto extends IdentifiableEntityImpl<Long> {
    private String name;
}
