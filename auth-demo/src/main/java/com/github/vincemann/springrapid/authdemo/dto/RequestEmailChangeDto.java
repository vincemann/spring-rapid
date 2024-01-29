package com.github.vincemann.springrapid.authdemo.dto;

import com.github.vincemann.springrapid.auth.controller.dto.RequestContactInformationChangeDto;

import javax.validation.constraints.Email;


public class RequestEmailChangeDto extends RequestContactInformationChangeDto {

    @Email
    @Override
    public String getNewContactInformation() {
        return super.getNewContactInformation();
    }
}
