package ng.upperlink.nibss.cmms.util;

import ng.upperlink.nibss.cmms.dto.biller.BillerUserRequest;
import ng.upperlink.nibss.cmms.embeddables.ContactDetails;
import ng.upperlink.nibss.cmms.embeddables.Name;
import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.auth.Role;
import ng.upperlink.nibss.cmms.model.bank.BankUser;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.biller.BillerUser;
import ng.upperlink.nibss.cmms.model.contact.Country;
import ng.upperlink.nibss.cmms.model.contact.Lga;
import ng.upperlink.nibss.cmms.model.contact.State;
import ng.upperlink.nibss.cmms.repo.biller.BillerRepo;
import ng.upperlink.nibss.cmms.repo.biller.BillerUserRepo;
import ng.upperlink.nibss.cmms.service.NotificationConfig;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.auth.RoleService;
import ng.upperlink.nibss.cmms.service.bank.BankUserService;
import ng.upperlink.nibss.cmms.service.biller.BillerService;
import ng.upperlink.nibss.cmms.service.contact.LgaService;
import ng.upperlink.nibss.cmms.util.email.SmtpMailSender;
import ng.upperlink.nibss.cmms.util.encryption.EncyptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class BillerUserServiceTest  {
//
//    private static Logger LOG = LoggerFactory.getLogger(BankUserService.class);
//
//    private NotificationConfig notificationConfig;
//    private BillerService billerService;
//    private RoleService roleService;
//    private BillerUserRepo billerUserRepo;
//    private BillerRepo billerRepo;
//    private LgaService lgaService;
//    private BankUserService bankUserService;
//    private UserService userService;
//    private Biller biller = new Biller();
//    private UserType entityType = UserType.BANK;
//    private SmtpMailSender smtpMailSender;
//    @Value("${encryption.salt}")
//    private String salt;
//
//    @Value("${email_from}")
//    private String fromEmail;
//
//    @Autowired
//    public void setBillerService(BillerService billerService)
//    {
//        this.billerService = billerService;
//    }
//    @Autowired
//    public void setRoleService(RoleService roleService)
//    {
//        this.roleService = roleService;
//    }
//
//    @Autowired
//    public void setBankUserService(BankUserService bankUserService)
//    {
//        this.bankUserService = bankUserService;
//    }
//
//    @Autowired
//    public void setSmtpMailSender(SmtpMailSender smtpMailSender)
//    {
//        this.smtpMailSender = smtpMailSender;
//    }
//
//    @Autowired
//    public void setBillerUserRepo(BillerUserRepo billerUserRepo)
//    {
//        this.billerUserRepo = billerUserRepo;
//    }
//
//    @Autowired
//    public void setUserService(UserService userService)
//    {
//        this.userService = userService;
//    }
//
//    @Autowired
//    public void setLgaService(LgaService lgaService)
//    {
//        this.lgaService = lgaService;
//    }
//
//    public Page<BillerUser> getAll(Pageable pageable)
//    {
//        return billerUserRepo.findAll(pageable);
//    }
//
//    public Page<BillerUser> getAllByBillerId(Long billerId, Pageable pageable)
//    {
//        return billerUserRepo.getAllByBillerId(billerId, pageable);
//    }
//
//
//    public Page<BillerUser> getAllByActiveStatusAndBillerId(boolean activated, Long billerId, Pageable pageable)
//    {
//        return billerUserRepo.getAllByActiveStatusAndBillerId(activated, billerId, pageable);
//    }
//
//    public Page<BillerUser> getAllByActiveStatus(boolean activated, Pageable pageable)
//    {
//        return billerUserRepo.getAllByActiveStatus(activated, pageable);
//    }
//
//    public BillerUser getById(Long id){
//
//        return billerUserRepo.getById(id);
//    }
//
//    public List<String> getAllActiveAuthorizerEmailAddress(Long billerId)
//    {
//        return billerUserRepo.getAllActiveAuthorizerEmailAddress(billerId);
//    }
//
//    public long getCountOfSameEmailAddress(String emailAddress, Long id)
//    {
//        if (id == null) {
//            return userService.getCountOfUsersByEmailAddress(emailAddress);
//        }
//        return userService.getCountOfUsersByEmailAddressNotId(emailAddress, id);
//    }
//
//    public BillerUser save(BillerUser billerUser,String password, boolean isUpdate)
//    {
//        billerUser =billerUserRepo.save(billerUser);
//
//        //send a mail to all the authorizers for awareness
//        sendAwarenessMail(billerUser,password,isUpdate,fromEmail);
//        return billerUser;
//    }
//
//    public BillerUser getUserById(Long userId)
//    {
//        return billerUserRepo.getById(userId);
//    }
//
//    public String validate(BillerUserRequest request, boolean isUpdate, Long id)
//    {
//
//        BillerUser billerUser = null;
//        if (isUpdate) {
//            if (id == null) {
//                return "BillerUser id is not provided";
//            }
//        }
//        return userService.validate(userService.getUserRequest(request), isUpdate, request.getUserId());
//    }
//
//    public BillerUser generate(BillerUser billerUser, BillerUserRequest billerUserRequest, User operator, boolean isUpdate, UserType entityType)
//    {
//
//        if(billerUserRequest.getLgaId() != null && billerUserRequest.getLgaId() !=0){
//            Lga lga = this.lgaService.get(billerUserRequest.getLgaId());
//            billerUser.setContactDetails(
//                    new ContactDetails(billerUserRequest.getHouseNumber(),
//                            billerUserRequest.getStreetName(), billerUserRequest.getCity(),
//                            new Lga(lga.getId(), lga.getName()),
//                            new State(lga.getState().getId(), lga.getState().getName()),
//                            new Country(lga.getState().getCountry().getId(), lga.getState().getCountry().getName())));
//
//        }
//        billerUser.setEmailAddress(billerUserRequest.getEmailAddress());
//        billerUser.setName(new Name(billerUserRequest.getName()));
//        billerUser.setPhoneNumber(billerUserRequest.getPhoneNumber());
//
////        String userAuthType = roles.stream().map(Role::getUserAuthorisationType).collect(Collectors.toList()).get(0);
//
//        //added modifications
////        billerUser.setMakerCheckerType(MakerCheckerType.find(userAuthType));
//
//        Collection<Role> roles = this.roleService.getActivated(billerUserRequest.getRoleId());
//        billerUser.getRoles().addAll(roles);
////        billerUser.getRoles().addAll((Collection) roles.stream().map((r) -> {
////            return new Role(r.getId(), r.getName(), r.getEntityType());
////        }).collect(Collectors.toSet()));
//
//        if (isUpdate) {
//            if (operator != null)
//                billerUser.setUpdatedAt(new Date());
//            billerUser.setUpdatedBy(new User(operator.getId(), operator.getName(), operator.getEmailAddress(), operator.isActivated(), operator.getEntityType()));
//
//        } else {
//
//            billerUser.setChange_password(true);
//            billerUser.setEntityType(entityType);
//            billerUser.setStaffNumber(billerUserRequest.getStaffNumber());
//            billerUser.setActivated(true);
//            billerUser.setBiller(billerUserRequest.getBiller());
//            billerUser.setCreatedAt(new Date());
//
//            if (operator != null) {
//                billerUser.setCreatedBy(new User(operator.getId(), operator.getName(), operator.getEmailAddress(), operator.isActivated(), operator.getEntityType()));
//            }
//        }
//        return billerUser;
//    }
//    public BillerUser toggleBiller (Long id, User user)
//    {
//
//        BillerUser billerUser = getById(id);
//        if (billerUser == null) {
//            return new BillerUser();
//        }
//
//        boolean activated = billerUser.isActivated();
//        billerUser.setActivated(!activated);
//        billerUser.setUpdatedBy(user);
//        billerUser = save(billerUser,null,true);
//
//        return billerUser;
//    }
//
//    public void sendAwarenessMail(BillerUser billerUser,String password,boolean isUpdated,String fromEmail)
//    {
//
//
//        if (billerUser.getCreatedBy() == null){
//            LOG.error("BANK User 'created by' is NULL");
//            return;
//        }
//
//        if (isUpdated && billerUser.getUpdatedBy() == null){
//            LOG.error("BANK User 'updated by' is NULL");
//            return;
//        }
//        String emailAddress = billerUser.getEmailAddress();
//
//        String[] emailAddress = {emailAddress};
//
//        if(emailAddress.length > 0){
//            String subject = "Account creation";
//            String  title="Login credential";
//            String message = "Your account  has been created.<br/><i>Click the link below to change your password<i>";
//            if (isUpdated){
//                message = "Changes on your account has been updated";
//                title = "Password reset";
//                subject = "Account Update";
//            }
//
//            //Send the mail
//            smtpMailSender.sendMail(fromEmail, emailAddress, subject,
//                    title, message, generateDetails(billerUser, isUpdated,password));
//        }
//
//    }
//
//    private String generateDetails(BillerUser billerUser, boolean isUpdate,String password)
//    {
//
//        String details = "";
//        if (isUpdate) {
//            details += "<strong>Your password reset successful : </strong> " + "<br/>";
//            details += "<strong>Date : </strong>  " + billerUser.getUpdatedAt() + "<br/>";
//        }else {
//            details += "<strong>Username :</strong> " + billerUser.getEmailAddress()+"<br/>";
//            details += "<strong>Password :</strong> " + password + "<br/>";
////            details += "<a href="+loginURL+"> <strong>Click here To Login and change password</strong></a><br/>";
//        }
//        return details;
//    }
//
//    public ResponseEntity<?> setup(BillerUserRequest billerUserRequest, User operatorUser, boolean isUpdate,Role operatorRole)
//    {
//        String errorResult = validate(billerUserRequest, isUpdate, billerUserRequest.getUserId());
//        if (errorResult != null)
//        {
//            return ResponseEntity.badRequest().body(new ErrorDetails(errorResult));
//        }
//
//        BillerUser billerUser =null;
//
//        if (isUpdate)
//        {
//            billerUser = getById(billerUserRequest.getUserId());
//            if (billerUser==null)
//            {
//                return ResponseEntity.badRequest().body(new ErrorDetails(ng.upperlink.nibss.cmms.enums.Errors.DATA_IS_NULL.getValue().replace("{}", "The user to be updated is null").replace("{name}", billerUserRequest.getBiller().getName())));
//            }
//            // delete old roles and persist new
//            Set<Role> oldRoles = billerUser.getRoles();
//            if (oldRoles != null)
//                billerUser.getRoles().removeAll(billerUser.getRoles());
//
//            billerUser = generate(billerUser, billerUserRequest, operatorUser, isUpdate, entityType);
//            billerUser = save(billerUser,null,isUpdate);
//
//            //send a mail to all the authorizers for awareness
//            sendAwarenessMail(billerUser,null,isUpdate,fromEmail);
//        }else
//        {
//            Biller biller = billerService.getBillerById(billerUserRequest.getBillerId());
//            if (biller==null)
//            {
//                return ResponseEntity.badRequest().body(new ErrorDetails(ng.upperlink.nibss.cmms.enums.Errors.DATA_IS_NULL.getValue().replace("{}", "Biller").replace("{name}", billerUserRequest.getBiller().getName())));
//            }
//            billerUserRequest.setBiller(biller);
//
//            if (operatorRole.getName().equals(RoleName.BANK_ADMIN))
//            {
//                BankUser operatorBankUser = bankUserService.getById(operatorUser.getId());
//                Set<Biller> ownedBillers = operatorBankUser.getUserBank().getOwnedBillers();
//                if( ownedBillers.contains(biller))
//                    return ResponseEntity.badRequest().body("Users cannot be created for this biller because it is owned by your bank");
//
//            }
//            billerUser = generate(new BillerUser(), billerUserRequest, operatorUser, isUpdate, entityType);
//            String password = userService.generatePassword();
//            if (!password.isEmpty())
//                billerUser.setPassword(EncyptionUtil.doSHA512Encryption(password, salt));
//            billerUser = save(billerUser,password,isUpdate);
//
//        }
//        return ResponseEntity.ok(billerUser);
//    }
}
