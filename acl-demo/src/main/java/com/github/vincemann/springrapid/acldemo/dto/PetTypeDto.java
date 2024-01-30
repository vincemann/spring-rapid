package com.github.vincemann.springrapid.acldemo.dto;

import com.github.vincemann.springrapid.core.dto.IdAwareDto;
import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor

public class PetTypeDto extends IdAwareDto<Long> {
    @Size(min = 2, max = 20)
    private String name;
}
