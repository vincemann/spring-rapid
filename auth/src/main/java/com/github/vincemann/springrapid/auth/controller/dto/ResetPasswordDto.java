package com.github.vincemann.springrapid.auth.controller.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder @ToString
public class ResetPasswordDto implements Serializable {

	@NotEmpty
	private String newPassword;
}
