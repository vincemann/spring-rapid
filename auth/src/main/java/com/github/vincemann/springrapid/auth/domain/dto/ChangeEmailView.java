package com.github.vincemann.springrapid.auth.domain.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ChangeEmailView {
    private String newEmail;
}
