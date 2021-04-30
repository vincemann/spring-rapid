package com.github.vincemann.springrapid.authtest.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.java.Log;

@AllArgsConstructor
@Getter
public class LoginForm {
    private String email;
    private String password;

}
