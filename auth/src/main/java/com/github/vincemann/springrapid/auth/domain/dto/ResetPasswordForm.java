package com.github.vincemann.springrapid.auth.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter @Setter @ToString
public class ResetPasswordForm {
	
	@NotBlank
	@NotEmpty
	private String code;

	@NotEmpty
//	@Password
	private String newPassword;
}
