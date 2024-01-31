package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.core.sec.Roles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class UpdateUserTest extends RapidAuthIntegrationTest
{


	@Test
    public void userCantUpdateOwnRoles() throws Exception {
		String patchRoleJson = createUpdateJsonRequest(
				createUpdateJsonLine("replace", "/roles", Roles.ADMIN)
		);
		Set<String> oldRoles = new HashSet<>(getUser().getRoles());
		String token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
		mvc.perform(userController.update(patchRoleJson,getUser().getId())
						.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isForbidden());

		AbstractUser<Serializable> updated = getUserService().findById(getUser().getId()).get();

		// Ensure that data has not changed
		assertRolesHaveNotChanged(oldRoles,updated.getRoles());
    }

	/**
	 * An ADMIN should be able to update another user's contactInformation and roles.
	 */
	@Test
    public void adminCanUpdateDiffUser() throws Exception {
		String patchRoleAndContactInformationJson = createUpdateJsonRequest(
				createUpdateJsonLine("replace", "/contactInformation", NEW_CONTACT_INFORMATION),
				createUpdateJsonLine("replace", "/roles", Roles.ADMIN)
		);

		String token = login2xx(ADMIN_CONTACT_INFORMATION, ADMIN_PASSWORD);
		mvc.perform(userController.update(patchRoleAndContactInformationJson,getUser().getId())
						.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.roles").value(hasSize(1)))
				.andExpect(jsonPath("$.roles[0]").value(Roles.ADMIN))
				.andExpect(jsonPath("$.contactInformation").value(NEW_CONTACT_INFORMATION));

		AbstractUser<Serializable> user = getUserService().findById(getUser().getId()).get();

		// Ensure that data changed properly
		//should get replaced because admin has full power
		Assertions.assertEquals(NEW_CONTACT_INFORMATION, user.getContactInformation());
		Assertions.assertEquals(1, user.getRoles().size());
		Assertions.assertTrue(user.getRoles().contains(Roles.ADMIN));
    }
	@Test
	public void userCantUpdateOwnContactInformation() throws Exception {
		String patchContactInformationJson = createUpdateJsonRequest(
				createUpdateJsonLine("replace", "/contactInformation", NEW_CONTACT_INFORMATION)
		);
		String token = login2xx(USER_CONTACT_INFORMATION, USER_PASSWORD);
		mvc.perform(userController.update(patchContactInformationJson,getUser().getId())
						.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isForbidden());

		AbstractUser<Serializable> updated = getUserService().findById(getUser().getId()).get();

		// Ensure that data has not changed
		Assertions.assertEquals(USER_CONTACT_INFORMATION, updated.getContactInformation());
	}


	private void assertRolesHaveNotChanged(Set<String> old, Set<String> updated){
		Assertions.assertEquals(old.size(),updated.size());
		for (String role : old) {
			Assertions.assertTrue(updated.contains(role));
		}
	}


}
