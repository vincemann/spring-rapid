package com.github.vincemann.springrapid.auth.controller.dto;

import com.github.vincemann.springrapid.auth.service.val.ValidPassword;
import lombok.*;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Change password form.
 * 
 * @author Sanjay Patel
 * @modifiedBy vincemann
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString @Builder
public class ChangePasswordDto implements Serializable {
	
	@NotBlank
	private String oldPassword;

	@NotBlank
	@ValidPassword
	private String newPassword;

	@NotBlank
	@ValidPassword
	private String retypeNewPassword;

}
