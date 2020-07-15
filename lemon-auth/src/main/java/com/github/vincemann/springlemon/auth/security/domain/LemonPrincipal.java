package com.github.vincemann.springlemon.auth.security.domain;

import com.github.vincemann.springlemon.auth.domain.dto.user.LemonUserDto;
import lombok.*;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security Principal, implementing both OidcUser, UserDetails
 */
@Getter @Setter @RequiredArgsConstructor @ToString
public class LemonPrincipal implements UserDetails, CredentialsContainer {

	private static final long serialVersionUID = -7849730155307434535L;
	
	@Getter(AccessLevel.NONE)
	private final LemonUserDto lemonUserDto;
	
	private Map<String, Object> attributes;
	//username is always email in spring lemon
	private String name;
	private Map<String, Object> claims;
//	private OidcUserInfo userInfo;
//	private OidcIdToken idToken;
	
	public LemonUserDto currentUser() {
		return lemonUserDto;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		//todo sollte das nicht eher bei der creation dieses obj passieren (adden der gen roles) anstatt bei der abfrage?? fix
		Set<String> roles = lemonUserDto.getRoles();
		
		Collection<LemonGrantedAuthority> authorities = roles.stream()
				.map(role -> new LemonGrantedAuthority(role))
				.collect(Collectors.toCollection(() ->
					new ArrayList<LemonGrantedAuthority>(roles.size() + 2))); 
		
		if (lemonUserDto.isGoodUser()) {
			
			authorities.add(new LemonGrantedAuthority(LemonRole.GOOD_USER));
			
			if (lemonUserDto.isGoodAdmin())
				authorities.add(new LemonGrantedAuthority(LemonRole.GOOD_ADMIN));
		}
		
		return authorities;	
	}

	// UserDetails ...

	@Override
	public String getPassword() {

		return lemonUserDto.getPassword();
	}

	@Override
	public String getUsername() {
//username is always email in spring lemon
		return lemonUserDto.getEmail();
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
}
