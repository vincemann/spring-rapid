package com.github.vincemann.springrapid.auth.domain.dto.user;

import lombok.*;

import java.io.Serializable;
import java.util.Set;


@Getter @NoArgsConstructor @Setter @ToString(callSuper = true)
public class RapidFullUserDto extends AbstractFindRapidUserDto implements Serializable {

	private static final long serialVersionUID = -9134054705405149534L;
	
	private String password;

	@Builder
	public RapidFullUserDto(String email, Set<String> roles, String id, String password) {
		super(email, roles,id);
		this.password = password;
	}


}
