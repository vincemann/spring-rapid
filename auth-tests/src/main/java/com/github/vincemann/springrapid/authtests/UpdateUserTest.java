package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.AuthRoles;
import com.github.vincemann.springrapid.core.security.Roles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class UpdateUserTest extends AbstractRapidAuthIntegrationTest
{


	@Test
    public void userCantUpdateOwnRoles() throws Exception {
		String patchRoleJson = createUpdateJsonRequest(
				createUpdateJsonLine("replace", "/roles", Roles.ADMIN)
		);
		String token = login2xx(USER_EMAIL, USER_PASSWORD);
		mvc.perform(update(patchRoleJson,getUser().getId())
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isForbidden());

		AbstractUser<Long> updated = getUserService().findById(getUser().getId()).get();

		// Ensure that data has not changed
		Assertions.assertEquals(1, updated.getRoles().size());
		Assertions.assertTrue(updated.getRoles().contains(AuthRoles.USER));
    }

	/**
	 * An ADMIN should be able to update another user's email and roles.
	 */
	@Test
    public void adminCanUpdateDiffUser() throws Exception {
		String patchRoleAndEmailJson = createUpdateJsonRequest(
				createUpdateJsonLine("replace", "/email", NEW_EMAIL),
				createUpdateJsonLine("replace", "/roles", Roles.ADMIN)
		);

		String token = login2xx(ADMIN_EMAIL, ADMIN_PASSWORD);
		mvc.perform(update(patchRoleAndEmailJson,getUser().getId())
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.roles").value(hasSize(1)))
				.andExpect(jsonPath("$.roles[0]").value(Roles.ADMIN))
				.andExpect(jsonPath("$.email").value(NEW_EMAIL));

		AbstractUser<Long> user = getUserService().findById(getUser().getId()).get();

		// Ensure that data changed properly
		//should get replaced because admin has full power
		Assertions.assertEquals(NEW_EMAIL, user.getEmail());
		Assertions.assertEquals(1, user.getRoles().size());
		Assertions.assertTrue(user.getRoles().contains(Roles.ADMIN));
    }


	@Test
    public void cantUpdateUnknownUser() throws Exception {
		String patchFieldJson = createUpdateJsonRequest(
				createUpdateJsonLine("replace", "/"+testAdapter.getUpdatableFieldName(), testAdapter.getNewValidFieldValue())
		);
		String token = login2xx(USER_EMAIL, USER_PASSWORD);
		mvc.perform(update(patchFieldJson,UNKNOWN_USER_ID)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isForbidden());
    }

    @Test
    public void userCanUpdateOwnField() throws Exception {
		Assumptions.assumeFalse(testAdapter.getUpdatableFieldName() ==null);
		String patchFieldJson = createUpdateJsonRequest(
				createUpdateJsonLine("replace", "/"+testAdapter.getUpdatableFieldName(), testAdapter.getNewValidFieldValue())
		);
		String token = login2xx(USER_EMAIL, USER_PASSWORD);
		mvc.perform(update(patchFieldJson,getUser().getId())
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful());
	}

	/**
	 * Invalid name
	 * @throws Exception
	 */
	@Test
	public void cantUpdateUserWithInvalidData() throws Exception {
		Assumptions.assumeFalse(testAdapter.getUpdatableFieldName() ==null);
		String invalidFieldPatchJson = createUpdateJsonRequest(
				createUpdateJsonLine("replace", "/"+testAdapter.getUpdatableFieldName(), testAdapter.getInvalidFieldValue())
		);
		String token = login2xx(USER_EMAIL, USER_PASSWORD);
		mvc.perform(update(invalidFieldPatchJson,getUser().getId())
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isBadRequest());
	}

	/**
	 * A non-admin trying to update the name and roles of another user should throw exception
	 * @throws Exception
	 */
	@Test
	public void userCantUpdateDiffUser() throws Exception {
		Assumptions.assumeFalse(testAdapter.getUpdatableFieldName() ==null);
		String invalidFieldPatchJson = createUpdateJsonRequest(
				createUpdateJsonLine("replace", "/"+testAdapter.getUpdatableFieldName(), testAdapter.getNewValidFieldValue())
		);
		String token = login2xx(USER_EMAIL, USER_PASSWORD);
		mvc.perform(update(invalidFieldPatchJson,getSecondUser().getId())
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isForbidden());
	}

	@Test
	public void userCantUpdateAdmin() throws Exception {
		Assumptions.assumeFalse(testAdapter.getUpdatableFieldName() ==null);
		String invalidFieldPatchJson = createUpdateJsonRequest(
				createUpdateJsonLine("replace", "/"+testAdapter.getUpdatableFieldName(), testAdapter.getNewValidFieldValue())
		);
		String token = login2xx(USER_EMAIL, USER_PASSWORD);
		mvc.perform(update(invalidFieldPatchJson,getAdmin().getId())
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isForbidden());
	}

	@Test
	public void userCantUpdateOwnEmail() throws Exception {
		String patchEmailJson = createUpdateJsonRequest(
				createUpdateJsonLine("replace", "/email", NEW_EMAIL)
		);
		String token = login2xx(USER_EMAIL, USER_PASSWORD);
		mvc.perform(update(patchEmailJson,getUser().getId())
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isForbidden());

		AbstractUser<Long> updated = getUserService().findById(getUser().getId()).get();

		// Ensure that data has not changed
		Assertions.assertEquals(USER_EMAIL, updated.getEmail());
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
