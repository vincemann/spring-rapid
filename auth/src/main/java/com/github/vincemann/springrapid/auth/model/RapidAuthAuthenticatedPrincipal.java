package com.github.vincemann.springrapid.auth.model;

import com.github.vincemann.springrapid.core.security.Roles;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;


@Getter @NoArgsConstructor
public class RapidAuthAuthenticatedPrincipal extends RapidAuthenticatedPrincipal {

	private static final long serialVersionUID = -7849730155307434535L;

	private boolean blocked = false;
	private boolean admin = false;
	private boolean goodUser = false;
	private boolean verified = false;
	private boolean anon = false;

	public RapidAuthAuthenticatedPrincipal(String contactInformation, String password, Set<String> roles, String id) {
		super(contactInformation, password,roles, id);
		initFlags();
	}

	public RapidAuthAuthenticatedPrincipal(AbstractUser<?> user) {
		this(user.getContactInformation(), user.getPassword(),user.getRoles(), user.getId() == null? null : user.getId().toString());
	}
	public RapidAuthAuthenticatedPrincipal(RapidAuthAuthenticatedPrincipal user) {
		this(user.getContactInformation(), user.getPassword(),user.getRoles(), user.getId());
	}

	public void initFlags() {
		//init role flags
		verified = !getRoles().contains(AuthRoles.UNVERIFIED);
		blocked = getRoles().contains(AuthRoles.BLOCKED);
		admin = getRoles().contains(Roles.ADMIN);
		goodUser = (getRoles().contains(AuthRoles.USER) | admin) && verified && !blocked;
		anon = getRoles().contains(AuthRoles.ANON);
//		goodAdmin = goodUser && admin;
	}

	public String getContactInformation(){
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
