package ng.upperlink.nibss.cmms.service;

import ng.upperlink.nibss.cmms.dto.UserRequest;
import ng.upperlink.nibss.cmms.embeddables.ContactDetails;
import ng.upperlink.nibss.cmms.embeddables.Name;
import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.auth.Role;
import ng.upperlink.nibss.cmms.model.contact.Country;
import ng.upperlink.nibss.cmms.model.contact.Lga;
import ng.upperlink.nibss.cmms.model.contact.State;
import ng.upperlink.nibss.cmms.repo.UserRepo;
import ng.upperlink.nibss.cmms.service.auth.RoleService;
import ng.upperlink.nibss.cmms.service.contact.CountryService;
import ng.upperlink.nibss.cmms.service.contact.LgaService;
import ng.upperlink.nibss.cmms.service.contact.StateService;
import ng.upperlink.nibss.cmms.util.email.SmtpMailSender;
import ng.upperlink.nibss.cmms.util.emandate.EmandateFormValidation;
import ng.upperlink.nibss.cmms.util.encryption.EncyptionUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserService {

    private static Logger LOG = LoggerFactory.getLogger(UserService.class);

    private UserRepo userRepo;

    private RoleService roleService;

    private LgaService lgaService;

    private StateService stateService;
    private CountryService countryService;
    private SmtpMailSender smtpMailSender;
    private EmandateFormValidation emandateFormValidation;
    @Value("${email_from}")
    private String fromEmail;

    @Value("${characters}")
    private String characters;

    @Value("${defaultPasswordLength}")
    private int defaultPasswordLength;

    @Value("${encryption.salt}")
    private String salt;

    @Autowired
    public void setEmandateFormValidation(EmandateFormValidation emandateFormValidation) {
        this.emandateFormValidation = emandateFormValidation;
    }

    @Autowired
    public void setStateService(StateService stateService) {
        this.stateService = stateService;
    }

    @Autowired
    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    public void setLgaService(LgaService lgaService) {
        this.lgaService = lgaService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setSmtpMailSender(SmtpMailSender smtpMailSender) {
        this.smtpMailSender = smtpMailSender;
    }

    public User get(Long id) {
        return userRepo.findOne(id);
    }

    public User getByEmail(String emailAddress) {
        return userRepo.findUserByEmailAddress(emailAddress);
    }

    public User getByEmailAndUserType(String emailAddress, UserType userType) {
        return userRepo.findUserByEmailAddressAndUserType(emailAddress, userType);
    }

    public User getByEmailAndUserType(String emailAddress, List<UserType> userType) {
        return userRepo.findUserByEmailAddressAndUserType(emailAddress, userType);
    }

    public User getByEmailAndNotId(String emailAddress, Long id) {
        return userRepo.findUserByEmailAddressAndNotId(emailAddress, id);
    }

    public long getCountOfUsersByEmailAddress(String emailAddress) {
        return userRepo.countByEmailAddress(emailAddress);
    }

    public long getCountOfUsersByEmailAddressNotId(String emailAddress, Long id) {
        return userRepo.countByEmailAddressAndNotId(emailAddress, id);
    }

    public long getCountOfUsersByPhone(String emailAddress) {
        return userRepo.countByPhoneNum(emailAddress);
    }

    public long getCountOfUsersByPhoneNotId(String emailAddress, Long id) {
        return userRepo.countByPhoneNumAndNotId(emailAddress, id);
    }

    public User save(User user) throws CMMSException {
        try {
            return userRepo.saveAndFlush(user);
        } catch (Exception e) {
            LOG.error("Unable save user details ...", e);
            throw new CMMSException("Unable save user details ...due to :" + e.getMessage(), "500", "500");
        }
    }

    public User toggle(Long id) throws CMMSException {

        User userToBeToggled = get(id);
        if (userToBeToggled == null)
        {
            throw  new CMMSException("User to be toggleInit is null","400","400");
        }

        boolean activated = userToBeToggled.isActivated();
        userToBeToggled.setActivated(!activated);
        userToBeToggled = save(userToBeToggled);

        return userToBeToggled;
    }

    public void validate(UserRequest request, boolean isUpdate, Long id) throws CMMSException {

        long userCountEmail = 0;
        long userCountPhone = 0;
        if (isUpdate) {
            if (id == null)

            {
                throw new CMMSException("User id was not provided!", "404", "404");
            }

            userCountEmail = getCountOfUsersByEmailAddressNotId(request.getEmailAddress(), id);
            userCountPhone = getCountOfUsersByPhoneNotId(request.getPhoneNumber(), id);
        } else {

            //validate the emailAddress
            if (request.getEmailAddress() == null || request.getEmailAddress().isEmpty()) {
                throw new CMMSException("emailAddress is invalid!", "404", "404");
            }
            userCountEmail = getCountOfUsersByEmailAddress(request.getEmailAddress());
            userCountPhone = getCountOfUsersByPhone(request.getPhoneNumber());
        }

        if (userCountEmail > 0) {
            throw new CMMSException("emailAddress address '" + request.getEmailAddress() + "' already exists!", "400", "400");
        }

        if (userCountPhone > 0) {
            throw new CMMSException("Phone Number '" + request.getPhoneNumber() + "' already exists!", "404", "404");
        }
        if(!emandateFormValidation.validPhoneNumber(request.getPhoneNumber()))
        {
            throw new CMMSException("Phone number must be between 11 and 15 digits","400","400");
        }
    }

    public User generate(User user, UserRequest request, boolean isUpdate, UserType userType) {

        String password = "";
        Lga lga = lgaService.get(request.getLgaId());
        user.setContactDetails(new ContactDetails(request.getHouseNumber(), request.getStreetName(), request.getCity(), new Lga(lga.getId(), lga.getName())));
        password = user.getPassword();
        if (!isUpdate) {
            password = generatePassword();
            user.setPassword(EncyptionUtil.doSHA512Encryption(password, salt));
            user.setChange_password(true);
        }
        user.setEmailAddress(request.getEmailAddress());
        user.setName(new Name(request.getName()));
        user.setPhoneNumber(request.getPhoneNumber());

        Collection<Role> roles = roleService.getActivated(request.getRoleId());
        user.getRoles().addAll(roles.stream().map(r -> new Role(r.getId(), r.getName(), r.getUserType())).collect(Collectors.toSet()));

        user.setUserType(userType);
        user.setActivated(false);


        //this authorizationStatus will be done only when it has been approved
//        save(user);
//        sendMail(user, code, password, isUpdate);

        return user;
    }

    public User generate(User user, User newUser) {

        user.setId(newUser.getId());
        user.setContactDetails(newUser.getContactDetails());
        user.setPassword(newUser.getPassword());
        user.setChange_password(newUser.isChange_password());
        user.setPasswordUpdatedAt(newUser.getPasswordUpdatedAt());
        user.setEmailAddress(newUser.getEmailAddress());
        user.setName(newUser.getName());
        user.setPhoneNumber(newUser.getPhoneNumber());
        user.getRoles().addAll(newUser.getRoles());
        user.setUserType(newUser.getUserType());
        user.setActivated(true);
        return user;
    }

    public UserRequest getUserRequest(UserRequest request) {
        return new UserRequest(request.getUserId(), request.getName(),
                request.getPhoneNumber(), request.getEmailAddress(), request.getHouseNumber(),
                request.getStreetName(), request.getRoleId(),
                request.getCity(), request.getLgaId());
    }

    public String generatePassword() {
        return RandomStringUtils.random(this.defaultPasswordLength, this.characters);
    }

    public void sendMail(User user, String code, String password, boolean isUpdate) {

        String details = generateDetails(user, code, password, isUpdate);
        String[] email = {user.getEmailAddress()};
        //Send the mail
        smtpMailSender.sendMail(fromEmail, email, "CMMS Login Credentials",
                "CMMS Login Credentials", "Kindly your login credentials below:", details);
    }

    public void sendRecoveryEmail(String email, String link) {
        String details = "<p><a href=\"" + link + "\">Click here to continue</a></p>";
        String[] senderEmail = {email};

        //Send the mail
        smtpMailSender.sendMail(fromEmail, senderEmail, "Password Recovery",
                "Password Recovery", "Kindly click on the link below to continue. Also note that this link will expire in 24 hours.", details);
    }

    public void passwordUpdateNotification(String email) {
        String[] senderEmail = {email};

        //Send the mail
        smtpMailSender.sendMail(fromEmail, senderEmail, "Request for Password Update",
                "Request for Password Update", "Please be informed that your password has expired. Kindly log into the portal to update your password.", null);
    }

    private String generateDetails(User user, String code, String password, boolean isUpdate) {

        String details = "";
        /* details += "<tr>\n" +
                " <td class=\"text-right\">Username :</td>\n" +
                " <td class=\"text-left\">"+user.getUsername()+"</td>\n" +
                " </tr>";

        details += "<tr>\n" +
                " <td class=\"text-right\">Password :</td>\n" +
                " <td class=\"text-left\">"+user.getPassword()+"</td>\n" +
                " </tr>";

        details += "<tr>\n" +
                " <td class=\"text-right\">User Type :</td>\n" +
                " <td class=\"text-left\">"+user.getEntityType().getValue()+"</td>\n" +
                " </tr>";

        if (code != null && !code.isEmpty()) {
            details += "<tr>\n" +
                    " <td class=\"text-right\">Code :</td>\n" +
                    " <td class=\"text-left\">"+code+"</td>\n" +
                    " </tr>";
        }*/

        details += "<strong>USERNAME :</strong> " + user.getEmailAddress() + "<br/>";
        if (!isUpdate) {
            details += "<strong>PASSWORD :</strong> " + password + "<br/>";
            details += "<strong>ROLE :</strong> " + password + "<br/>";
        }
        details += "<strong>User Type :</strong> " + user.getUserType().getValue() + "<br/>";

        if (code != null && !code.isEmpty()) {
            details += "<strong>Code :</strong> " + code + "<br/>";
        }
//
        return details;
    }

    public List<User> getAllUsersForPasswordUpdate() {
        try {
            return userRepo.getUsersForPasswordUpdate();
        } catch (Exception e) {
            LOG.error("Unable to fetch users for password update", e);
        }
        return new ArrayList<>();
    }

    public long countByType(UserType userType) {
        synchronized (new Object()) {
            return userRepo.countByType(userType);
        }
    }

    public List<String> getAllEmailAddress() {
        return userRepo.getAllEmailAddress();
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepo.getAllUsers(pageable);
    }

    public List<User> getAllUsers() {
        return userRepo.getAllUsers();
    }


    public List<User> getUsersByUserType(UserType userType, boolean activated, RoleName roleName) {
        return userRepo.getUsersByUserType(roleName, userType, activated);
    }

    public List<Long> setStatusCount(Map<UserType,Map<Boolean,List<User>>> mapObj, UserType key) {
        Long active =0L;
        Long inactive = 0L;
        Map<Boolean,List<User>> mapCount = mapObj.get(key);
        if(mapCount.containsKey(true)){
            active = new Long(mapCount.get(true).size());
        }

        if(mapCount.containsKey(false)){
            inactive = new Long(mapCount.get(false).size());
        }
        return Arrays.asList(active,inactive);

    }

    public List<Date> computeTimeSpan() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        String startDateString = String.format("%d-%d-%d",calendar.get(Calendar.YEAR),calendar.get(Calendar.JANUARY),1);

        Date startDate = dateFormat.parse(startDateString);
        Date dt = new Date();
        LocalDateTime ldt = LocalDateTime.ofInstant(dt.toInstant(), ZoneId.systemDefault());
        Date endDate = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

        return Arrays.asList(startDate,endDate);
    }
    public ContactDetails getContactDetails(UserRequest userReq) {
        ContactDetails contact = new ContactDetails();
        Lga lga = null;
        State state =null;
        Country country =null;
        if (userReq.getLgaId() !=null && userReq.getLgaId() !=0)
        {
            lga= this.lgaService.get(userReq.getLgaId());
            contact.setLga(Optional.ofNullable(lga).orElse(null));
        }
        if (userReq.getStateId() !=null && userReq.getStateId() !=0)
        {
            state= this.stateService.get(userReq.getStateId());
            contact.setState(Optional.ofNullable(state).orElse(null));
        }
        if (userReq.getCountryId() !=null && userReq.getCountryId() !=0)
        {
            country= this.countryService.get(userReq.getCountryId());
            contact.setCountry(Optional.ofNullable(country).orElse(null));
        }
        contact.setHouseNumber(Optional.ofNullable(userReq.getHouseNumber()).orElse(null));
        contact.setStreetName(Optional.ofNullable(userReq.getStreetName()).orElse(null));
        contact.setCity(Optional.ofNullable(userReq.getCity()).orElse(null));
        return contact;
    }
    public User setUser(User operator)
    {
        return new User(operator.getId(),operator.getName(),operator.getEmailAddress(),operator.getUserType());
    }

    public void updateLoggedToken(String token,Long id){
        userRepo.updateToken(token,id);
    }
}