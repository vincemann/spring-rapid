package com.github.vincemann.springrapid.auth.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Change password form.
 * 
 * @author Sanjay Patel
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString @Builder
public class ChangePasswordDto implements Serializable {
	
	@NotBlank
	private String oldPassword;

	@NotBlank
	private String newPassword;

	@NotBlank
	private String retypeNewPassword;
}
