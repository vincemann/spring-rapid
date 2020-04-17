package com.naturalprogrammer.spring.lemon.auth.domain;

import com.naturalprogrammer.spring.lemon.auth.validation.Password;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class ResetPasswordForm {
	
	@NotBlank
	private String code;
	
	@Password
	private String newPassword;
}
