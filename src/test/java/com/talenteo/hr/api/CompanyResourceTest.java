package com.talenteo.hr.api;

import com.easyms.test.AbstractResourceTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username="jean.dupont@titi.com",authorities={"ROLE_ADMIN_CLIENT5"})
public class CompanyResourceTest extends AbstractResourceTest {

    private static final String API_ALL_COMPANIES = "/api/v1/companies";
    private static final String API_COMPANY_ID = "/api/v1/companies/{id}";

    /************ get all companies **********/

    @Test
    @WithMockUser(username="jean.dupont@toto.com",authorities={"PERM_GET_COMPANY2"})
    public void should_return_forbidden_when_getting_all_companies_and_not_permitted() throws Exception {
        mockMvc.perform(get(API_ALL_COMPANIES)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username="jean.dupont@toto.com",authorities={"PERM_GET_COMPANY"})
    public void should_return_companies_when_getting_all_companies_and_company_exists() throws Exception {
        mockMvc.perform(get(API_ALL_COMPANIES)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="jean.dupont@toto.com",authorities={"PERM_GET_COMPANY"})
    public void should_return_empty_when_getting_all_companies_and_no_company_exists() throws Exception {
        mockMvc.perform(get(API_ALL_COMPANIES)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /************ get company by id **********/

    @Test
    @WithMockUser(username="jean.dupont@toto.com",authorities={"PERM_GET_COMPANY2"})
    public void should_return_forbidden_when_not_permitted() throws Exception {
        mockMvc.perform(get(API_COMPANY_ID, "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username="jean.dupont@toto.com",authorities={"PERM_GET_COMPANY"})
    public void should_return_company_when_company_exists() throws Exception {
        mockMvc.perform(get(API_COMPANY_ID, "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="jean.dupont@toto.com",authorities={"PERM_GET_COMPANY"})
    public void should_return_not_found_when_company_does_not_exist() throws Exception {
        mockMvc.perform(get(API_COMPANY_ID, "50")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
