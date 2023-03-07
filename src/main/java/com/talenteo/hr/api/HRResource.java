package com.talenteo.hr.api;

import com.talenteo.common.error.TalenteoCommonMessages;
import com.talenteo.hr.dto.*;
import com.talenteo.hr.model.entity.Company;
import com.talenteo.hr.service.HumanResourceService;
import com.talenteo.hr.service.OAuthRemote;
import com.talenteo.hr.service.ValidationService;
import feign.Headers;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Slf4j
@RestController
@RequestMapping("/api")
@Validated
@AllArgsConstructor
@Timed
public class HRResource {

    private final HumanResourceService humanResourceService;
    private final ValidationService validationService;
    private final OAuthRemote oAuthRemote;


    /************************************* Get Apis *********************************************************/


    @ApiOperation("Get human resource by id")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_HR')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/{id}")
    ResponseEntity<HumanResourceDto> getById(@PathVariable Long id) {
        log.info("get human resource by id = {}", id);
        Optional<HumanResourceDto> humanResourceDto = humanResourceService.getHrById(id);
        return humanResourceDto.map(hr -> ResponseEntity.ok().body(hr)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ApiOperation("Get all human resources by role")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_HR')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/by-role")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "Results page you want to retrieve (0..N)", required = true),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "Number of records per page.", required = true),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "Sorting criteria in the format: property(,asc|desc). " +
                            "Default sort order is ascending. " +
                            "Multiple sort criteria are supported.")
    })
    public ResponseEntity<PagedResources<Resource<HumanResourceDto>>> getAllResourcesByRole(@RequestParam(required = false) Role role,
                                                                                            Pageable pageable,
                                                                                            PagedResourcesAssembler<HumanResourceDto> assembler) {
        log.info("Get all human resources by role = {}", role);
        Page<HumanResourceDto> resources = humanResourceService.getAllHrsByRole(pageable, role);
        PagedResources<Resource<HumanResourceDto>> pagedResources = assembler.toResource(resources);
        return ResponseEntity.ok(pagedResources);
    }


    @ApiOperation("Get company admin by company")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_HR')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/company-admin/by-company")
    ResponseEntity<HumanResourceDto> getCompanyAdminByCompany(@RequestParam("company_id") Long companyId) {
        log.info("Get company admin by company = {}", companyId);
        Optional<HumanResourceDto> companyAdminByCompany = humanResourceService.getCompanyAdminByCompany(companyId);
        return companyAdminByCompany.map(hr -> ResponseEntity.ok().body(hr)).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @ApiOperation("confirm registration")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/confirm-registration")
    @PreAuthorize("hasAnyAuthority('PERM_REGISTER')")
    public ResponseEntity<HumanResourceDto> confirmRegistration(@RequestParam("token") String confirmationToken) {
        log.info("confirm registration with token = {}", confirmationToken);
        HumanResourceDto humanResourceDto = humanResourceService.confirmRegistration(confirmationToken);
        return ResponseEntity.ok().body(humanResourceDto);
    }

    @ApiOperation("Get light human resource by id")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_HR')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/light/{id}")
    ResponseEntity<HumanResourceLightDto> getLightHRById(@PathVariable Long id) {
        log.info("get light human resource by id = {}", id);
        Optional<HumanResourceLightDto> humanResourceDto = humanResourceService.getLightHrById(id);
        return humanResourceDto.map(hr -> ResponseEntity.ok().body(hr)).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @ApiOperation("Get human resource by email")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_HR')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/search-by-email")
    ResponseEntity<HumanResourceDto> getByEmail(@RequestParam @Email(message = TalenteoCommonMessages.INVALID_EMAIL) String email) {
        log.info("get human resource by email = {}", email);
        Optional<HumanResourceDto> humanResourceDto = humanResourceService.getByEmail(email);
        return humanResourceDto.map(hr -> ResponseEntity.ok().body(hr)).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @ApiOperation("Check if email exists")
    @Timed
    @PreAuthorize("hasAnyRole('ROLE_PLATFORM_UI')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/email-exists")
    ResponseEntity<Boolean> emailExists(@RequestParam @Email(message = TalenteoCommonMessages.INVALID_EMAIL) String email) {
        log.info("check if email = {} exists", email);
        HumanResourceDto humanResourceDto = humanResourceService.getByEmail(email).orElse(null);
        Boolean exists = Objects.nonNull(humanResourceDto);
        return ResponseEntity.ok().body(exists);

    }

    @ApiOperation("Check all recruiters and managers by company entity id ")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_HR')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/company-admin")
    ResponseEntity<List<HumanResourceDto>> getAllHumanResourcesByCompanyEntityId(@RequestParam(name = "company_entity_id") Company id) {
        log.info("company entity id", id);
        List<HumanResourceDto> humanResourceDtos = humanResourceService.getAllRecruitersAndManagersDataByCompanyEntityId(id);
        return ResponseEntity.ok().body(humanResourceDtos);

    }

    @ApiOperation("Get all human resources by company id")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_HR')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/by-company")
    ResponseEntity<List<HumanResourceDto>> getAllHumanResourcesByCompanyId(@RequestParam(name = "company_id") Long companyId) {
        log.info("get all human resources by company = {}", companyId);
        List<HumanResourceDto> humanResourceDtos = humanResourceService.getAllByCompanyId(companyId);
        return ResponseEntity.ok().body(humanResourceDtos);
    }


    @ApiOperation("get Human Resources by Supervisor Id")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_HR')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/by-supervisor-id")
    ResponseEntity<List<HumanResourceDto>> getHumanResourceBySupervisor(@RequestParam(name = "supervisor_id") Long id) {
        log.info("get Human Resources by Supervisor Id = {}", id);

        List<HumanResourceDto> humanResourceDtos = humanResourceService.getHumanResourcesBySupervisorId(id);
        return ResponseEntity.ok().body(humanResourceDtos);
    }

    @ApiOperation("get all Human Resources Data by Id")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_HR')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/{id}/details")
    ResponseEntity<Map<String, Object>> getAllHumanResourceDataById(@PathVariable() Long id) {
        log.info("get all Human Resources data by Id= {}", id);
        Map<String, Object> humanResourceDto = humanResourceService.getAllHumanResourceDataById(id);
        return ResponseEntity.ok().body(humanResourceDto);
    }

    @ApiOperation("get all Human Resources Data by Supervisor Id")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_HR')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/by-supervisor")
    ResponseEntity<Map<String, List>> getAllHumanResourcesDataBySupervisorId(@RequestParam(name = "supervisor_id") Long id) {
        log.info("get all Human Resources data by Supervisor Id= {}", id);
        Map<String, List> humanResourceDtos = humanResourceService.getAllHumanResourcesDataBySupervisorId(id);
        return ResponseEntity.ok().body(humanResourceDtos);
    }

    @ApiOperation("filter Human Resource by keyword")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_HR')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/by")
    ResponseEntity<List<HumanResourceDto>> filterHumanResourcesByKeyword(@RequestParam(name = "supervisor_id") Long id, @RequestParam String query) {
        log.info("filter Human Resource by keyword = {}", query);
        List<HumanResourceDto> humanResourceDtos = humanResourceService.filterHumanResourcesByKeyword(id, query);
        return ResponseEntity.ok().body(humanResourceDtos);
    }


    @ApiOperation("Get human resource by career id")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_GET_HR')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/by-career")
    ResponseEntity<HumanResourceLightDto> getHumanResourceByCareerId(@RequestParam(name = "career_id") Long careerId) {

        log.info("get human resource by career id {}", careerId);
        Optional<HumanResourceLightDto> lightHr = humanResourceService.getHumanResourceByCareerId(careerId);

        return lightHr.map(hr -> ResponseEntity.ok().body(hr)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /************************************* Post Apis *********************************************************/

    @ApiOperation("Create new manager")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_CREATE_MANAGER')")
    @PostMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/managers")
    ResponseEntity<HumanResourceDto> createManager(@RequestBody @Valid HumanResourceRequest humanResourceRequest, @RequestParam String language) {
        log.info("create new manager");
        validationService.validateCreateHumanResource(humanResourceRequest);
        HumanResourceDto humanResourceDto = humanResourceService.createManager(humanResourceRequest, language);
        final URI location = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/api/v1/human-resources/{id}").build().expand(humanResourceDto.getId()).toUri();
        return ResponseEntity.created(location).body(humanResourceDto);
    }

    @ApiOperation("Create new recruiter")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_CREATE_RECRUITER')")
    @PostMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/recruiters")
    ResponseEntity<HumanResourceDto> createRecruiter(@RequestBody @Valid HumanResourceRequest humanResourceRequest) {
        log.info("create new recruiter");
        validationService.validateCreateHumanResource(humanResourceRequest);
        HumanResourceDto humanResourceDto = humanResourceService.createRecruiter(humanResourceRequest);
        final URI location = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/api/v1/human-resources/{id}").build().expand(humanResourceDto.getId()).toUri();
        return ResponseEntity.created(location).body(humanResourceDto);
    }

    @ApiOperation("Create new consultant")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_CREATE_CONSULTANT')")
    @PostMapping(consumes = MimeTypeUtils.APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/consultants")
    ResponseEntity<HumanResourceDto> createConsultant(@RequestBody @Valid HumanResourceRequest humanResourceRequest, @RequestParam String language) {
        log.info("create new consultant");
        validationService.validateCreateHumanResource(humanResourceRequest);
        HumanResourceDto humanResourceDto = humanResourceService.createConsultant(humanResourceRequest, language);
        final URI location = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/api/v1/human-resources/{id}").build().expand(humanResourceDto.getId()).toUri();
        return ResponseEntity.created(location).body(humanResourceDto);
    }

    @ApiOperation("Create new company admin")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_CREATE_COMPANY_ADMIN')")
    @PostMapping(consumes = MimeTypeUtils.APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/company-admin")
    ResponseEntity<HumanResourceDto> createCompanyAdmin(@RequestBody @Valid HumanResourceRequest humanResourceRequest, @RequestParam String language) {
        log.info("create new company admin");
        validationService.validateCreateHumanResource(humanResourceRequest);
        HumanResourceDto humanResourceDto = humanResourceService.createCompanyAdmin(humanResourceRequest, language);
        final URI location = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/api/v1/human-resources/{id}").build().expand(humanResourceDto.getId()).toUri();
        return ResponseEntity.created(location).body(humanResourceDto);
    }

    @ApiOperation("Create new candidate")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_CREATE_CANDIDATE')")
    @PostMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/candidates")
    ResponseEntity<HumanResourceDto> createCandidate(@RequestBody @Valid HumanResourceRequest humanResourceRequest) {
        log.info("create new candidate");
        validationService.validateCreateHumanResource(humanResourceRequest);
        HumanResourceDto humanResourceDto = humanResourceService.createCandidate(humanResourceRequest);
        final URI location = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/api/v1/human-resources/{id}").build().expand(humanResourceDto.getId()).toUri();
        return ResponseEntity.created(location).body(humanResourceDto);
    }

    /************************************* Put Apis *********************************************************/

    @ApiOperation("Update human resource by id")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_UPDATE_SUPER_ADMIN','PERM_UPDATE_COMPANY_ADMIN','PERM_UPDATE_MANAGER','PERM_UPDATE_RECRUITER','PERM_UPDATE_CANDIDATE','PERM_UPDATE_CONSULTANT')")
    @PutMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/{id}")
    ResponseEntity<HumanResourceDto> updateHumanResource(@PathVariable("id") Long id, @RequestBody @Valid HumanResourceRequest humanResourceRequest) {
        log.info("update human resource by id {}");
        HumanResourceDto humanResourceDto = humanResourceService.update(id, humanResourceRequest);
        return ResponseEntity.ok().body(humanResourceDto);

    }

    @ApiOperation("Update already logged in")
    @PreAuthorize("hasAnyAuthority('PERM_UPDATE_COMPANY_ADMIN','PERM_UPDATE_MANAGER','PERM_UPDATE_RECRUITER','PERM_UPDATE_CANDIDATE','PERM_UPDATE_CONSULTANT')")
    @PutMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/update-logged-in")
    ResponseEntity<HumanResourceDto> updateAlreadyLoggedIn(@RequestParam Long id) {
        log.info("update already logged in");
        HumanResourceDto humanResourceDto = humanResourceService.setLoggedInTrue(id);
        return ResponseEntity.ok(humanResourceDto);

    }

    @ApiOperation("Update password by id user")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_UPDATE_PASSWORD')")
    @PutMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/users/update/{password}")
    ResponseEntity<OauthUserDto> update(@PathVariable String password) {
        log.info("update  password by id user {}");
        OauthUserDto currentUser = oAuthRemote.getOAuthUser();

        OauthUserDto oAuthUser = oAuthRemote.updateOAuthUser(currentUser.getId(), password);
        oAuthUser.setPassword(password);
        return ResponseEntity.ok().body(oAuthUser);

    }

    @ApiOperation("Update password by id user")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_UPDATE_PASSWORD')")
    @PutMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/users/update2/{password}")
    ResponseEntity<OauthUserDto> update2(@RequestParam String id, @PathVariable String password) {
        log.info("update  password by id user {}");
        OauthUserDto currentUser = oAuthRemote.getOAuthUser(id);
        OauthUserDto oAuthUser = oAuthRemote.updateOAuthUser2(currentUser.getId(), password);
        oAuthUser.setPassword(password);
        return ResponseEntity.ok().body(oAuthUser);

    }


    @ApiOperation("Update supervisor to Human Resource")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_UPDATE_MANAGER','PERM_UPDATE_RECRUITER','PERM_UPDATE_CANDIDATE','PERM_UPDATE_CONSULTANT')")
    @PutMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/{id}/supervisor/{id_supervisor}")
    void assignSupervisorToHr(@PathVariable Long id, @PathVariable("id_supervisor") Long supervisorId) {
        log.info("add supervisor {} to Human Resource {}", supervisorId, id);
        humanResourceService.assignSupervisorToHumanResource(id, supervisorId);
    }

    @ApiOperation("Update human resource by id")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_UPDATE_SUPER_ADMIN','PERM_UPDATE_COMPANY_ADMIN','PERM_UPDATE_MANAGER','PERM_UPDATE_RECRUITER','PERM_UPDATE_CANDIDATE','PERM_UPDATE_CONSULTANT')")
    @PutMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/{id}/avatar")
    ResponseEntity<HumanResourceDto> updateHumanResourceAvatar(@PathVariable("id") Long id, @RequestBody @Valid String avatarUrl) {
        HumanResourceDto humanResourceDto = humanResourceService.updateAvatar(id, avatarUrl);
        return ResponseEntity.ok().body(humanResourceDto);

    }

    /************************************* Delete Apis *********************************************************/

    @ApiOperation("delete human resource")
    @Timed
    @PreAuthorize("hasAnyAuthority('PERM_CREATE_COMPANY_ADMIN','PERM_CREATE_MANAGER','PERM_CREATE_RECRUITER','PERM_CREATE_CANDIDATE','PERM_CREATE_CONSULTANT')")
    @DeleteMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/{id}")
    ResponseEntity<Void> deleteHumanRessource(@PathVariable Long id) {
        log.info("delete human resource by id {}", id);
        humanResourceService.delete(id);
        return ResponseEntity.ok().build();
    }


    @ApiOperation("Get human resource by id")
    @Timed
    // @PreAuthorize("hasAnyAuthority('PERM_GET_HR')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/get-hierarchy/{id}")
    List<HumanResourceDto> getHierarchy(@PathVariable Long id) {
        log.info("get hierarchy by id = {}", id);
        List<HumanResourceDto> humanResourceDto = humanResourceService.getHierarchy(id);
        return humanResourceDto;
    }

    @ApiOperation("Get all managers by company id")
    @Timed
    //@PreAuthorize("hasAnyAuthority('PERM_GET_HR')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/managers/by-company")
    ResponseEntity<List<HumanResourceDto>> getAllManagersByCompanyId(@RequestParam(name = "company_id") Long companyId) {
        log.info("get all managers by company = {}", companyId);
        List<HumanResourceDto> humanResourceDtos = humanResourceService.getAllManagersByCompanyId(companyId);
        return ResponseEntity.ok().body(humanResourceDtos);
    }

    @ApiOperation("Get all consultants by company id")
    @Timed
    //@PreAuthorize("hasAnyAuthority('PERM_GET_HR')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/consultants/by-company")
    ResponseEntity<List<HumanResourceDto>> getAllConsultantsByCompanyId(@RequestParam(name = "company_id") Long companyId) {
        log.info("get all consultants by company = {}", companyId);
        List<HumanResourceDto> humanResourceDtos = humanResourceService.getAllConsultantByCompanyId(companyId);
        return ResponseEntity.ok().body(humanResourceDtos);
    }

    @ApiOperation("Get sas token")
    @Timed
    //@PreAuthorize("hasAnyAuthority('PERM_GET_HR')")
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/sas-token")
    Map<String, String> generateSasToken() {
        log.info("get sas token");
        Map<String, String> token = humanResourceService.callblobRestAPIWithSas();
        return token;
    }

    @ApiOperation("Upload an avatar")
    @Timed
    //@PostMapping(produces = APPLICATION_JSON_VALUE, path = "/v1/human-resources/avatar", consumes = "multipart/form-data")
    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = MULTIPART_FORM_DATA_VALUE, path = "/v1/human-resources/{id}/blob")
    public ResponseEntity uploadAvatar(@PathVariable("id") Long id, @RequestPart(name = "file") MultipartFile file) throws IOException {
        log.info("Upload avatar to hr {}", id);
        humanResourceService.uploadAvatar(id, file);
        return ResponseEntity.ok().build();
    }


}
