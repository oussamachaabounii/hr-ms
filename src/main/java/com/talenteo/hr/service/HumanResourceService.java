package com.talenteo.hr.service;

import com.easyms.mail.domain.Mail;
import com.talenteo.career.dto.AssessmentCampaignDto;
import com.talenteo.career.dto.BiAnnualAssessmentDto;
import com.talenteo.career.dto.CareerDto;
import com.talenteo.common.security.SecurityUtils;
import com.talenteo.common.utils.AzureBlobStorageUtils;
import com.talenteo.hr.client.CareerMsClient;
import com.talenteo.hr.client.MissionMsClient;
import com.talenteo.hr.client.TdMsClient;
import com.talenteo.hr.config.HrProperties;
import com.talenteo.hr.converter.AddressDtoConverter;
import com.talenteo.hr.converter.HumanResourceConverter;
import com.talenteo.hr.converter.HumanResourceLightConverter;
import com.talenteo.hr.converter.HumanResourceRequestConverter;
import com.talenteo.hr.dto.*;
import com.talenteo.hr.model.entity.*;
import com.talenteo.hr.repository.HumanResourceRepository;
import com.talenteo.hr.repository.NationalityRepository;
import com.talenteo.hr.repository.VerificationTokenRepository;
import com.talenteo.notification.dto.Type;
import com.talenteo.td.dto.TechnicalDocumentDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class HumanResourceService {

    private final HumanResourceRepository humanResourceRepository;
    private final OAuthRemote oAuthRemote;
    private final VerificationTokenRepository verificationTokenRepository;
    private final NotificationRemote notificationRemote;
    private final SearchRemote searchRemote;
    private final NationalityRepository nationalityRepository;

    private final AzureBlobStorageUtils azureBlobStorageUtils;

    private final HumanResourceRequestConverter humanResourceRequestConverter;
    private final PasswordEncoder passwordEncoder;
    private final HrProperties hrProperties;
    private final CareerMsClient careerMsClient;
    private final TdMsClient tdMsClient;
    private final MissionMsClient missionMsClient;

    public Optional<HumanResourceDto> getByEmail(String email) {
        Optional<HumanResource> hr = humanResourceRepository.findByEmail(email);
        return hr.map(cl -> HumanResourceConverter.newInstance().convert(cl));
    }

    public Optional<HumanResourceDto> getHrById(Long id) {
        Optional<HumanResource> hr = humanResourceRepository.findById(id);
        return hr.map(cl -> HumanResourceConverter.newInstance().convert(cl));
    }

    public Optional<HumanResourceLightDto> getLightHrById(Long id) {
        Optional<HumanResource> hr = humanResourceRepository.findById(id);
        return hr.map(cl -> HumanResourceLightConverter.newInstance().convert(cl));
    }

    public Optional<HumanResourceDto> getCompanyAdminByCompany(Long companyId) {
        Optional<HumanResource> companyAdminByCompany = humanResourceRepository.getCompanyAdminByCompany(companyId);
        return companyAdminByCompany.map(cl -> HumanResourceConverter.newInstance().convert(cl));
    }

    public HumanResourceDto createManager(HumanResourceRequest humanResourceRequest, String language) {
        HumanResource hr = humanResourceRequestConverter.convert(humanResourceRequest);
        hr.setRole(Role.Manager);
        hr.setActive(true);
        HumanResource humanResource = humanResourceRepository.save(hr);

        /** send email **/
        String cryptedPassword = sendMailtoCreatedHr(humanResource, language);

        /** create oauth secure user **/
        createOauthUser(humanResource, cryptedPassword);

        return HumanResourceConverter.newInstance().convert(humanResource);
    }

    public HumanResourceDto createRecruiter(HumanResourceRequest humanResourceRequest) {
        HumanResource hr = humanResourceRequestConverter.convert(humanResourceRequest);
        hr.setRole(Role.Recruiter);
        hr.setActive(true);
        HumanResource humanResource = humanResourceRepository.save(hr);

        return HumanResourceConverter.newInstance().convert(humanResource);
    }

    public HumanResourceDto confirmRegistration(String confirmationToken) {
        HumanResource humanResource = oAuthRemote.confirmRegistration(confirmationToken);
        return HumanResourceConverter.newInstance().convert(humanResource);
    }


    public String getCurrentUserEmail() {
        return SecurityUtils.getCurrentUserId();
    }

    /**
     * Create a new consultant
     *
     * @param humanResourceRequest
     * @return
     */
    public HumanResourceDto createConsultant(HumanResourceRequest humanResourceRequest, String language) {
        /** save consultant **/
        HumanResource hr = humanResourceRequestConverter.convert(humanResourceRequest);
        hr.setActive(true);
        HumanResource humanResource = humanResourceRepository.save(hr);
        /** Elastic Search **/
        indexElasticSearch(humanResource);
        /** send email **/
        String cryptedPassword = sendMailtoCreatedHr(humanResource, language);

        /** create oauth secure user **/
        createOauthUser(humanResource, cryptedPassword);

        return HumanResourceConverter.newInstance().convert(humanResource);
    }

    public HumanResourceDto createCompanyAdmin(HumanResourceRequest humanResourceRequest, String language) {
        /** save consultant **/
        HumanResource hr = humanResourceRequestConverter.convert(humanResourceRequest);
        hr.setActive(true);
        HumanResource humanResource = humanResourceRepository.save(hr);
        /** Elastic Search **/

        /** send email **/
        String cryptedPassword = sendMailtoCreatedHr(humanResource, language);

        /** create oauth secure user **/
        createCompanyAdminUser(humanResource, cryptedPassword);

        return HumanResourceConverter.newInstance().convert(humanResource);
    }


    /**
     * @param humanResource
     * @param cryptedPassword
     */
    private void createOauthUser(HumanResource humanResource, String cryptedPassword) {
        List<String> role = new ArrayList<>();
        if (humanResource.getRole().toString().equals("Manager")) {
            role.add("BUSINESS_MANAGER");
        } else {
            role.add(humanResource.getRole().toString().toUpperCase());
        }
        OauthUserDto oAuthUser = OauthUserDto.builder()
                .id(humanResource.getId().toString())
                .login(humanResource.getEmail())
                .password(cryptedPassword)
                .emailValidation(false)
                .enabled(true)
                .roles(role)
                .build();
        oAuthRemote.createOAuthUser(oAuthUser);
    }

    private void createCompanyAdminUser(HumanResource humanResource, String crypted_password) {
        OauthUserDto oAuthUser = OauthUserDto.builder()
                .id(humanResource.getId().toString())
                .login(humanResource.getEmail())
                .password(crypted_password)
                .emailValidation(false)
                .enabled(true)
                .roles(Arrays.asList("COMPANY_ADMIN"))
                .build();
        oAuthRemote.createOAuthUser(oAuthUser);
    }

    /**
     * Send an email to the created Hr
     *
     * @param humanResource
     * @return cryptedPassword
     */
    private String sendMailtoCreatedHr(HumanResource humanResource, String language) {
        String password = this.generateRandomPassword();
        String cryptedPassword = passwordEncoder.encode(password);

        Map variables = new HashMap();

        VerificationToken verificationToken = new VerificationToken(humanResource);
        VerificationToken token = verificationTokenRepository.save(verificationToken);

        String url = hrProperties.getFrontUrl() + hrProperties.getRouteMailConfirm() + token.getConfirmationToken();
        variables.put("url", url);
        variables.put("password", password);

        Mail mail = new Mail();
        mail.setTo(humanResource.getEmail());
        if (language.equals("fr")) {
            mail.setTemplate(Type.ENREGISTREMENT_ACTIVATION.name());
        } else {
            mail.setTemplate(Type.REGISTRATION_ACTIVATION.name());
        }
        mail.setVariables(variables);
        try {
            notificationRemote.send(mail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cryptedPassword;
    }

    /**
     * Index created hr in elastic Search database
     *
     * @param humanResource
     */
    private void indexElasticSearch(HumanResource humanResource) {
        try {
            searchRemote.indexHumanResource(humanResource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public HumanResourceDto createCandidate(HumanResourceRequest humanResourceRequest) {
        HumanResource hr = humanResourceRequestConverter.convert(humanResourceRequest);
        hr.setActive(true);
        hr.setRole(Role.Candidate);
        HumanResource humanResource = humanResourceRepository.save(hr);

        return HumanResourceConverter.newInstance().convert(humanResource);
    }

    public HumanResourceDto update(Long id, HumanResourceRequest humanResourceRequest) {

        List<Nationality> nationalityList = Objects.nonNull(humanResourceRequest.getNationalities()) ? humanResourceRequest.getNationalities().stream()
                .map(nationality -> nationalityRepository.findByCountry(nationality)).collect(Collectors.toList()) : Arrays.asList();

        log.info("update hr service");
        HumanResource hr = humanResourceRepository.findById(id).orElse(null);
        hr.setEmail(humanResourceRequest.getEmail());
        hr.setFirstname(humanResourceRequest.getFirstname());
        hr.setLastname(humanResourceRequest.getLastname());
        hr.setRole(humanResourceRequest.getRole());
        hr.setAddress(AddressDtoConverter.newInstance().convert(humanResourceRequest.getAddress()));
        hr.setAvatar(humanResourceRequest.getAvatar());
        hr.setGender(humanResourceRequest.getGender());
        hr.setBirthDate(humanResourceRequest.getBirthDate());
        hr.setPhoneNumber(humanResourceRequest.getPhoneNumber());
        hr.setNationalities(nationalityList);
        hr.setActive(true);

        // hr.setCompanyEntity(CompanyEntityDtoConverter.newInstance().convert(humanResourceRequest.getCompanyEntity()));
        //hr.setSupervisor(HumanResourceDtoConverter.newInstance().convert(humanResourceRequest.getSupervisor()));
        HumanResource humanResource = humanResourceRepository.save(hr);

        return HumanResourceConverter.newInstance().convert(hr);

    }

    public void assignSupervisorToHumanResource(Long id, Long supervisorId) {
        Optional<HumanResource> hr = humanResourceRepository.findById(id);
        Optional<HumanResource> supervisor = humanResourceRepository.findById(supervisorId);

        hr.get().setSupervisor(supervisor.get());
        humanResourceRepository.save(hr.get());

    }

    public void delete(Long id) {

        Optional<HumanResource> hr = humanResourceRepository.findById(id);
        hr.get().setActive(false);
        humanResourceRepository.save(hr.get());
        oAuthRemote.deleteOAuthUser(id);
    }


    public List<HumanResourceDto> filterHumanResourcesByKeyword(Long id, String query) {
        List<HumanResource> hr = humanResourceRepository.findByCriteria(id, query);
        return hr.stream().map(humanResource -> HumanResourceConverter.newInstance().convert(humanResource)).collect(Collectors.toList());
    }

    public List<HumanResourceDto> getHumanResourcesBySupervisorId(Long id) {
        HumanResource supervisor = humanResourceRepository.findById(id).orElse(null);
        List<HumanResource> hrs = humanResourceRepository.findBySupervisor(supervisor);
        return hrs.stream().map(humanResource -> HumanResourceConverter.newInstance().convert(humanResource)).collect(Collectors.toList());
    }

    public Map<String, List> getAllHumanResourcesDataBySupervisorId(Long id) {
        List<HumanResource> hrs = humanResourceRepository.findBySupervisorId(id);
        List<HumanResource> consultants = hrs.stream().filter(hr -> Role.Consultant.equals(hr.getRole())).collect(Collectors.toList());
        List<HumanResource> managers = hrs.stream().filter(hr -> Role.Manager.equals(hr.getRole())).collect(Collectors.toList());
        List<HumanResource> candidates = hrs.stream().filter(hr -> Role.Candidate.equals(hr.getRole())).collect(Collectors.toList());
        List<HumanResource> recruiters = hrs.stream().filter(hr -> Role.Recruiter.equals(hr.getRole())).collect(Collectors.toList());
        Map<String, List> allHrs = new HashMap<String, List>();
        List<Map<String, Object>> consultantsResult = new ArrayList<>();

        consultantsResult = consultants.stream().map(consultant ->
                getConsultantsData(HumanResourceConverter.newInstance().convert(consultant))).collect(Collectors.toList());

        List<Map<String, Object>> managersResult = new ArrayList<>();

        managersResult = managers.stream().map(manager ->
                getConsultantsData(HumanResourceConverter.newInstance().convert(manager))).collect(Collectors.toList());
        List<Map<String, Object>> recruitersResult = new ArrayList<>();

        recruitersResult = recruiters.stream().map(recruiter ->
                getConsultantsData(HumanResourceConverter.newInstance().convert(recruiter))).collect(Collectors.toList());
        allHrs.put("consultants", consultantsResult);
        allHrs.put("managers", managersResult);
        allHrs.put("candidates", candidates);
        allHrs.put("recruiters", recruitersResult);
        return allHrs;
    }


    public List<HumanResourceDto> getAllRecruitersAndManagersDataByCompanyEntityId(Company id) {
        List<HumanResource> humanResourceDtos = humanResourceRepository.findByCompanyEntityId(id);
        return humanResourceDtos.stream().map(humanResource -> HumanResourceConverter.newInstance().convert(humanResource)).collect(Collectors.toList());

    }


    private Map<String, Object> getConsultantsData(HumanResourceDto consultant) {
        Map<String, Object> consultantsData = new HashMap<String, Object>();
        Map<String, Object> careerData = new HashMap<String, Object>();
        List<BiAnnualAssessmentDto> biAnnualAssessmentsList = new ArrayList<>();
        List<AssessmentCampaignDto> assessmentCampaignsList = new ArrayList<>();
        CareerDto career = CareerDto.builder().build();
        Map<String, Object> missionsStats = new HashMap<>();
        TechnicalDocumentDto td = null;
        consultantsData.put("hrData", consultant);
        //consultantsData.put("supervisorData",consultant.getSupervisor());
        try {
            td = tdMsClient.getByHrId(consultant.getId()).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consultantsData.put("tdData", td);
        }
        try {
            missionsStats = missionMsClient.getMissionsStatsByConsultant(consultant.getId()).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consultantsData.put("missionData", missionsStats);
        }
        try {
            career = careerMsClient.getCareerByResourceId(consultant.getId()).getBody();
            assessmentCampaignsList = careerMsClient.getAllAssessmentCampaigns(consultant.getCompanyEntity().getCompany().getId()).getBody();
            biAnnualAssessmentsList = careerMsClient.findBiannualAssessmentByhrId(consultant.getId()).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            careerData.put("career", career);
            careerData.put("assessments", biAnnualAssessmentsList);
            careerData.put("campaigns", assessmentCampaignsList);
            consultantsData.put("careerData", careerData);
        }
        return consultantsData;
    }


    private boolean isValidHr(Long id, HumanResource supervisor, HumanResource hr) {
        //boolean valid1 = Role.Consultant.equals(hr.getRole());
        boolean valid2 = id.equals(Optional.ofNullable(hr.getSupervisor()).map(HumanResource::getId).orElse(null));
        CompanyEntity companyEntity = supervisor.getCompanyEntity();
        boolean valid3 = Visibility.Entity.equals(hr.getVisibility()) && Optional.ofNullable(companyEntity).map(companyEntity1 -> companyEntity1.getId().equals(hr.getCompanyEntity().getId())).orElse(false);
        boolean valid4 = Visibility.Group.equals(hr.getVisibility()) && Optional.ofNullable(companyEntity).filter(companyEntity1 -> Objects.nonNull(companyEntity1.getCompany())).map(company -> company.getCompany().getId().equals(hr.getCompanyEntity().getCompany().getId())).orElse(false);
        return valid2 || valid3 || valid4;
        //return valid1 && (valid2 || valid3 || valid4);
    }

    public HumanResourceDto setLoggedInTrue(Long id) {
        Optional<HumanResource> hr = humanResourceRepository.findById(id);
        hr.get().setAlreadyLoggedIn(true);
        HumanResource humanResource = humanResourceRepository.save(hr.get());
        return HumanResourceConverter.newInstance().convert(humanResource);

    }

    public List<HumanResourceDto> getAllByCompanyId(Long companyId) {
        List<HumanResource> humanResourceDtos = humanResourceRepository.findByCompanyId(companyId);
        return humanResourceDtos.stream().map(humanResource -> HumanResourceConverter.newInstance().convert(humanResource)).collect(Collectors.toList());
    }

    public List<HumanResourceLightDto> getAllByCompanyLabel(String label) {
        List<HumanResource> humanResourceDtos = humanResourceRepository.findByCompanyLabel(label);
        return humanResourceDtos.stream().map(humanResource -> HumanResourceLightConverter.newInstance().convert(humanResource)).collect(Collectors.toList());
    }

    public Page<HumanResourceDto> getAllHrsByRole(Pageable pageable, Role role) {
        Page<HumanResource> resources = humanResourceRepository.findByRole(role, pageable);
        return resources.map(HumanResourceConverter.newInstance()::convert);
    }

    /**
     * get human resource by career id
     *
     * @param careerId
     * @return
     */
    public Optional<HumanResourceLightDto> getHumanResourceByCareerId(Long careerId) {
        CareerDto career = careerMsClient.getCareer(careerId).getBody();
        return getLightHrById(career.getHr());
    }


    public String generateRandomPassword() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }

    public HumanResourceDto updateAvatar(Long id, String avatarUrl) {
        HumanResource hr = humanResourceRepository.findById(id).orElse(null);

        hr.setAvatar(avatarUrl);
        HumanResource savedHumanResource = humanResourceRepository.save(hr);
        if (savedHumanResource.getRole().toString().equals("Consultant")) {
            indexElasticSearch(savedHumanResource);
        }
        return HumanResourceConverter.newInstance().convert(savedHumanResource);


    }

    public Map<String, Object> getAllHumanResourceDataById(Long id) {
        HumanResource humanResource = humanResourceRepository.findById(id).orElse(null);

        Map<String, Object> consultantsData = getConsultantsData(HumanResourceConverter.newInstance().convert(humanResource));
        return consultantsData;
    }


    public List<HumanResourceDto> getHierarchy(long humanResource) {
        List<HumanResource> humanResource1 = new ArrayList<>();
        Map<Long, List> allHrs = new HashMap<Long, List>();
        List<HumanResource> parcours = new ArrayList<>();
        List<HumanResource> stockage = new ArrayList<>();
        Boolean test = true;
        List<HumanResource> humanResources = humanResourceRepository.findBySupervisorId(humanResource);
        parcours = humanResources;
        humanResource1 = humanResources;

        while (test == true) {
            for (int i = 0; i < parcours.size(); i++) {
                humanResource1.addAll(humanResourceRepository.findBySupervisorId(parcours.get(i).getId()));

                if (humanResourceRepository.findBySupervisorId(parcours.get(i).getId()).size() > 0) {
                    stockage.addAll(humanResourceRepository.findBySupervisorId(parcours.get(i).getId()));
                }
            }
            if (stockage.size() > 0) {
                parcours = stockage;
                stockage.clear();
            } else {
                test = false;
            }
        }
        return humanResource1.stream().map(hr -> HumanResourceConverter.newInstance().convert(hr)).collect(Collectors.toList());
    }

    public List<HumanResourceDto> getAllManagersByCompanyId(Long companyId) {
        List<HumanResource> humanResourceDtos = humanResourceRepository.getManagerByCompany(companyId);
        return humanResourceDtos.stream().map(humanResource -> HumanResourceConverter.newInstance().convert(humanResource)).collect(Collectors.toList());
    }

    public List<HumanResourceDto> getAllConsultantByCompanyId(Long companyId) {
        List<HumanResource> humanResourceDtos = humanResourceRepository.getConsultantByCompany(companyId);
        return humanResourceDtos.stream().map(humanResource -> HumanResourceConverter.newInstance().convert(humanResource)).collect(Collectors.toList());
    }

    public static String GetSASToken(String resourceUri, String keyName, String key) {
        long epoch = System.currentTimeMillis() / 1000L;
        int week = 60 * 60 * 24 * 7;
        String expiry = Long.toString(epoch + week);

        String sasToken = null;
        try {
            String stringToSign = URLEncoder.encode(resourceUri, StandardCharsets.UTF_8) + "\n" + expiry;
            String signature = getHMAC256(key, stringToSign);
            sasToken = "SharedAccessSignature sr=" + URLEncoder.encode(resourceUri, StandardCharsets.UTF_8) + "&sig=" +
                    URLEncoder.encode(signature, StandardCharsets.UTF_8) + "&se=" + expiry + "&skn=" + keyName;
        } catch (Exception e) {

            e.printStackTrace();
        }

        return sasToken;
    }


    public static String getHMAC256(String key, String input) {
        Mac sha256_HMAC = null;
        String hash = null;
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            Base64.Encoder encoder = Base64.getEncoder();

            hash = new String(encoder.encode(sha256_HMAC.doFinal(input.getBytes("UTF-8"))));

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hash;
    }

    public Map<String, String> callblobRestAPIWithSas() {

        //SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        //fmt.setTimeZone(TimeZone.getTimeZone("UTC"));

        ZonedDateTime startDate = ZonedDateTime.now().minusDays(1);
        ZonedDateTime expiryDate = ZonedDateTime.now().plusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");
        String start = formatter.format(startDate.withZoneSameLocal(ZoneId.of("UTC")));
        String expiry = formatter.format(expiryDate.withZoneSameLocal(ZoneId.of("UTC")));


//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date());
//        cal.add(Calendar.DATE, -2);
//        String start = fmt.format(cal);
//        cal.add(Calendar.DATE, 4);
//        String expiry = fmt.format(cal.getTime());
        String storageAccountName = "captivaostalenteo";
        String storageAccountKey = "edkc4XZAWi6bOTnCGfpovoijdqKMG6gSCSYP7IuXRCN7wCryP3LU+EpjhdZ72Xs9hEZDL/jD78EbP4SDN3LeGg==";
        String apiVersion = "2019-12-12";
        String resource = "sco";
        String permissions = "rwdlacx";
        String service = "b";

        String stringToSign = storageAccountName + "\n" +
                permissions + "\n" +
                service + "\n" +
                resource + "\n" +
                start + "\n" +
                expiry + "\n" +
                "\n" +
                "https\n" +
                apiVersion + "\n";

        String signature = getHMAC256(storageAccountKey, stringToSign);

        try {

            String sasToken = "comp=list&restype=container" +
                    "&sv=" + apiVersion +
                    "&ss=" + service +
                    "&srt=" + resource +
                    "&sp=" + permissions +
                    "&se=" + expiry + //URLEncoder.encode(expiry, "UTF-8") +
                    "&st=" + start + //URLEncoder.encode(start, "UTF-8") +
                    "&spr=https" +
                    "&sig=" + URLEncoder.encode(signature, "UTF-8");

            Map<String, String> result = new HashMap<>();
            result.put("token", sasToken);
            return result;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void uploadAvatar(Long humanResource, MultipartFile file) throws IOException {
        Optional<HumanResource> optHr = humanResourceRepository.findById(humanResource);
        HumanResource hr = optHr.get();
        ZonedDateTime now = ZonedDateTime.now();
        String filename = "avatar-" + hr.getId() + "-" + now.getNano() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        String blobPath = "avatars";
        hr.setAvatar(hrProperties.getEndpoint() + "/" + blobPath+ "/" + filename);
        azureBlobStorageUtils.createOrReplaceFile(blobPath, filename, file.getInputStream(), file.getSize());
        humanResourceRepository.save(hr);
    }
}