package io.github.vincemann.springlemon.auth.domain.dto.user;

import lombok.*;

import java.io.Serializable;
import java.util.Set;

/**
 * A lighter User class,
 * mainly used for holding logged-in user data 
 */
@Getter @NoArgsConstructor @Setter @ToString(callSuper = true)
public class LemonUserDto extends AbstractLemonUserDto implements Serializable {

	private static final long serialVersionUID = -9134054705405149534L;
	
	private String password;

	@Builder
	public LemonUserDto( String email,  Set<String> roles, String id, String password) {
		super(email, roles,id);
		this.password = password;
	}


}
