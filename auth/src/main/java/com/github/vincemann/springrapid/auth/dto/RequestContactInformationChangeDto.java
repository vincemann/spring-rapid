package com.github.vincemann.springrapid.auth.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
public class RequestContactInformationChangeDto {
    private String oldContactInformation;
    private String newContactInformation;
}
