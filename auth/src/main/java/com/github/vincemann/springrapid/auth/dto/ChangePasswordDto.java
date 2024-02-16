package com.github.vincemann.springrapid.auth.dto;

import com.github.vincemann.springrapid.auth.service.val.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ChangePasswordDto {

    @NotBlank
    String contactInformation;

    @NotBlank
    String oldPassword;

    @NotBlank
    @ValidPassword
    String newPassword;
}
