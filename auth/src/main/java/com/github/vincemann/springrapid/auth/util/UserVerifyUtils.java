package com.github.vincemann.springrapid.auth.util;

public class UserVerifyUtils {

	public static final int CONTACT_INFORMATION_MIN = 4;
	public static final int CONTACT_INFORMATION_MAX = 250;
	public static final int UUID_LENGTH = 36;
	public static final int PASSWORD_MAX = 50;
	public static final int PASSWORD_MIN = 6;

	//todo how does this work
	//hints? wie werden die interpretiert
	// validation groups
	public interface SignUpValidation {
	}

	public interface UpdateValidation {
	}

	public interface ChangeContactInformationValidation {
	}

	// JsonView for Sign up
	public interface SignupInput {
	}

//	public static <ID> boolean hasPermission(ID id, UserDto currentUser, String permission) {
//
//		log.debug("Computing " + permission + " permission for User " + id + "\n  Logged in user: " + currentUser);
//
//		if (permission.equals("edit")) {
//
//			if (currentUser == null)
//				return false;
//
//			boolean isSelf = currentUser.getId().equals(id.toString());
//			return isSelf || currentUser.isGoodAdmin(); // self or admin;
//		}
//
//		return false;
//	}

	//todo das in mein acl module auslagern, auch als interfaces und auth module dependet dann auf mein acl module
//	/**
//	 * Role constants. To allow extensibility, this couldn't be made an enum
//	 */
//	public interface Role {
//
//		static final String UNVERIFIED = "UNVERIFIED";
//		static final String BLOCKED = "BLOCKED";
//		static final String ADMIN = "ADMIN";
//	}
//
//	public interface Permission {
//
//		static final String EDIT = "edit";
//	}
}
