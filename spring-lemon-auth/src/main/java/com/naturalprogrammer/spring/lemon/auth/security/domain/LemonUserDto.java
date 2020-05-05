package com.naturalprogrammer.spring.lemon.auth.security.domain;

import io.github.vincemann.springrapid.acl.Role;
import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A lighter User class,
 * mainly used for holding logged-in user data 
 */
@Getter @Setter @ToString @NoArgsConstructor
public class LemonUserDto extends IdentifiableEntityImpl<String> implements Serializable {

	private static final long serialVersionUID = -9134054705405149534L;
	
//	private String id;
	private String email;
	private String password;
	private Set<String> roles = new HashSet<String>();
//	private Serializable tag;
	
	private boolean unverified = false;
	private boolean blocked = false;
	private boolean admin = false;
	private boolean goodUser = false;
	private boolean goodAdmin = false;

	@Builder
	public LemonUserDto(String email, String password, Set<String> roles, boolean unverified, boolean blocked, boolean admin, boolean goodUser, boolean goodAdmin) {
		this.email = email;
		this.password = password;
		this.roles = roles;
		this.unverified = unverified;
		this.blocked = blocked;
		this.admin = admin;
		this.goodUser = goodUser;
		this.goodAdmin = goodAdmin;
	}

	public void initialize() {
		
		unverified = roles.contains(LemonRole.UNVERIFIED);
		blocked = roles.contains(LemonRole.BLOCKED);
		admin = roles.contains(Role.ADMIN);
		goodUser = !(unverified || blocked);
		goodAdmin = goodUser && admin;
	}
}
