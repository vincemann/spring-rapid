package com.github.vincemann.springrapid.auth.domain.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * Change password form.
 * 
 * @author Sanjay Patel
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class ChangePasswordForm {
	
	@NotBlank
	private String oldPassword;

	@NotBlank
	private String password;

	@NotBlank
	private String retypePassword;
}
