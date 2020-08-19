package com.github.vincemann.springlemon.auth.domain;

import com.github.vincemann.springrapid.acl.Role;
import com.github.vincemann.springrapid.core.service.RapidAuthenticatedPrincipal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security Principal, implementing both OidcUser, UserDetails
 */
@Getter @ToString
public class LemonAuthenticatedPrincipal extends RapidAuthenticatedPrincipal implements UserDetails, CredentialsContainer {

	private static final long serialVersionUID = -7849730155307434535L;

	public LemonAuthenticatedPrincipal(String name,String password, Set<String> roles) {
		super(name, roles);
		initialize();
	}

	private boolean blocked = false;
	private boolean admin = false;
	private boolean goodUser = false;
	private boolean goodAdmin = false;
	private boolean unverified = false;

	/**
	 * Same as {@link this#getRoles()} but wrapped in {@link LemonGrantedAuthority}
	 */
	private Collection<? extends GrantedAuthority> authorities;



	public void initialize() {
		//init role flags
		unverified = getRoles().contains(LemonRole.UNVERIFIED);
		blocked = getRoles().contains(LemonRole.BLOCKED);
		admin = getRoles().contains(Role.ADMIN);
		goodUser = !(unverified || blocked);
		goodAdmin = goodUser && admin;

		//create Springs Wrapper for String authorities
		Collection<GrantedAuthority> authorities = getRoles().stream()
				.map(role -> new LemonGrantedAuthority(role))
				.collect(Collectors.toSet());

		if (isGoodUser()) {

			authorities.add(new LemonGrantedAuthority(LemonRole.GOOD_USER));

			if (isGoodAdmin())
				authorities.add(new LemonGrantedAuthority(LemonRole.GOOD_ADMIN));
		}
		this.authorities=authorities;
	}
	
//	public LemonUserDto currentUser() {
//		return lemonUserDto;
//	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	// UserDetails ...

	@Override
	public String getPassword() {
		return getPassword();
	}

	@Override
	public String getName() {
		return getName();
	}

	//username is always email in spring lemon
	@Override
	public String getUsername() {
		return getName();
	}

	@Override
	public boolean isAccountNonExpired() {

		return true;
	}

	@Override
	public boolean isAccountNonLocked() {

		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {

		return true;
	}

	@Override
	public boolean isEnabled() {

		return true;
	}

	@Override
	public void eraseCredentials() {

		lemonUserDto.setPassword(null);
		attributes = null;
		claims = null;
//		userInfo = null;
//		idToken = null;
	}


	//	private Map<String, Object> attributes;
//	@Getter(AccessLevel.NONE)
//	private final LemonUserDto lemonUserDto;

//	private String name;
//	private Map<String, Object> claims;
//	private OidcUserInfo userInfo;
//	private OidcIdToken idToken;
}
