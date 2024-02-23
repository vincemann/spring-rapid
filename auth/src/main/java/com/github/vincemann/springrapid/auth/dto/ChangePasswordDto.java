package com.github.vincemann.springrapid.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ChangePasswordDto {
    String contactInformation;
    String oldPassword;
    String newPassword;
}
