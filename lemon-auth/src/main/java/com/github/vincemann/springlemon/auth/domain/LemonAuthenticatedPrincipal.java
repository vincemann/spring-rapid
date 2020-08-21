package com.github.vincemann.springlemon.auth.domain;

import com.github.vincemann.springrapid.core.service.security.Role;
import com.github.vincemann.springrapid.core.service.security.AbstractAuthenticatedPrincipal;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@Getter @ToString
public class LemonAuthenticatedPrincipal extends AbstractAuthenticatedPrincipal  {

	private static final long serialVersionUID = -7849730155307434535L;

	private boolean blocked = false;
	private boolean admin = false;
	private boolean goodUser = false;
	private boolean goodAdmin = false;
	private boolean unverified = false;

	public LemonAuthenticatedPrincipal(String name,String password, Set<String> roles) {
		super(name, password,roles);
	}

	@Override
	protected Collection<? extends GrantedAuthority> createAuthorities() {
		initFlags();
		//create Springs Wrapper for String authorities
		Collection<GrantedAuthority> authorities = getRoles().stream()
				.map(LemonGrantedAuthority::new)
				.collect(Collectors.toSet());
		if (isGoodUser()) {

			authorities.add(new LemonGrantedAuthority(LemonRole.GOOD_USER));

			if (isGoodAdmin())
				authorities.add(new LemonGrantedAuthority(LemonRole.GOOD_ADMIN));
		}
		return authorities;
	}

	public void initFlags() {
		//init role flags
		unverified = getRoles().contains(LemonRole.UNVERIFIED);
		blocked = getRoles().contains(LemonRole.BLOCKED);
		admin = getRoles().contains(Role.ADMIN);
		goodUser = !(unverified || blocked);
		goodAdmin = goodUser && admin;
	}
	
//	public LemonUserDto currentUser() {
//		return lemonUserDto;
//	}



//	@Override
//	public void eraseCredentials() {
//
//		lemonUserDto.setPassword(null);
//		attributes = null;
//		claims = null;
////		userInfo = null;
////		idToken = null;
//	}


	//	private Map<String, Object> attributes;
//	@Getter(AccessLevel.NONE)
//	private final LemonUserDto lemonUserDto;

//	private String name;
//	private Map<String, Object> claims;
//	private OidcUserInfo userInfo;
//	private OidcIdToken idToken;
}
