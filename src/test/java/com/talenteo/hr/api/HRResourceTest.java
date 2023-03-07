package com.talenteo.hr.api;

import com.easyms.test.AbstractResourceTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talenteo.hr.converter.CompanyEntityConverter;
import com.talenteo.hr.dto.*;
import com.talenteo.hr.model.entity.CompanyEntity;
import com.talenteo.hr.repository.CompanyEntityRepository;
import com.talenteo.hr.service.HumanResourceService;
import com.talenteo.hr.service.NotificationRemote;
import com.talenteo.hr.service.OAuthRemote;
import com.talenteo.hr.service.SearchRemote;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "jean.dupont@titi.com", authorities = {"ROLE_ADMIN_CLIENT5"})
public class HRResourceTest extends AbstractResourceTest {


    private static final String API_BASE_HRS = "/api/v1/human-resources";
    private static final String API_BASE_POST_CONSULTANT = "/api/v1/human-resources/consultants";
    private static final String API_BASE_POST_COMPANY_ADMIN = "/api/v1/human-resources/company-admin";
    private static final String API_HR_ID = "/api/v1/human-resources/{id}";
    private static final String API_HR_EMAIL = "/api/v1/human-resources/search-by-email";
    private static final String API_COMPANY_HRS = "/api/v1/human-resources/by-company";
    private static final String API_SUPERVISOR_HRS = "/api/v1/human-resources/by-supervisor-id";
    private static final String API_HRS_KEYWORD = "/api/v1/human-resources/by";

    @Autowired
    private HumanResourceService humanResourceService;

    @Autowired
    CompanyEntityRepository companyEntityRepository;

    @MockBean
    private SearchRemote searchRemote;
    @MockBean
    private NotificationRemote notificationRemote;
    @MockBean
    private OAuthRemote oAuthRemote;

