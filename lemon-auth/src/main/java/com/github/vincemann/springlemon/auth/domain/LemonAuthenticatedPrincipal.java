package com.github.vincemann.springlemon.auth.domain;

import com.github.vincemann.springrapid.core.security.RapidRole;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@Getter @ToString
public class LemonAuthenticatedPrincipal extends RapidAuthenticatedPrincipal {

	private static final long serialVersionUID = -7849730155307434535L;

	private boolean blocked = false;
	private boolean admin = false;
	private boolean goodUser = false;
	private boolean goodAdmin = false;
	private boolean unverified = false;

	public LemonAuthenticatedPrincipal(String email,String password,String id, Set<String> roles) {
		super(email, password,roles, id);
		initFlags();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		//create Springs Wrapper for String authorities
		Collection<GrantedAuthority> authorities = getRoles().stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toSet());
		if (isGoodUser()) {

			authorities.add(new SimpleGrantedAuthority(LemonRole.GOOD_USER));

			if (isGoodAdmin())
				authorities.add(new SimpleGrantedAuthority(LemonRole.GOOD_ADMIN));
		}
		return authorities;
	}

	public void initFlags() {
		//init role flags
		unverified = getRoles().contains(LemonRole.UNVERIFIED);
		blocked = getRoles().contains(LemonRole.BLOCKED);
		admin = getRoles().contains(RapidRole.ADMIN);
		goodUser = !(unverified || blocked);
		goodAdmin = goodUser && admin;
	}

	public String getEmail(){
		return getName();
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
