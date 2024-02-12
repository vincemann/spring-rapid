package com.github.vincemann.springrapid.authdemo.dto;

import com.github.vincemann.springrapid.auth.dto.RequestContactInformationChangeDto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


public class RequestEmailChangeDto extends RequestContactInformationChangeDto {

    @Email
    @NotBlank
    @Override
    public String getNewContactInformation() {
        return super.getNewContactInformation();
    }
}
