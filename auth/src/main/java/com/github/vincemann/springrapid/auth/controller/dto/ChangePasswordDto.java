package com.github.vincemann.springrapid.auth.controller.dto;

import lombok.*;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
