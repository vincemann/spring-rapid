package com.github.vincemann.springrapid.authtest.controller.template;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.java.Log;

@AllArgsConstructor
@Getter
public class LoginDto {
    private String email;
    private String password;

}
