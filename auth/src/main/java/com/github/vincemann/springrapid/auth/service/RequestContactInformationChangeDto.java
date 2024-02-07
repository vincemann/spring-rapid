package com.github.vincemann.springrapid.auth.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Data
public class RequestContactInformationChangeDto {
    @NotBlank
    private String oldContactInformation;
    @NotBlank
    private String newContactInformation;
}
