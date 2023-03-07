package com.talenteo.hr.api;

import com.easyms.test.AbstractResourceTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username="jean.dupont@titi.com",authorities={"ROLE_ADMIN_CLIENT5"})
public class CompanyEntityResourceTest extends AbstractResourceTest {

    private static final String API_ALL_COMPANY_ENTITIES = "/api/v1/company-entities";
    private static final String API_COMPANY_ENTITIES_ID = "/api/v1/company-entities/{id}";


    @Test
    @WithMockUser(username="jean.dupont@toto.com",authorities={"PERM_GET_COMPANY_ENTITY2"})
    public void should_return_forbidden_when_not_permitted() throws Exception {
        mockMvc.perform(get(API_COMPANY_ENTITIES_ID, "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username="jean.dupont@toto.com",authorities={"PERM_GET_COMPANY_ENTITY"})
    public void should_return_client_when_client_exists() throws Exception {
        mockMvc.perform(get(API_COMPANY_ENTITIES_ID, "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="jean.dupont@toto.com",authorities={"PERM_GET_COMPANY_ENTITY"})
    public void should_return_not_found_when_client_not_exists() throws Exception {
        mockMvc.perform(get(API_COMPANY_ENTITIES_ID, 200L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
