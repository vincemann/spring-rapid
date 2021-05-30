package com.github.vincemann.springrapid.auth.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor @Builder
public class ResetPasswordDto implements Serializable {

	@NotEmpty
	private String newPassword;
}
