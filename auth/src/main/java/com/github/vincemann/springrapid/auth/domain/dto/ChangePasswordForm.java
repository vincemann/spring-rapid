package com.github.vincemann.springrapid.auth.domain.dto;

import com.github.vincemann.springrapid.auth.validation.Password;
import com.github.vincemann.springrapid.auth.validation.RetypePassword;
import com.github.vincemann.springrapid.auth.validation.RetypePasswordForm;

import lombok.*;

/**
 * Change password form.
 * 
 * @author Sanjay Patel
 */
@RetypePassword
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class ChangePasswordForm implements RetypePasswordForm {
	
	@Password
	private String oldPassword;

	@Password
	private String password;
	
	@Password
	private String retypePassword;
}
