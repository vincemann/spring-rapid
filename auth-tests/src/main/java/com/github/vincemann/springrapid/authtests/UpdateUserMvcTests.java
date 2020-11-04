package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.AuthRoles;
import com.github.vincemann.springrapid.core.security.Roles;

import com.github.vincemann.springrapid.core.util.ResourceUtils;
import com.github.vincemann.springrapid.coretest.controller.rapid.UrlParamIdCrudControllerTest;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.sql.DataTruncation;
import java.sql.SQLIntegrityConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public abstract class UpdateUserMvcTests extends AbstractMvcTests
		implements UrlParamIdCrudControllerTest<AbstractUserController<?,Long,?>,Long> {

	private static final String FIELD_KEY_PLACEHOLDER = "name";
	private static final String FIELD_INVALID_VALUE_PLACEHOLDER = "invalidFieldValue";
	private static final String FIELD_NEW_VALID_VALUE_PLACEHOLDER = "newFieldValue";
	private static final String EMAIL_NEW_VALID_VALUE_PLACEHOLDER = "updated@e.mail";

    String patchEmailAndRole;
	String patchRole;
    String patchEmail;

	String patchField;
	String patchNullField;
    String patchLongField;


	@Autowired
	@Getter
	private AbstractUserController<? extends AbstractUser<Long>,Long,?> controller;
	
	protected String updatedEmail(){
		return "updated@e.mail";
	}

	protected String newValidFieldValue(){
		return "newName";
	}

	protected String invalidFieldValue(){
		return "A123456789A123456789A123456789A123456789A123456789A123456789A123456789";
	}

	/**
	 * Constraints on field:
	 * String
	 * Not blank
	 * Length 1 - 50
	 * Not null
	 * "newName" is valid new value
	 */
	protected abstract String getUpdatableUserField();

	/**
	 * A non-admin user should be able to update his own field,
	 * but changes in roles should be skipped.
	 * @throws Exception
	 */
	@Test
    public void testUserUpdatesOwnRoles_should400() throws Exception {

			mvc.perform(update(patchRole,getUser().getId())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUser().getId())))
				.andExpect(status().is(400));

		AbstractUser<Long> updated = getUserService().findById(getUser().getId()).get();

		// Ensure that data has not changed
		Assertions.assertEquals(USER_EMAIL, updated.getEmail());
		Assertions.assertEquals(1, updated.getRoles().size());
