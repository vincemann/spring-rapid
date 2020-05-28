package io.github.vincemann.springlemon.auth.domain.dto;

import io.github.vincemann.springlemon.auth.validation.Password;
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
