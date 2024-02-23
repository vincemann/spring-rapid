package com.github.vincemann.springrapid.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestContactInformationChangeDto {
    private String oldContactInformation;
    private String newContactInformation;
}
