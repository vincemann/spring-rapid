package com.github.vincemann.springrapid.auth.controller.dto.user;

import lombok.*;

import java.io.Serializable;
import java.util.Set;


@Getter @NoArgsConstructor @Setter
public class FullUserDto extends AbstractFindUserDto implements Serializable {

	private static final long serialVersionUID = -9134054705405149534L;
	
	private String password;

	@Builder
	public FullUserDto(String contactInformation, Set<String> roles, String id, String password) {
		super(contactInformation, roles,id);
		this.password = password;
	}


}
