package com.github.vincemann.springlemon.auth.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.vincemann.springrapid.core.service.security.Role;
import com.google.common.collect.Sets;
import com.github.vincemann.springlemon.auth.domain.dto.user.LemonUserDto;
import com.github.vincemann.springlemon.auth.util.UserUtils;
import com.github.vincemann.springlemon.auth.validation.Captcha;
import com.github.vincemann.springlemon.auth.validation.Password;
import com.github.vincemann.springlemon.auth.validation.UniqueEmail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;



@Getter @Setter
@MappedSuperclass
@AllArgsConstructor
@ToString(callSuper = true)
public class AbstractUser<ID extends Serializable>
	extends LemonEntity<ID> implements AuthenticatedPrincipal, CredentialsContainer, UserDetails
		/*implements LemonUser<ID>*/ {
	
	// email
	@JsonView(UserUtils.SignupInput.class)
	@UniqueEmail(groups = {UserUtils.SignUpValidation.class})
	@Column(nullable = false, unique=true, length = UserUtils.EMAIL_MAX)
	protected String email;
	
	// password
	@JsonView(UserUtils.SignupInput.class)
	@Password(groups = {UserUtils.SignUpValidation.class, UserUtils.ChangeEmailValidation.class})
	@Column(nullable = false) // no length because it will be encrypted
	protected String password;
	
	// roles collection
	@ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="usr_role", joinColumns=@JoinColumn(name="user_id"))
    @Column(name="role")
	protected Set<String> roles = new HashSet<>();
	
	// in the email-change process, temporarily stores the new email
	@UniqueEmail(groups = {UserUtils.ChangeEmailValidation.class})
	@Column(length = UserUtils.EMAIL_MAX)
	protected String newEmail;

	// A JWT issued before this won't be valid
	@Column(nullable = false)
	@JsonIgnore
	protected long credentialsUpdatedMillis = System.currentTimeMillis();

	// holds reCAPTCHA response while signing up
	@Transient
	@JsonView(UserUtils.SignupInput.class)
	@Captcha(groups = {UserUtils.SignUpValidation.class})
	private String captchaResponse;
	
	public final boolean hasRole(String role) {
		return roles.contains(role);
	}

	public AbstractUser() {
	}

//	protected Collection<? extends GrantedAuthority> createAuthorities() {
//		initFlags();
//		//create Springs Wrapper for String authorities
//		Collection<GrantedAuthority> authorities = getRoles().stream()
//				.map(LemonGrantedAuthority::new)
//				.collect(Collectors.toSet());
//		if (isGoodUser()) {
//			authorities.add(new LemonGrantedAuthority(LemonRole.GOOD_USER));
//			if (isGoodAdmin())
//				authorities.add(new LemonGrantedAuthority(LemonRole.GOOD_ADMIN));
//		}
//		return authorities;
//	}

	@Override
	public void eraseCredentials() {
		this.password=null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream().map(LemonGrantedAuthority::new).collect(Collectors.toSet());
	}

	public boolean isUnverified() {
		return getRoles().contains(LemonRole.UNVERIFIED);
	}

	public boolean isBlocked() {
		return getRoles().contains(LemonRole.BLOCKED);
	}

	public boolean isAdmin() {
		return getRoles().contains(Role.ADMIN);
	}

	public boolean isGoodUser() {
//		return !(isUnverified() || isBlocked());
		return getRoles().contains(LemonRole.GOOD_USER);
	}

	public boolean isGoodAdmin() {
//		return isGoodUser() && isAdmin();
		return getRoles().contains(LemonRole.GOOD_ADMIN);
	}

	// UserDetails ...

	@Override
	public String getPassword() {
		return getPassword();
	}

	//username is always email in spring lemon
	@Override
	public String getName() {
		return email;
	}

	//username is always email in spring lemon
	@Override
	public String getUsername() {
		return email;
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

	
//	/**
//	 * A convenient toString method
//	 */
//	@Override
//	public String toString() {
//		return "AbstractUser [id= "+getId()+"email=" + email + ", roles=" + roles + "]";
//	}


//	/**
//	 * Makes a User DTO
//	 */
//	public LemonUserDto toUserDto() {
//
//		return LemonUserDto.builder()
//				.id(getId().toString())
//				.email(email)
//				.password(password)
//				.roles(Sets.newHashSet(roles))
//				.build();
//		userDto.setId(getId().toString());
//		userDto.setEmail(email);
//		userDto.setPassword(password);
//
//		// roles would be org.hibernate.collection.internal.PersistentSet,
//		// which is not in another microservices not having Hibernate.
//		// So, let's convert it to HashSet
//		userDto.setRoles(new HashSet<String>(roles));
//
////		userDto.setTag(toTag());
//
//		userDto.initialize();

//		return userDto;
	}

//	/**
//	 * Override this to supply any additional fields to the user DTO,
//	 * e.g. name
//	 */
//	protected Serializable toTag() {
//
//		return null;
//	}
}
