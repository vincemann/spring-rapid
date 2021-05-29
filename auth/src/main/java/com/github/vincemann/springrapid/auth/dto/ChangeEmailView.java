package com.github.vincemann.springrapid.auth.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ChangeEmailView {
    private String newEmail;
}
