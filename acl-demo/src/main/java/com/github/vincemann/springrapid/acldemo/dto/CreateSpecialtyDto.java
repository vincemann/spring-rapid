package com.github.vincemann.springrapid.acldemo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class CreateSpecialtyDto {
    @Size(min = 2, max = 255)
    private String description;
}
