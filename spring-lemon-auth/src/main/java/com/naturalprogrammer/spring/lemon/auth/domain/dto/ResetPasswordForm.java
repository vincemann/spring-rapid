package com.naturalprogrammer.spring.lemon.auth.domain.dto;

import com.naturalprogrammer.spring.lemon.auth.validation.Password;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class ResetPasswordForm {
	
	@NotBlank
	@NotEmpty
	private String code;

	@NotEmpty
	@Password
	private String newPassword;
}
