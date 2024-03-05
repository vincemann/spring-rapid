package com.github.vincemann.springrapid.auth.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
public class ChangePasswordDto {
    String contactInformation;
    String oldPassword;
    String newPassword;
}