//		Assertions.assertTrue(user.getRoles().contains(LemonRoles.UNVERIFIED));
		Assertions.assertTrue(updated.getRoles().contains(AuthRoles.USER));
    }

	/**
	 * An ADMIN should be able to update another user's email and roles.
	 */
	@Test
    public void testAdminCanUpdateOther() throws Exception {
		mvc.perform(update(patchEmailAndRole,getUnverifiedUser().getId())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getAdmin().getId())))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$.roles").value(hasSize(1)))
				.andExpect(jsonPath("$.roles[0]").value(Roles.ADMIN))
				.andExpect(jsonPath("$.email").value(updatedEmail()));

		AbstractUser<Long> user = getUserService().findById(getUnverifiedUser().getId()).get();

		// Ensure that data changed properly
		//should get replaced because admin has full power
		Assertions.assertEquals(updatedEmail(), user.getEmail());
		Assertions.assertEquals(1, user.getRoles().size());
		Assertions.assertTrue(user.getRoles().contains(Roles.ADMIN));
    }

	/**
	 * Providing an unknown id should return 404.
	 */
	@Test
    public void testUpdateUnknownId_should403() throws Exception {
		mvc.perform(update(patchEmailAndRole,99L)
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getAdmin().getId())))
				.andExpect(status().is(403));
    }

	/**
	 * Invalid name
	 * @throws Exception
	 */
	@Test
	public void testUpdateUserInvalidFieldConstraints_should400() throws Exception {
		// Null name
		assertThatThrownBy(() -> mvc.perform(update(patchNullField, getUser().getId())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUser().getId())))
				.andExpect(status().is(400)))
				.hasRootCauseInstanceOf(SQLIntegrityConstraintViolationException.class);

		// Too long name
		assertThatThrownBy(() -> mvc.perform(update(patchLongField, getUser().getId())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUser().getId())))
				.andExpect(status().is(400)))
				.hasRootCauseInstanceOf(DataTruncation.class);
	}

	/**
	 * A non-admin trying to update the name and roles of another user should throw exception
	 * @throws Exception
	 */
	@Test
	public void testUserUpdatesAnotherUser_should403() throws Exception {
		mvc.perform(update(patchField,getUnverifiedUser().getId())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUser().getId())))
				.andExpect(status().is(403));
	}

	@Test
	public void testUserUpdatesAnotherAdmin_should403() throws Exception {
		mvc.perform(update(patchField,getAdmin().getId())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUser().getId())))
				.andExpect(status().is(403));
	}

	@Test
	public void testUserUpdatesOwnEmail_should400() throws Exception {
		mvc.perform(update(patchEmail,getUser().getId())
				.header(HttpHeaders.AUTHORIZATION, tokens.get(getUser().getId())))
				.andExpect(status().is(400));
	}


	@Value("classpath:/update-user/patch-email-and-role.json")
	public void setPatchEmailAndRole(Resource patch) throws IOException {
		this.patchEmailAndRole = ResourceUtils.toStr(patch)
				.replace(EMAIL_NEW_VALID_VALUE_PLACEHOLDER,updatedEmail());
	}

	@Value("classpath:/update-user/patch-role.json")
	public void setPatchRole(Resource patch) throws IOException {
		this.patchRole = ResourceUtils.toStr(patch);
	}

	@Value("classpath:/update-user/patch-email.json")
	public void setPatchEmail(Resource patch) throws IOException {
		this.patchEmail = ResourceUtils.toStr(patch)
				.replace(EMAIL_NEW_VALID_VALUE_PLACEHOLDER,updatedEmail());
	}



	@Value("classpath:/update-user/patch-field.json")
	public void setPatchField(Resource patch) throws IOException {
		this.patchField = ResourceUtils.toStr(patch)
				.replace(FIELD_KEY_PLACEHOLDER, getUpdatableUserField())
				.replace(FIELD_NEW_VALID_VALUE_PLACEHOLDER, newValidFieldValue());
	}

	@Value("classpath:/update-user/patch-null-field.json")
	public void setPatchNullField(Resource patch) throws IOException {
		this.patchNullField = ResourceUtils.toStr(patch)
				.replace(FIELD_KEY_PLACEHOLDER, getUpdatableUserField());
	}

	@Value("classpath:/update-user/patch-invalid-field.json")
	public void setPatchLongField(Resource patch) throws IOException {
		this.patchLongField = ResourceUtils.toStr(patch)
				.replace(FIELD_KEY_PLACEHOLDER, getUpdatableUserField())
				.replace(FIELD_INVALID_VALUE_PLACEHOLDER,invalidFieldValue());
	}

//	/**
//	 * Providing an unknown id should return 404.
//	 */
//	@Test
//	public void testUpdateOwnRolesAsNonAdmin_should403() throws Exception {
//
//		mvc.perform(update(userPatch,99L)
////				.contentType(MediaType.APPLICATION_JSON)
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(getAdmin().getId())))
////				.content(userPatch))
//				.andExpect(status().is(404));

//	}


//	/**
//	 * A bad ADMIN trying to update the name and roles of another user should throw exception
//	 * @throws Exception
//	 */
//	@Test
//    public void testBadAdminUpdateAnotherUser() throws Exception {
//
//		mvc.perform(update(userPatch, getUnverifiedUser().getId())
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(secondAdmin.getId())))
//				.andExpect(status().is(403));
//
//		mvc.perform(update( userPatch,getUnverifiedUser().getId())
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(blockedAdmin.getId())))
//				.andExpect(status().is(403));

//	}
//	/**
//	 * A ADMIN should not be able to change his own roles
//	 * @throws Exception
//	 */
//	@Test
//	@Disabled
//	//why not?
//    public void adminCanNotUpdateSelfRoles() throws Exception {
//
//		mvc.perform(update(userPatchAdminRole,getAdmin().getId())
////				.contentType(MediaType.APPLICATION_JSON)
//				.header(HttpHeaders.AUTHORIZATION, tokens.get(getAdmin().getId())))
////				.content(userPatchAdminRole))
//				.andExpect(status().is(200))
////				.andExpect(jsonPath("$.tag.name").value(UPDATED_NAME))
//				.andExpect(jsonPath("$.roles").value(hasSize(1)))
//				.andExpect(jsonPath("$.roles[0]").value(RapidRoles.ADMIN));

//    }
}
