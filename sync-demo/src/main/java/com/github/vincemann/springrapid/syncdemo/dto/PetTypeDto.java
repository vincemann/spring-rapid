package com.github.vincemann.springrapid.syncdemo.dto;

import com.github.vincemann.springrapid.syncdemo.dto.abs.MyIdDto;
import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PetTypeDto extends MyIdDto<Long> {
    @Size(min = 2, max = 20)
    private String name;
}
