package com.github.vincemann.springrapid.auth.dto;


import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ChangeContactInformationView {
    private String newContactInformation;
}
