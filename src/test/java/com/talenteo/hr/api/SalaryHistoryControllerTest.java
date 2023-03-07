package com.talenteo.hr.api;

import com.easyms.test.AbstractResourceTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talenteo.hr.dto.SalaryHistoryDto;
import com.talenteo.hr.dto.SalaryHistoryRequest;
import com.talenteo.hr.service.SalaryHistoryService;
import com.talenteo.hr.service.ValidationService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "jean.dupont@titi.com", authorities = {"ROLE_ADMIN_CLIENT5"})
public class SalaryHistoryControllerTest extends AbstractResourceTest {

    private static final String API_SALARIES_HISTORY = "/api/v1/salaries";
    private static final String API_GET_LATEST_SALARY_BY_HR = "/api/v1/salaries/latest";

    @MockBean
    private SalaryHistoryService salaryHistoryService;
    @MockBean
    private ValidationService validationService;

    @Test
    public void should_return_ok_when_get_salary_history_by_hr() throws Exception {

        doReturn(Arrays.asList(SalaryHistoryDto.builder().build()))
                .when(salaryHistoryService).getAllByHrId(anyLong());
        mockMvc.perform(get(API_SALARIES_HISTORY)
                .param("resourceId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    public void should_return_ok_when_get_latest_salary_by_unknown_hr() throws Exception {

        doReturn(getSalaryHistory()).when(salaryHistoryService).getlatestByHrId(anyLong());

        mockMvc.perform(get(API_GET_LATEST_SALARY_BY_HR)
                .param("resourceId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    //@WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_FULL_CREATE_BA"})
    public void should_throw_exception_when_create_invalid_salary_histo() throws Exception {
        doReturn(SalaryHistoryDto.builder().build()).when(salaryHistoryService).create(any(SalaryHistoryRequest.class));
        doThrow(new IllegalStateException()).when(validationService).validateCreateSalaryHistory(getSalHistoRequest());
        mockMvc.perform(post(API_SALARIES_HISTORY)
                .content(new ObjectMapper().writeValueAsString(getSalHistoRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }


    private Optional<SalaryHistoryDto> getSalaryHistory() {
        return Optional.of(SalaryHistoryDto.builder()
                .id(1L)
                .hrId(1L)
                .salaryAmount(2600)
                .startDate(new Date(2012 - 01 - 12))
                .endDate(new Date(2012 - 01 - 12))
                .currency("EUR")
                .build());
    }

    private static SalaryHistoryRequest getSalHistoRequest() {
        return SalaryHistoryRequest.builder()
                .id(1L)
                .hrId(4L)
                .salaryAmount(2600)
                .startDate(new Date(2012 - 01 - 12))
                .endDate(new Date(2012 - 01 - 12))
                .currency("EUR")
                .build();
    }


}
