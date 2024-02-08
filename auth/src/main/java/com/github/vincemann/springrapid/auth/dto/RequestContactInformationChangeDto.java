package com.github.vincemann.springrapid.auth.dto;

import lombok.*;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class RequestContactInformationChangeDto {

    @NotBlank
    private String newContactInformation;
}
