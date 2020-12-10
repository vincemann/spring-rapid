package com.github.vincemann.springrapid.auth.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.vincemann.springrapid.auth.util.UserVerifyUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Getter @Setter
@MappedSuperclass
@AllArgsConstructor
@ToString(callSuper = true)
public class AbstractUser<ID extends Serializable>
	extends AuditingEntity<ID>
{
	
	// email
	@JsonView(UserVerifyUtils.SignupInput.class)
//	@UniqueEmail(groups = {UserVerifyUtils.SignUpValidation.class})
	@Email
	@Column(nullable = false, unique=true, length = UserVerifyUtils.EMAIL_MAX)
	protected String email;
	
	// password
	@JsonView(UserVerifyUtils.SignupInput.class)
//	@Password(/*groups = {UserVerifyUtils.SignUpValidation.class, UserVerifyUtils.ChangeEmailValidation.class}*/)
	@Column(nullable = false) // no length because it will be encrypted
	protected String password;
	
	// roles collection
	@ElementCollection(fetch = FetchType.EAGER)
	//todo change this to user_role ??
    @CollectionTable(name="usr_role", joinColumns=@JoinColumn(name="user_id"))
    @Column(name="role")
	protected Set<String> roles = new HashSet<>();
	
	// in the email-change process, temporarily stores the new email
//	@UniqueEmail(groups = {UserVerifyUtils.ChangeEmailValidation.class})
	@Column(length = UserVerifyUtils.EMAIL_MAX)
	protected String newEmail;

	// A JWT issued before this won't be valid
	@Column(nullable = false)
	@JsonIgnore
	protected long credentialsUpdatedMillis = System.currentTimeMillis();

	// holds reCAPTCHA response while signing up
	// todo put captcha response in signupDto and validate there
	@Transient
	@JsonView(UserVerifyUtils.SignupInput.class)
//	@Captcha(groups = {UserVerifyUtils.SignUpValidation.class})
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

