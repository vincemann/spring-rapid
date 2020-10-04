package com.github.vincemann.springlemon.demo;

import com.github.vincemann.springlemon.auth.controller.AbstractUserController;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.demo.domain.User;
import com.github.vincemann.springlemon.auth.service.UserService;
import com.github.vincemann.springlemon.auth.domain.LemonRoles;
import com.github.vincemann.springrapid.core.security.RapidRoles;

import com.github.vincemann.springrapid.core.util.ResourceUtils;
import com.github.vincemann.springrapid.coretest.controller.rapid.UrlParamIdRapidControllerTest;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import lombok.Getter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UpdateUserMvcTests extends AbstractMvcTests
		implements UrlParamIdRapidControllerTest<UserService<User,Long>,User,Long> {
	
//	private static final String UPDATED_NAME = "Edited name";
	
    String userPatch;
	String userPatchUpdatedEmail = "updated@e.mail";
    String userPatchAdminRole;
    String userPatchNullName;
    String userPatchLongName;

    @Autowired
	@Getter
    private AbstractUserController<User,Long> controller;
	private String namePatch;

	@Value("classpath:/update-user/patch-update-user.json")
	public void setUserPatch(Resource patch) throws IOException {
		this.userPatch = ResourceUtils.toStr(patch);
	}

	@Value("classpath:/update-user/patch-name.json")
	public void setNamePatch(Resource patch) throws IOException {
		this.namePatch = ResourceUtils.toStr(patch);
	}
	
	@Value("classpath:/update-user/patch-admin-role.json")
	public void setUserPatchAdminRole(Resource patch) throws IOException {
		this.userPatchAdminRole = ResourceUtils.toStr(patch);;
	}

	@Value("classpath:/update-user/patch-null-name.json")
	public void setUserPatchNullName(Resource patch) throws IOException {
		this.userPatchNullName = ResourceUtils.toStr(patch);;
	}

	@Value("classpath:/update-user/patch-long-name.json")
	public void setUserPatchLongName(Resource patch) throws IOException {
		this.userPatchLongName = ResourceUtils.toStr(patch);;
	}

	/**
	 * A non-admin user should be able to update his own name,
	 * but changes in roles should be skipped.
	 * The name of security principal object should also
	 * change in the process.
	 * @throws Exception 
	 */
	@Test
    public void testUpdateWithUnknownPathVariables_should400() throws Exception {

			mvc.perform(update(userPatch,unverifiedUser.getId())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId())))
				.andExpect(status().is(400));
//				.andExpect(header().string(LecUtils.TOKEN_RESPONSE_HEADER_NAME, containsString(".")))
//				.andExpect(jsonPath("$.tag.name").value(UPDATED_NAME))
//				.andExpect(jsonPath("$.roles").value(hasSize(1)))
//				.andExpect(jsonPath("$.roles[0]").value(LemonRole.UNVERIFIED))
//				.andExpect(jsonPath("$.email").value(UNVERIFIED_USER_EMAIL));

		AbstractUser<Long> user = userRepository.findById(unverifiedUser.getId()).get();
		
		// Ensure that data has not changed
		Assertions.assertEquals(UNVERIFIED_USER_EMAIL, user.getEmail());
		Assertions.assertEquals(1, user.getRoles().size());
		Assertions.assertTrue(user.getRoles().contains(LemonRoles.UNVERIFIED));
    }

	/**
	 * A ADMIN should be able to update another user's name and roles.
	 * The name of security principal object should NOT change in the process,
	 * and the verification code should get set/unset on addition/deletion of
	 * the UNVERIFIED role. 
	 * @throws Exception 
	 */
	@Test
    public void testGoodAdminCanUpdateOther() throws Exception {

		
		mvc.perform(update(userPatch,unverifiedUser.getId())
//				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(admin.getId())))
//				.content(userPatch))
				.andExpect(status().is(200))
				.andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
//				.andExpect(jsonPath("$.id").value(unverifiedUser.getId()))
//				.andExpect(jsonPath("$.tag.name").value(UPDATED_NAME))
				.andExpect(jsonPath("$.roles").value(hasSize(1)))
				.andExpect(jsonPath("$.roles[0]").value(RapidRoles.ADMIN))
				.andExpect(jsonPath("$.email").value(userPatchUpdatedEmail));

		AbstractUser<Long> user = userRepository.findById(unverifiedUser.getId()).get();
    	
		// Ensure that data changed properly
		//should get replaced because admin has full power
		Assertions.assertEquals(userPatchUpdatedEmail, user.getEmail());
		Assertions.assertEquals(1, user.getRoles().size());
		Assertions.assertTrue(user.getRoles().contains(RapidRoles.ADMIN));
    }
	
	/**
	 * Providing an unknown id should return 404.
	 */
	@Test
    public void testUpdateUnknownId() throws Exception {
    	
		mvc.perform(update(userPatch,99L)
//				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(admin.getId())))
//				.content(userPatch))
				.andExpect(status().is(404));
    }



//	/**
//	 * Providing an unknown id should return 404.
//	 */
//	@Test
//	public void testUpdateOwnRolesAsNonAdmin_should403() throws Exception {
//
//		mvc.perform(update(userPatch,99L)
////				.contentType(MediaType.APPLICATION_JSON)
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(admin.getId())))
////				.content(userPatch))
//				.andExpect(status().is(404));
//	}
	
	/**
	 * A non-admin trying to update the name and roles of another user should throw exception
	 * @throws Exception 
	 */
	@Test
    public void testUpdateAnotherUser() throws Exception {
    	
		mvc.perform(update(namePatch,admin.getId())
//				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId())))
//				.content(userPatch))
				.andExpect(status().is(403));
    }

	/**
	 * A bad ADMIN trying to update the name and roles of another user should throw exception
	 * @throws Exception 
	 */
	@Test
    public void testBadAdminUpdateAnotherUser() throws Exception {
		
		mvc.perform(update(userPatch, unverifiedUser.getId())
//				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(secondAdmin.getId())))
//				.content(userPatch))
				.andExpect(status().is(403));

		mvc.perform(update( userPatch,unverifiedUser.getId())
//				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(blockedAdmin.getId())))
//				.content(userPatch))
				.andExpect(status().is(403));
	}

	/**
	 * A ADMIN should not be able to change his own roles
	 * @throws Exception 
	 */
	@Test
	@Disabled
	//why not?
    public void adminCanNotUpdateSelfRoles() throws Exception {
    	
		mvc.perform(update(userPatchAdminRole,admin.getId())
//				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(admin.getId())))
//				.content(userPatchAdminRole))
				.andExpect(status().is(200))
//				.andExpect(jsonPath("$.tag.name").value(UPDATED_NAME))
				.andExpect(jsonPath("$.roles").value(hasSize(1)))
				.andExpect(jsonPath("$.roles[0]").value(RapidRoles.ADMIN));
    }
	
	/**
	 * Invalid name
	 * @throws Exception
	 */
	@Test
    public void testUpdateUserInvalidNewName() throws Exception {

		// Null name
		assertThatThrownBy(() -> mvc.perform(update(userPatchNullName, unverifiedUser.getId())
//				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId())))
//				.content(userPatchNullName))
				.andExpect(status().is(400)))
				.hasRootCauseInstanceOf(SQLIntegrityConstraintViolationException.class);

		// Too long name
		assertThatThrownBy(() -> mvc.perform(update(userPatchLongName, unverifiedUser.getId())
//				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(unverifiedUser.getId())))
//				.content(userPatchLongName))
				.andExpect(status().is(400)))
				.hasRootCauseInstanceOf(MysqlDataTruncation.class);
    }
}
