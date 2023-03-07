package com.talenteo.hr.api;

import com.easyms.test.AbstractResourceTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talenteo.hr.dto.AddressDto;
import com.talenteo.hr.dto.AddressRequest;
import com.talenteo.hr.service.AddressService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "jean.dupont@titi.com", authorities = {"ROLE_ADMIN_CLIENT"})
public class AddressResourceTest extends AbstractResourceTest {

    @MockBean
    private AddressService addressService;

    private static final String API_ALL_ADDRESSES = "/api/v1/addresses";
    private static final String API_ADDRESS_ID = "/api/v1/addresses/{id}";



    /************ create new address **********/

    @Test
    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_CREATE_ADDRESS2"})
    public void should_return_forbidden_when_creating_address() throws Exception {
        doReturn(getAddressDto()).when(addressService).create(any(AddressRequest.class));
        mockMvc.perform(post(API_ALL_ADDRESSES)
                .content(new ObjectMapper().writeValueAsString(AddressRequest.builder().build()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
    @Test
    @WithMockUser(username = "jean.dupont@toto.com", authorities = {"PERM_CREATE_CONSULTANT","PERM_CREATE_MANAGER","PERM_CREATE_RECRUITER","PERM_CREATE_CANDIDATE","PERM_CREATE_COMPANY_ADMIN"})
    public void should_return_ok_when_creating_address() throws Exception {
        doReturn(getAddressDto()).when(addressService).create(any(AddressRequest.class));
        mockMvc.perform(post(API_ALL_ADDRESSES)
                .content(new ObjectMapper().writeValueAsString(AddressRequest.builder().build()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }


    /************ utils **********/

    private AddressRequest getAddressRequest() {
        return AddressRequest.builder()
                .address1("Cité Olympique")
                .address2("Cité El Khadra")
                .zipCode("3000")
                .city("Tunis")
                .country("Tunisie")
                .build();
    }

    private AddressDto getAddressDto() {
        return AddressDto.builder().
                address1("Cité Olympique")
                .address2("Cité El Khadra")
                .zipCode("3000")
                .city("Tunis")
                .country("Tunisie")
                .build();
    }

    private List<AddressDto> getListAddressDto() {
        return Arrays.asList(getAddressDto());
    }


}