    /**
     * find human resource by id with invalid permission
     */
    @Test
    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_GET_HR2"})
    public void should_return_forbidden_when_getting_hr_by_id_and_not_permitted() throws Exception {
        mockMvc.perform(get(API_HR_ID, "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    /**
     * find human resource by id with valid permission and id exists
     */
    @Test
    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_GET_HR"})
    public void should_return_hr_when_getting_hr_by_id_and_hr_exists() throws Exception {
        mockMvc.perform(get(API_HR_ID, "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * find human resource by id with valid permission and id does not exist
     */
    @Test
    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_GET_HR"})
    public void should_return_notfound_when_getting_hr_by_id_and_hr_does_not_exist() throws Exception {
        mockMvc.perform(get(API_HR_ID, "200")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * find human resource by email with invalid permission
     */
    @Test
    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_GET_HR2"})
    public void should_return_forbidden_when_getting_hr_by_email_and_not_permitted() throws Exception {
        mockMvc.perform(get(API_HR_EMAIL)
                .param("email", "mondher@gmail.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    /**
     * find human resource by email with valid permission and email exists
     */
    @Test
    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_GET_HR"})
    public void should_return_hr_when_getting_hr_by_email_and_hr_exists() throws Exception {
        mockMvc.perform(get(API_HR_EMAIL)
                .param("email", "admin@yopmail.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * find human resource by email with valid permission and email does not exist
     */
    @Test
    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_GET_HR"})
    public void should_return_notfound_when_getting_hr_by_email_and_hr_does_not_exist() throws Exception {
        mockMvc.perform(get(API_HR_EMAIL)
                .param("email", "1234@gmail.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * find human resources by supervisor with invalid permission
     */
    @Test
    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_GET_CONSULTANT2"})
    public void should_return_forbidden_when_getting_all_hrs_by_supervisor_and_not_permitted() throws Exception {
        mockMvc.perform(get(API_SUPERVISOR_HRS)
                .param("supervisor_id", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    /**
     * find human resources by supervisor with valid permission and list is not empty
     */
    @Test
    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_GET_HR"})
    public void should_return_hrs_when_getting_all_hrs_by_supervisor_and_not_empty() throws Exception {
        mockMvc.perform(get(API_SUPERVISOR_HRS)
                .param("supervisor_id", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
                .andDo(print());

    }

    /**
     * find human resources by supervisor with valid permission and list is empty
     */
    @Test
    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_GET_HR"})
    public void should_return_empty_when_getting_all_hrs_by_supervisor_and_no_hr_exists() throws Exception {
        mockMvc.perform(get(API_SUPERVISOR_HRS)
                .param("supervisor_id", "3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty())
                .andDo(print());
    }


    /**
     * find human resources by keyword with invalid permission
     */
    @Test
    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_GET_MANAGER"})
    public void should_return_forbidden_when_getting_all_hrs_by_keyword_and_not_permitted() throws Exception {
        mockMvc.perform(get(API_HRS_KEYWORD)
                .param("supervisor_id", "2")
                .param("query", "john"))
                .andExpect(status().isForbidden());
    }

    /**
     * find human resources by keyword with valid permission and list is not empty
     */
    @Test
    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_GET_HR"})
    public void should_return_hrs_when_getting_all_hrs_by_keyword_and_not_empty() throws Exception {
        mockMvc.perform(get(API_HRS_KEYWORD)
                .param("supervisor_id", "2")
                .param("query", "gmail"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
                .andDo(print());
    }

    /**
     * find human resources by keyword with valid permission and list is empty
     */
    @Test
    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_GET_HR"})
    public void should_return_empty_when_getting_all_hrs_by_keyword_and_no_hr_exists() throws Exception {
        mockMvc.perform(get(API_HRS_KEYWORD)
                .param("supervisor_id", "1")
                .param("query", "haha"))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty())
                .andExpect(status().isOk())
                .andDo(print());
    }


    /**
     * create new consultant with invalid permission
     *
     * @throws Exception
     */

    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_CREATE_CONSULTANT1"})
    @Test
    public void should_return_forbidden_when_create_consultant_and_not_permitted() throws Exception {

        HumanResourceRequest consultant = getHumanResource();
        consultant.setRole(Role.Consultant);
        String HrJsonString = new ObjectMapper().writeValueAsString(consultant);

        mockMvc.perform(post(API_BASE_POST_CONSULTANT)
                .content(HrJsonString)
                .param("language","en")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }


    /**
     * create new consultant with valid permission
     *
     * @throws Exception
     */

    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_CREATE_CONSULTANT"})
    @Test
    public void should_return_ok_when_create_consultant() throws Exception {

        HumanResourceRequest consultant = getHumanResource();
        consultant.setRole(Role.Consultant);
        String HrJsonString = new ObjectMapper().writeValueAsString(consultant);
        mockMvc.perform(post(API_BASE_POST_CONSULTANT)
                .content(HrJsonString)
                .param("language", "en")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nationalities").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").isNotEmpty())
                .andDo(print());
    }

    /**
     * create new company admin with invalid permission
     *
     * @throws Exception
     */

    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_CREATE_COMPANY_ADMIN1"})
    @Test
    public void should_return_forbidden_when_create_company_admin_and_not_permitted() throws Exception {

        HumanResourceRequest companyAdmin = getHumanResource();
        companyAdmin.setRole(Role.Company_Admin);
        String HrJsonString = new ObjectMapper().writeValueAsString(companyAdmin);
        mockMvc.perform(post(API_BASE_POST_COMPANY_ADMIN)
                .content(HrJsonString)
                .param("language","en")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }


    /**
     * create new company admin with valid permission
     *
     * @throws Exception
     */

    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_CREATE_COMPANY_ADMIN"})
    @Test
    public void should_return_ok_when_create_company_admin() throws Exception {

        HumanResourceRequest companyAdmin = getHumanResource();
        companyAdmin.setRole(Role.Company_Admin);
        companyAdmin.setEmail("company-admin@yopmail.com");
        String HrJsonString = new ObjectMapper().writeValueAsString(companyAdmin);
        mockMvc.perform(post(API_BASE_POST_COMPANY_ADMIN)
                .content(HrJsonString)
                .param("language", "en")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nationalities").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").isNotEmpty())
                .andDo(print());
    }

    /**
     * update an existing Human resource with invalid permission
     *
     * @throws Exception
     */

    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_UPDATE_CONSULTANT1"})
    @Test
    public void should_return_forbidden_when_update_consultant_and_not_permitted() throws Exception {

        HumanResourceRequest hrRequest = getHumanResource();
        hrRequest.setRole(Role.Consultant);
        hrRequest.setEmail("test@gmail.com");

        String HrJsonString = new ObjectMapper().writeValueAsString(hrRequest);

        mockMvc.perform(MockMvcRequestBuilders
                .put(API_BASE_HRS + "/{id}", 1L)
                .content(HrJsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    /**
     * update an existing  Human resource
     *
     * @throws Exception
     */
    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_UPDATE_CONSULTANT"})
    @Test
    public void should_return_ok_when_update_consultant() throws Exception {

        HumanResourceRequest hrRequest = getHumanResource();
        hrRequest.setRole(Role.Consultant);
        hrRequest.setEmail("test@gmail.com");

        String HrJsonString = new ObjectMapper().writeValueAsString(hrRequest);

        mockMvc.perform(MockMvcRequestBuilders
                .put(API_BASE_HRS + "/{id}", 3L)
                .content(HrJsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstname").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@gmail.com"));
    }


    /**
     * Mock objects
     */

    public HumanResourceRequest getHumanResource() throws ParseException {
        Optional<CompanyEntity> entity = companyEntityRepository.findById(1L);
        CompanyEntityDto companyEntityDto = CompanyEntityConverter.newInstance().convert(entity.get());

        AddressDto addressDto = AddressDto.builder()
                .address1("123 Main Street Room 22")
                .address2("University Dorm")
                .city("New York")
                .zipCode("11377")
                .country("USA")
                .build();

        HumanResourceRequest hrRequest = HumanResourceRequest.builder()
                .firstname("John")
                .lastname("Smith")
                .gender(Gender.Male)
                .email("johnsmith@gmail.com")
                .companyEntity(companyEntityDto)
                .address(addressDto)
                .alreadyLoggedIn(true)
                .avatar("logo.png")
                .birthDate(new Date())
                .nationalities(Arrays.asList("fr", "tn"))
                .supervisor(null)
                .build();

        return hrRequest;
    }
}
