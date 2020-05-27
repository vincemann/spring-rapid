package io.github.spring.lemon.auth.domain.dto;

import io.github.spring.lemon.auth.validation.Password;
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
	@Password
	private String newPassword;
}
