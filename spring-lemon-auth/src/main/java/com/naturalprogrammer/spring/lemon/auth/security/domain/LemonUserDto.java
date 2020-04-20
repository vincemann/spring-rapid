package com.naturalprogrammer.spring.lemon.auth.security.domain;

import io.github.vincemann.springrapid.acl.Role;
import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A lighter User class,
 * mainly used for holding logged-in user data 
 */
@Getter @Setter @ToString
public class LemonUserDto extends IdentifiableEntityImpl<String> implements Serializable {

	private static final long serialVersionUID = -9134054705405149534L;
	
//	private String id;
	private String username;
	private String password;
	private Set<String> roles = new HashSet<String>();
//	private Serializable tag;
	
	private boolean unverified = false;
	private boolean blocked = false;
	private boolean admin = false;
	private boolean goodUser = false;
	private boolean goodAdmin = false;
	
	public void initialize() {
		
		unverified = roles.contains(LemonRole.UNVERIFIED);
		blocked = roles.contains(LemonRole.BLOCKED);
		admin = roles.contains(Role.ADMIN);
		goodUser = !(unverified || blocked);
		goodAdmin = goodUser && admin;
	}
}
