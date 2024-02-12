package com.github.vincemann.springrapid.auth.dto;

import com.github.vincemann.springrapid.auth.service.val.ValidContactInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestContactInformationChangeDto {
    @NotBlank
    private String oldContactInformation;

    @NotBlank
    @ValidContactInformation
    private String newContactInformation;
}
