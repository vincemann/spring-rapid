package com.github.vincemann.springlemon.auth.domain;

import com.github.vincemann.springrapid.core.security.RapidRoles;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@Getter @ToString(callSuper = true)
public class LemonAuthenticatedPrincipal extends RapidAuthenticatedPrincipal {

	private static final long serialVersionUID = -7849730155307434535L;

	private boolean blocked = false;
	private boolean admin = false;
	private boolean goodUser = false;
	private boolean goodAdmin = false;
	private boolean unverified = false;

	public LemonAuthenticatedPrincipal(String email,String password, Set<String> roles, String id) {
		super(email, password,roles, id);
		initFlags();
	}

	public LemonAuthenticatedPrincipal(AbstractUser<?> user) {
		this(user.getEmail(), user.getPassword(),user.getRoles(), user.getId().toString());
	}

	public void initFlags() {
		//init role flags
		unverified = getRoles().contains(LemonRoles.UNVERIFIED);
		blocked = getRoles().contains(LemonRoles.BLOCKED);
		admin = getRoles().contains(RapidRoles.ADMIN);
		goodUser = !(unverified || blocked);
		goodAdmin = goodUser && admin;
	}

	public String getEmail(){
		return getName();
	}

	//removed because i dont want to support runtime roles to keep it simple, see LemonSecurityCheckerHelper

//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		//create Springs Wrapper for String authorities
//		Collection<GrantedAuthority> authorities = getRoles().stream()
//				.map(SimpleGrantedAuthority::new)
//				.collect(Collectors.toSet());
//		if (isGoodUser()) {
//
//			authorities.add(new SimpleGrantedAuthority(LemonRoles.GOOD_USER));
//
//			if (isGoodAdmin())
//				authorities.add(new SimpleGrantedAuthority(LemonRoles.GOOD_ADMIN));
//		}
//		return authorities;
//	}





	
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
