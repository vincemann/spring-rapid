package com.github.vincemann.springrapid.auth.dto;

import com.github.vincemann.springrapid.auth.service.val.ValidContactInformation;
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
