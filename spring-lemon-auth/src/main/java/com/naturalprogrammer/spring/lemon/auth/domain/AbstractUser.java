package com.naturalprogrammer.spring.lemon.auth.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Sets;
import com.naturalprogrammer.spring.lemon.auth.domain.dto.user.LemonUserDto;
import com.naturalprogrammer.spring.lemon.auth.util.UserUtils;
import com.naturalprogrammer.spring.lemon.auth.validation.Captcha;
import com.naturalprogrammer.spring.lemon.auth.validation.Password;
import com.naturalprogrammer.spring.lemon.auth.validation.UniqueEmail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * Base class for User entity
 * 
 * @author Sanjay Patel
 */
@Getter @Setter
@MappedSuperclass
@AllArgsConstructor
public class AbstractUser<ID extends Serializable>
	extends LemonEntity<ID>
	implements LemonUser<ID> {
	
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

	
	/**
	 * A convenient toString method
	 */
	@Override
	public String toString() {
		return "AbstractUser [id= "+getId()+"email=" + email + ", roles=" + roles + "]";
	}


	/**
	 * Makes a User DTO
	 */
	public LemonUserDto toUserDto() {

		return LemonUserDto.builder()
				.id(getId().toString())
				.email(email)
				.password(password)
				.roles(Sets.newHashSet(roles))
				.build();
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
