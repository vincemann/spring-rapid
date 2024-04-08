package com.github.vincemann.springrapid.authdemo;

import com.github.vincemann.springrapid.auth.AbstractUser;
import com.github.vincemann.springrapid.authdemo.service.ValidUsername;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usr", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"contactInformation"}),
		@UniqueConstraint(columnNames = {"name"})
})
@Getter
@Setter
@NoArgsConstructor
public class User extends AbstractUser<Long> {

    private static final long serialVersionUID = 2716710947175132319L;


	public User(String contactInformation, String password, String name, String... roles) {
		this.contactInformation = contactInformation;
		this.password = password;
		this.roles= new HashSet<>(Arrays.asList(roles));
		this.name = name;
	}

	@Builder
	public User(String contactInformation, String password, String name, Set<String> roles, String newContactInformation, long credentialsUpdatedMillis) {
		super(contactInformation,newContactInformation, password, roles, credentialsUpdatedMillis);
		this.name = name;
	}

	@NotBlank(message = "{blank.name}")
    @ValidUsername
    @Column(nullable = false,unique = true, length = ValidUsername.MAX_SIZE)
    private String name;

	@Email
	@Column(nullable = true, length = CONTACT_INFORMATION_MAX)
	public String getNewContactInformation() {
		return newContactInformation;
	}

	@NotBlank
	@Email
	@Override
	@Column(nullable = false, unique = true, length = CONTACT_INFORMATION_MAX)
	public String getContactInformation() {
		return super.getContactInformation();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
}