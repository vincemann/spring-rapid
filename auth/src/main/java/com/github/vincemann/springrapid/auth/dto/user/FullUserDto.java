package com.github.vincemann.springrapid.auth.dto.user;

import com.github.vincemann.springrapid.auth.dto.user.abs.AbstractFindUserDto;
import lombok.*;

import java.util.Set;


@Getter
@NoArgsConstructor
@Setter
public class FullUserDto extends AbstractFindUserDto {
	private String password;

	@Builder
	public FullUserDto(String contactInformation, Set<String> roles, String id, String password) {
		super(contactInformation, roles,id);
		this.password = password;
	}

	@Override
	public String toString() {
		return "FullUserDto{" +
				"password='" + password + '\'' +
				", verified=" + isVerified() +
				", blocked=" + isBlocked() +
				", admin=" + isAdmin() +
				", goodUser=" + isGoodUser() +
				", id='" + getId() + '\'' +
				", contactInformation='" + getContactInformation() + '\'' +
				", roles=" + getRoles() +
				'}';
	}
}
