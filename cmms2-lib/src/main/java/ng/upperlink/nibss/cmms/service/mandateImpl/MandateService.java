package ng.upperlink.nibss.cmms.service.mandateImpl;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.mandates.MandateRequest;
import ng.upperlink.nibss.cmms.dto.mandates.MandateResponse;
import ng.upperlink.nibss.cmms.dto.mandates.RejectionRequests;
import ng.upperlink.nibss.cmms.enums.*;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.model.NibssUser;
import ng.upperlink.nibss.cmms.model.Rejection;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.bank.BankUser;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.biller.BillerUser;
import ng.upperlink.nibss.cmms.model.biller.Product;
import ng.upperlink.nibss.cmms.model.mandate.Mandate;
import ng.upperlink.nibss.cmms.model.mandate.MandateStatus;
import ng.upperlink.nibss.cmms.model.pssp.PsspUser;
import ng.upperlink.nibss.cmms.repo.MandateRepo;
import ng.upperlink.nibss.cmms.service.QueueService;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.biller.ProductService;
import ng.upperlink.nibss.cmms.util.*;
import ng.upperlink.nibss.cmms.util.email.MailUtility;
import ng.upperlink.nibss.cmms.util.email.SmtpMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@Slf4j
public class MandateService {

   // private static Logger log = LoggerFactory.getLogger(MandateService.class);

    private MandateRepo mandateRepo;
    private MandateStatusService mandateStatusService;
    private UserService userService;
    private RejectionReasonsService rejectionReasonsService;
    private RejectionService rejectionService;
    private FileUploadUtils fileUploadUtils;
    private BankService bankService;
    private ProductService productService;
    private AccountLookUp accountLookUp;
    private SmtpMailSender smtpMailSender;
    private MailUtility mailUtility;
    private QueueService queueService;
    @Value("${nibss-identity-key}")
    private String nibssId;

    @Lazy
    @Autowired
    public void setQueueService(QueueService queueService) {
        this.queueService = queueService;
    }
    @Autowired
    public void setMailUtility(MailUtility mailUtility){
        this.mailUtility = mailUtility;
    }
    @Autowired
    public void setAccountLookUp(AccountLookUp accountLookUp){
        this.accountLookUp = accountLookUp;
    }

    @Autowired
    public void setSmtpMailSender(SmtpMailSender smtpMailSender) {
        this.smtpMailSender = smtpMailSender;
    }

    @Autowired
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setRejectionService(RejectionService rejectionService) {
        this.rejectionService = rejectionService;
    }

    @Autowired
    public void setMandateStatusService(MandateStatusService mandateStatusService) {
        this.mandateStatusService = mandateStatusService;
    }

    @Autowired
    public void setRejectionReasonsService(RejectionReasonsService rejectionReasonsService) {
        this.rejectionReasonsService = rejectionReasonsService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setMandateRepo(MandateRepo mandateRepo) {
        this.mandateRepo = mandateRepo;
    }

    @Autowired
    public void setUploadFileService(FileUploadUtils fileUploadUtils) {
        this.fileUploadUtils = fileUploadUtils;
    }


    public Page<Mandate> listAllMandates(Pageable pageable) {
        return mandateRepo.getAllMandates(pageable, Constants.STATUS_MANDATE_DELETED);
    }

    public List<Mandate> listAllMandates() {
        return mandateRepo.getAllMandates(Constants.STATUS_MANDATE_DELETED);
    }

    public Mandate saveMandate(Mandate mandate) {
        Mandate m = mandateRepo.save(mandate);
        return m;
    }

    public Mandate saveMandate(Mandate mandate,User user,boolean isUpdate) {
        Mandate m = mandateRepo.save(mandate);

        mailUtility.sendNotificationMail(MailUtility.MailType.NEW_MANDATE,m,user,isUpdate);
        return m;
    }

    public List<Mandate> saveBulkMandate(List<Mandate> mandateList){

        return mandateRepo.save(mandateList);
    }

    @Async
    public void updateMandate(Mandate mandate) {
        try {
            mandateRepo.save(mandate);
        } catch (Exception e) {
            log.error("An exception occurred while trying to update mandate with code: {}", mandate.getMandateCode(), e);
        }
    }

    public Mandate modifyMandate(Mandate mandate) {
        return this.saveMandate(mandate);
    }

    public Mandate getMandateByBankAndMandateId(Long id, Bank bank) {
        return mandateRepo.getMandateByBankAndMandateId(id, bank.getApiKey(), Constants.STATUS_MANDATE_DELETED);
    }

    public Mandate getMandateByBillerAndMandateId(Long mandateId, Biller biller) {
        return mandateRepo.getMandateByBillerAndMandateId(mandateId, biller.getApiKey(), Constants.STATUS_MANDATE_DELETED);
    }

    public Mandate getByPSSPAndMandateId(Long mandateId,String owner) {
        return mandateRepo.getMandateByPSSPAndMandateId(mandateId, owner, Constants.STATUS_MANDATE_DELETED);
    }

    public Page<Mandate> getAllMandates(String apiKey,Long id, Pageable pageable, List<Long> statusIdList) {
        return mandateRepo.getAllMandates(apiKey,pageable, Constants.STATUS_MANDATE_DELETED, statusIdList);
    }

    public Long getMaxMandate() {
        return mandateRepo.getMandateMaxId();
    }

    public Mandate getMandateByMandateId(Long id) {
        return mandateRepo.getMandateByMandateId(id, Constants.STATUS_MANDATE_DELETED);
    }

    public Page<Mandate> getMandatesByPSSP(String owner, Pageable pageable, List<Long> statusList){
        return mandateRepo.getMandatesByPSSP(Constants.STATUS_MANDATE_DELETED, owner, pageable, statusList);
    }

    public Mandate generateMandate(Mandate mandate, MandateRequest mandateRequest, User userOperator,
                                   String mandateCode, Product product, Bank bank, String userRole, Biller biller, boolean isUpdate) throws ParseException {



        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);

//        mandate.setStartDate(dateFormat.parse(mandateRequest.getMandateStartDate()));
//        mandate.setEndDate(dateFormat.parse(mandateRequest.getMandateEndDate()));

        String startDate = mandateRequest.getMandateStartDate() + " " + (String.format("%02d:%02d:%02d",hour, minute, second));
        String endDate = mandateRequest.getMandateEndDate() + " " + (String.format("%02d:%02d:%02d",hour, minute, second));
        mandate.setBvn(mandateRequest.getBvn());
        mandate.setStartDate(dateFormat.parse(startDate));
        mandate.setEndDate(dateFormat.parse(endDate));
        mandate.setFrequency(mandateRequest.getFrequency());
        mandate.setEmail(mandateRequest.getEmail());
        mandate.setAccountName(mandateRequest.getAccountName());
        mandate.setAccountNumber(mandateRequest.getAccountNumber());
        mandate.setBank(new Bank(bank.getId(), bank.getCode(), bank.getName()));   //subscriber's bank
        mandate.setLastActionBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
        mandate.setPayerName(mandateRequest.getPayerName());
        mandate.setPayerAddress(mandateRequest.getPayerAddress());
        mandate.setProduct(new Product(product.getId(),product.getName(),product.getAmount(),product.getDescription()));
        mandate.setPhoneNumber(mandateRequest.getPhoneNumber());
        mandate.setNarration(mandateRequest.getNarration());
        mandate.setChannel(Channel.PORTAL);
        mandate.setBiller(new Biller(biller.getId(),biller.getName(),biller.getRcNumber(),biller.getAccountNumber(),biller.getDescription(),biller.getAccountName()));
        mandate.setFixedAmountMandate(mandateRequest.isFixedAmountMandate());
        mandate.setMandateType(mandateRequest.isFixedAmountMandate() ? MandateRequestType.FIXED : MandateRequestType.VARIABLE); //changes made here
        mandate.setSubscriberCode(mandateRequest.getBillerSubscriberRef());
        mandate.setMandateCategory(MandateCategory.PAPER);
        mandate.setAmount(mandateRequest.isFixedAmountMandate() ? mandateRequest.getAmount() : null);  //amount is set here for a fixed mandate
        mandate.setVariableAmount(mandateRequest.isFixedAmountMandate() ? null : mandateRequest.getVariableAmount());
        MandateFrequency m = MandateFrequency.findById(mandateRequest.getFrequency());
        mandate.setScheduleTime(m.getDescription());
        if (mandate.getFrequency() > 0) {
            Date nextDebitDate = DateUtils.calculateNextDebitDate(mandate.getStartDate(), mandate.getEndDate(),
                    mandate.getFrequency());
            mandate.setNextDebitDate(nextDebitDate == null ? DateUtils.lastSecondOftheDay(mandate.getEndDate())
                    : DateUtils.nullifyTime(nextDebitDate));
        }

        if (!isUpdate) {
            //Creating new mandates
            MandateStatus mandateStatus = null;
            if (userRole.equals(RoleName.BANK_BILLER_INITIATOR.getValue())) {
                mandateStatus = mandateStatusService.getMandateStatusById(Constants.BANK_BILLER_INITIATE_MANDATE);
                mandate.setStatus(mandateStatus);
                mandate.setWorkflowStatus(mandateStatus.getName());
                mandate.setMandateOwnerKey(((BankUser) userOperator).getUserBank().getApiKey());
            } else if (userRole.equals(RoleName.BILLER_INITIATOR.getValue())) {
                mandateStatus = mandateStatusService.getMandateStatusById(Constants.BILLER_INITIATE_MANDATE);
                mandate.setStatus(mandateStatus);
                mandate.setWorkflowStatus(mandateStatus.getName());
                mandate.setMandateOwnerKey(((BillerUser) userOperator).getBiller().getApiKey());
            } else if (userRole.equals(RoleName.NIBSS_INITIATOR.getValue())) {
                mandateStatus = mandateStatusService.getMandateStatusById(Constants.NIBSS_BILLER_INITIATE_MANDATE);
                mandate.setStatus(mandateStatus);
                mandate.setWorkflowStatus(mandateStatus.getName());
                mandate.setMandateOwnerKey(nibssId);
            } else if(userRole.equals(RoleName.PSSP_INITIATOR.getValue())){
                mandateStatus = mandateStatusService.getMandateStatusById(Constants.PSSP_INITIATE_MANDATE);
                mandate.setStatus(mandateStatus);
                mandate.setWorkflowStatus(mandateStatus.getName());
                mandate.setMandateOwnerKey(((PsspUser) userOperator).getPssp().getApiKey());
            }

            mandate.setRequestStatus(Constants.STATUS_ACTIVE);
            mandate.setMandateCode(mandateCode);
            mandate.setDateCreated(new Date());
            mandate.setRejection(null);
            mandate.setCreatedBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
        } else {
            mandate.setUpdatedAt(new Date());
            mandate.setDateModified(new Date());
        }

        return this.saveMandate(mandate,userOperator,isUpdate);

    }

    public ResponseEntity<Object> performMandateOperations(User userOperator, Mandate mandate, String action) {
        try {
            mandate.setDateModified(new Date());
            mandate.setLastActionBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
            String message = null;
            switch (action) {
                case "activate":
                    message = "Mandate with code " + "'" + mandate.getMandateCode() + "'" + " was successfully activated!";
                    mandate.setRequestStatus(Constants.STATUS_ACTIVE);
                    break;
                case "suspend":
                    message = "Mandate with code " + "'" + mandate.getMandateCode() + "'" + " was successfully suspended!";
                    mandate.setRequestStatus(Constants.STATUS_MANDATE_SUSPENDED);
                    mandate.setDateSuspended(new Date());
                    break;
                case "delete":
                    message = "Mandate with code " + "'" + mandate.getMandateCode() + "'" + " was successfully deleted!";
                    mandate.setRequestStatus(Constants.STATUS_MANDATE_DELETED);
                    mandate.setDateModified(new Date());
                    break;
                default:
                    break;

            }
            return ResponseEntity.ok(new MandateResponse(message, this.modifyMandate(mandate)));
        } catch (Exception ex) {
            log.error("Exception thrown while trying to perform action on mandate with code {} ",mandate.getMandateCode(),ex);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> processMandateByBanks(Long id, String action,
                                                        HttpServletRequest request, User userOperator, RejectionRequests req) {
        String role = userOperator.getRoles().stream().map(r -> r.getName().getValue()).collect(Collectors.toList()).get(0);

        String message = "";
        Mandate mandate = null;
        boolean flag = false;
        try {
            mandate = this.getMandateByMandateId(id);

            if (mandate == null) {
                return new ResponseEntity<Object>(new ErrorDetails("Mandate not Found!"), HttpStatus.NOT_FOUND);
            }


            //This authorizationStatus can only be performed by Bank Initiator <<===>>> on mandates from Biller_Authorizer, Bank_Biller_Authorizer or NIBSS Initiator
            if ("approve".equalsIgnoreCase(action)
                    && (mandate.getStatus().getId() == Constants.BILLER_AUTHORIZE_MANDATE ||
                    mandate.getStatus().getId() == Constants.BANK_BILLER_AUTHORIZE_MANDATE ||
                    mandate.getStatus().getId() == Constants.NIBSS_BILLER_AUTHORIZE_MANDATE)
                    && role.equalsIgnoreCase(RoleName.BANK_INITIATOR.getValue())) {

                log.info("Role {} ", RoleName.BANK_INITIATOR.getValue());

                mandate.setStatus(mandateStatusService.getMandateStatusById(Constants.BANK_AUTHORIZE_MANDATE));
                mandate.setAcceptedBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
                mandate.setLastActionBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
                mandate.setDateAuthorized(new Date());
                mandate.setDateModified(new Date());
                mandate.setWorkflowStatus(MandateStatusType.getMandateStatusDescriptionById(MandateStatusType.BANK_AUTHORIZE_MANDATE.getId()));

                this.modifyMandate(mandate);
                message = "Mandate with code " + "'" + mandate.getMandateCode() + "'" + " was successfully approved!";
                flag = true;

                //send a mail after this action
               mailUtility.sendNotificationMail(MailUtility.MailType.MANDATE_ACCEPTED,mandate,userOperator,false);

            }

            //This authorizationStatus can be performed by a Bank_Authorizer  <<===>> on mandates from a Bank_Initiator
            else if ("approve".equalsIgnoreCase(action)
                    && mandate.getStatus().getId() == Constants.BANK_AUTHORIZE_MANDATE &&
                    role.equalsIgnoreCase(RoleName.BANK_AUTHORIZER.getValue())) {

                log.info("Role {} ", RoleName.BANK_AUTHORIZER.getValue());

                mandate.setStatus(mandateStatusService.getMandateStatusById(Constants.BANK_APPROVE_MANDATE));
                mandate.setApprovedBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
                mandate.setDateApproved(new Date());
                mandate.setLastActionBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
                mandate.setWorkflowStatus(MandateStatusType.getMandateStatusDescriptionById(MandateStatusType.BANK_APPROVE_MANDATE.getId()));

                mandate.setDateModified(new Date());
                message = "Mandate with code " + "'" + mandate.getMandateCode() + "'" + " was successfully authorized!";
                this.modifyMandate(mandate);
                flag = true;

                mailUtility.sendNotificationMail(MailUtility.MailType.MANDATE_APPROVED,mandate,userOperator,false);

                      //send a mandate advice
                String mandateInString = CommonUtils.convertObjectToJson(mandate);
                queueService.sendMandateAdvice(mandateInString);

            }
            //This authorizationStatus can be performed by a Bank_Biller_Authorizer  <<===>> on mandates from a Bank_Biller_Initiator
            else if ("approve".equalsIgnoreCase(action) && mandate.getStatus().getId() == Constants.BANK_BILLER_INITIATE_MANDATE &&
                    role.equalsIgnoreCase(RoleName.BANK_BILLER_AUTHORIZER.getValue())) {

                log.info("Role {} ", RoleName.BANK_BILLER_AUTHORIZER.getValue());

                mandate.setStatus(mandateStatusService.getMandateStatusById(Constants.BANK_BILLER_AUTHORIZE_MANDATE));
                mandate.setAcceptedBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
                mandate.setLastActionBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
                mandate.setDateAccepted(new Date());
                mandate.setDateModified(new Date());
                mandate.setWorkflowStatus(MandateStatusType.getMandateStatusDescriptionById(MandateStatusType.BANK_BILLER_AUTHORIZE_MANDATE.getId()));

                this.modifyMandate(mandate);
                message = "Mandate with code " + "'" + mandate.getMandateCode() + "'" + " was successfully approved!";
                flag = true;

                mailUtility.sendNotificationMail(MailUtility.MailType.MANDATE_AUTHORIZED,mandate,userOperator,false);
            }

            if (flag) {
                return new ResponseEntity<>(new MandateResponse(message, mandate), HttpStatus.OK);
            }

        } catch (Exception e) {
            log.error("Exception thrown while processing mandate ",e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.info("This User cannot act on the mandate with status {}", mandate.getStatus().getName().toUpperCase().replace(" ", "_"));
        return new ResponseEntity<Object>(new ErrorDetails("User not permitted to act on this mandate."), HttpStatus.UNAUTHORIZED);

    }

    public ResponseEntity<Object> rejectMandatesByBanks(Long id, String action,
                                                        HttpServletRequest request, User userOperator, RejectionRequests req) {
        String role = userOperator.getRoles().stream().map(r -> r.getName().getValue()).collect(Collectors.toList()).get(0);
        String message = "";
        Mandate mandate = null;
        boolean flag = false;
        RoleName roleName = null;

        try {
            mandate = this.getMandateByMandateId(id);

            if (mandate == null) {
                return new ResponseEntity<Object>(new ErrorDetails("Mandate not Found!"), HttpStatus.NOT_FOUND);
            }

            if (action.equalsIgnoreCase("reject")) {
                //disapprove mandates which were last acted upon by Biller_Authorizers and Bank_Biller_Initiators (Bank_Initiator role only)
                if (role.equalsIgnoreCase(RoleName.BANK_INITIATOR.getValue())) {
                    if ((mandate.getStatus().getId() == Constants.BILLER_AUTHORIZE_MANDATE ||
                            mandate.getStatus().getId() == Constants.BANK_BILLER_AUTHORIZE_MANDATE)) {
                        flag = true;
                    } else {
                        return new ResponseEntity<Object>(new ErrorDetails("User is not permitted to reject this mandate!"), HttpStatus.UNAUTHORIZED);
                    }
                }

                //disapprove of mandates that were last acted upon by bank_initiators (Bank_Authorizer role only)
                if (role.equalsIgnoreCase(RoleName.BANK_AUTHORIZER.getValue())) {
                    if (mandate.getStatus().getId() == Constants.BANK_AUTHORIZE_MANDATE) {
                        flag = true;
                    } else {
                        return new ResponseEntity<Object>(new ErrorDetails("User is not permitted to reject this mandate!"), HttpStatus.UNAUTHORIZED);
                    }
                }

                //disapprove of mandates that were last acted upon by bank_biller_initiators
                if (role.equalsIgnoreCase(RoleName.BANK_BILLER_AUTHORIZER.getValue())) {
                    if (mandate.getStatus().getId() == Constants.BANK_BILLER_INITIATE_MANDATE) {
                        flag = true;
                    } else {
                        return new ResponseEntity<Object>(new ErrorDetails("User is not permitted to reject this mandate!"), HttpStatus.UNAUTHORIZED);
                    }
                }
            }

            if (flag) {
                mandate.setStatus(mandateStatusService.getMandateStatusById(Constants.BANK_REJECT_MANDATE));
                mandate.setLastActionBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
                mandate.setDateModified(new Date());

                Rejection r = new Rejection();
                r.setComment(req.getComment());
                r.setDateRejected(new Date());
                r.setRejectionReason(rejectionReasonsService.getOne(req.getRejectionId()));
                mandate.setRejection(r);
                message = "Mandate was successfully Rejected!";

                this.modifyMandate(mandate);

                //send a mail after this action
                mailUtility.sendNotificationMail(MailUtility.MailType.MANDATE_REJECTED,mandate,userOperator,false);


                return new ResponseEntity<Object>(new MandateResponse(message, mandate), HttpStatus.OK);
            }

        } catch (Exception ex) {
            log.error("Error thrown while processing mandate rejection with code {} ",mandate.getMandateCode(), ex);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new ErrorDetails("Request processing failed!"), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Object> processMandateByBillers(Mandate mandate, String action,
                                                          HttpServletRequest request, User userOperator, RejectionRequests req) {
        String role = userOperator.getRoles().stream().map(r -> r.getName().getValue()).collect(Collectors.toList()).get(0);
        String message = "";
        boolean flag = false;
        try {
            //This authorizationStatus can only be performed by Biller Authorizer
            if ("approve".equalsIgnoreCase(action)) {
                if (mandate.getStatus().getId() == Constants.BILLER_INITIATE_MANDATE &&
                        role.equalsIgnoreCase(RoleName.BILLER_AUTHORIZER.getValue())) {

                    mandate.setStatus(mandateStatusService.getMandateStatusById(Constants.BILLER_AUTHORIZE_MANDATE));
                    mandate.setAuthorizedBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
                    mandate.setLastActionBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
                    mandate.setDateAuthorized(new Date());
                    mandate.setDateModified(new Date());
                    mandate.setWorkflowStatus(MandateStatusType.getMandateStatusDescriptionById(MandateStatusType.BILLER_AUTHORIZE_MANDATE.getId()));

                    this.modifyMandate(mandate);
                    message = "Mandate with code " + "'" + mandate.getMandateCode() + "'" + " was successfully authorized!";
                    flag = true;

                        //send a mail afterwards
                    mailUtility.sendNotificationMail(MailUtility.MailType.BILLER_AUTHORIZED_MANDATES,mandate,userOperator,false);

                }else if(mandate.getStatus().getId() == Constants.BILLER_AUTHORIZE_MANDATE){
                    return new ResponseEntity<>(new ErrorDetails("Mandate has already been authorized by biller."), HttpStatus.BAD_REQUEST);
                } else {
                    return new ResponseEntity<>(new ErrorDetails("Only biller-initated mandates can be authorized by this user."), HttpStatus.BAD_REQUEST);
                }
            } else if ("reject".equalsIgnoreCase(action)) {
                //A Biller Authorizer can only reject mandates that were initiated by a Biller Initiator
                if (mandate.getStatus().getId() == Constants.BILLER_INITIATE_MANDATE) {
                    mandate.setStatus(mandateStatusService.getMandateStatusById(Constants.BILLER_REJECT_MANDATE));
                    mandate.setLastActionBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
                    mandate.setDateModified(new Date());
                    mandate.setWorkflowStatus(MandateStatusType.getMandateStatusDescriptionById(MandateStatusType.BILLER_REJECT_MANDATE.getId()));

                    Rejection r = new Rejection(req.getComment(), rejectionReasonsService.getOne(req.getRejectionId()), new Date());
                    mandate.setRejection(r);
                    message = "Mandate with code " + "'" + mandate.getMandateCode() + "'" + " was successfully rejected!";
                    this.modifyMandate(mandate);

                    flag = true;

                    mailUtility.sendNotificationMail(MailUtility.MailType.BILLER_REJECTED_MANDATES,mandate,userOperator,false);

                } else {
                    if (mandate.getStatus().getId() == Constants.BILLER_REJECT_MANDATE) {
                        return new ResponseEntity<Object>(new ErrorDetails(String.format("The mandate with code '%s' is currently rejected.", mandate.getMandateCode())), HttpStatus.BAD_REQUEST);
                    }

                    return new ResponseEntity<Object>(new ErrorDetails("This user can only reject mandates whose status is biller initiated!"), HttpStatus.BAD_REQUEST);
                }
            }

            if (flag) {
                return new ResponseEntity<Object>(new MandateResponse(message, mandate), HttpStatus.OK);
            }

        } catch (Exception e) {
            log.error("Unable to perform {} action on mandate ",action, e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Object>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity<Object> processMandateByPSSP(Mandate mandate, String action,
                                                          HttpServletRequest request, User userOperator, RejectionRequests req) {

        String role = userOperator.getRoles().stream().map(r -> r.getName().getValue()).collect(Collectors.toList()).get(0);
        String message = "";
        boolean flag = false;
        try {
            //This authorizationStatus can only be performed by PSSP Authorizer
            if ("approve".equalsIgnoreCase(action)) {
                if (mandate.getStatus().getId() == Constants.PSSP_INITIATE_MANDATE &&
                        role.equalsIgnoreCase(RoleName.PSSP_AUTHORIZER.getValue())) {

                    mandate.setStatus(mandateStatusService.getMandateStatusById(Constants.PSSP_AUTHORIZE_MANDATE));
                    mandate.setAuthorizedBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
                    mandate.setLastActionBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
                    mandate.setDateAuthorized(new Date());
                    mandate.setDateModified(new Date());
                    mandate.setWorkflowStatus(MandateStatusType.getMandateStatusDescriptionById(MandateStatusType.PSSP_AUTHORIZE_MANDATE.getId()));

                    this.modifyMandate(mandate);
                    message = "Mandate with code " + "'" + mandate.getMandateCode() + "'" + " was successfully authorized!";
                    flag = true;

                    //send a mail after this action
                    // mailUtility.sendMail(MailUtility.MailType.MANDATE_AUTHORIZED,mandate,userOperator);


                }
            } else if ("reject".equalsIgnoreCase(action)) {
                //A PSSP Authorizer can only reject mandates that were initiated by a Biller Initiator
                if (mandate.getStatus().getId() == Constants.PSSP_INITIATE_MANDATE) {
                    mandate.setStatus(mandateStatusService.getMandateStatusById(Constants.PSSP_REJECT_MANDATE));
                    mandate.setLastActionBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
                    mandate.setDateModified(new Date());
                    mandate.setWorkflowStatus(MandateStatusType.getMandateStatusDescriptionById(MandateStatusType.PSSP_REJECT_MANDATE.getId()));

                    Rejection r = new Rejection(req.getComment(), rejectionReasonsService.getOne(req.getRejectionId()), new Date());
                    mandate.setRejection(r);
                    message = "Mandate with code " + "'" + mandate.getMandateCode() + "'" + " was successfully rejected!";
                    this.modifyMandate(mandate);

                    flag = true;

                    // mailUtility.sendMail(MailUtility.MailType.MANDATE_REJECTED,mandate,userOperator);

                } else {
                    if (mandate.getStatus().getId() == Constants.PSSP_REJECT_MANDATE) {
                        return new ResponseEntity<>(new ErrorDetails(String.format("The mandate with code %s is currently rejected.", mandate.getMandateCode())), HttpStatus.BAD_REQUEST);
                    }

                    return new ResponseEntity<>(new ErrorDetails("Only Mandates whose status are PSSP INITIATED can be rejected by this user!"), HttpStatus.BAD_REQUEST);
                }
            }

            if (flag) {
                return new ResponseEntity<>(new MandateResponse(message, mandate), HttpStatus.OK);
            }

        } catch (Exception e) {
            log.error("Unable to perform {} action on mandate ",action, e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Object> processMandateByNIBBS(Mandate mandate, String action,
                                                        HttpServletRequest request, User userOperator, RejectionRequests req) {
        String role = userOperator.getRoles().stream().map(r -> r.getName().getValue()).collect(Collectors.toList()).get(0);
        String message = "";
        boolean flag = false;
        try {
            //This authorizationStatus can only be performed by NIBBS Authorizer and NIBSS Super Admin
            if ("approve".equalsIgnoreCase(action)) {
                if (mandate.getStatus().getId() == Constants.NIBSS_BILLER_INITIATE_MANDATE &&
                        (role.equalsIgnoreCase(RoleName.NIBSS_AUTHORIZER.getValue()))) {

                    mandate.setStatus(mandateStatusService.getMandateStatusById(Constants.NIBSS_BILLER_AUTHORIZE_MANDATE));
                    mandate.setAuthorizedBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
                    mandate.setLastActionBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
                    mandate.setDateAuthorized(new Date());
                    mandate.setDateModified(new Date());
                    mandate.setWorkflowStatus(MandateStatusType.getMandateStatusDescriptionById(MandateStatusType.NIBSS_BILLER_AUTHORIZE_MANDATE.getId()));

                    this.modifyMandate(mandate);
                    message = "Mandate with code " + "'" + mandate.getMandateCode() + "'" + " was successfully authorized!";
                    flag = true;

                    //send a mail after this authorizationStatus

                }
            } else if ("reject".equalsIgnoreCase(action)) {
                if (mandate.getStatus().getId() == Constants.NIBSS_BILLER_INITIATE_MANDATE) {
                    mandate.setStatus(mandateStatusService.getMandateStatusById(Constants.NIBSS_REJECT_MANDATE));
                    mandate.setLastActionBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
                    mandate.setDateModified(new Date());
                    mandate.setWorkflowStatus(MandateStatusType.getMandateStatusDescriptionById(MandateStatusType.NIBSS_REJECT_MANDATE.getId()));

                    Rejection r = new Rejection(req.getComment(), rejectionReasonsService.getOne(req.getRejectionId()), new Date());
                    mandate.setRejection(r);
                    message = "Mandate with code " + "'" + mandate.getMandateCode() + "'" + " was successfully rejected!";
                    this.modifyMandate(mandate);

                    flag = true;
                } else {
                    if (mandate.getStatus().getId() == Constants.NIBSS_REJECT_MANDATE) {
                        return new ResponseEntity<Object>(new ErrorDetails(String.format("The mandate with code %s has been rejected before", mandate.getMandateCode())), HttpStatus.BAD_REQUEST);
                    }

                    return new ResponseEntity<>(new ErrorDetails("Only Mandates whose status are NIBSS INITIATED can be rejected by this user!"), HttpStatus.BAD_REQUEST);
                }
            }

            if (flag) {
                return new ResponseEntity<>(new MandateResponse(message, mandate), HttpStatus.OK);
            }

        } catch (Exception e) {
            log.error(String.format("Exception occured while trying to %s mandate with code %s",action,mandate.getMandateCode()));
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Object>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.BAD_REQUEST);
    }

    public Page<Mandate> getMandatesByBillers(String ownerKey, Pageable pageable, List<Long> statusList) {
        return mandateRepo.getMandatesByBillers(Constants.STATUS_MANDATE_DELETED, ownerKey, pageable, statusList);
    }

//    public Page<Mandate> getMandatesByBanks(Bank bank, Pageable pageable) {
//        return mandateRepo.getMandatesByBanks(Constants.STATUS_MANDATE_DELETED, bank.getCode(), pageable);
//    }

    public Page<Mandate> getMandatesByBanksAndStatus(String role, Long statusId, Pageable pageable, Bank bank, String reqStatus) {
        if (reqStatus.equalsIgnoreCase("pending_mandate")) {
            //if the logged in user is a bank_initiator
            if (role.equalsIgnoreCase(RoleName.BANK_INITIATOR.getValue())) {
                return mandateRepo.getPendingMandatesByBanksAndStatus(statusId, Constants.BANK_BILLER_AUTHORIZE_MANDATE.longValue(), bank.getApiKey(), pageable, Constants.STATUS_MANDATE_DELETED, Constants.STATUS_MANDATE_SUSPENDED);
            }

            return mandateRepo.getMandatesByBanksAndStatus(statusId, bank.getApiKey(), pageable, Constants.STATUS_MANDATE_DELETED, Constants.STATUS_MANDATE_SUSPENDED);

        } else {
            return reqStatus.equalsIgnoreCase("suspended_mandate") ? this.mandateRepo.getSuspendedMandatesByBank(bank.getApiKey(),Constants.STATUS_MANDATE_SUSPENDED, pageable) :
                    this.mandateRepo.getMandatesAuthorizedByBanks(statusId, bank.getApiKey(),pageable, Constants.STATUS_MANDATE_DELETED);
        }

    }

    // get all due mandates
    public List<Mandate> getAllDueMandates() {
        // get bank approved mandate status and biller approved mandate status
        List<String> names = Arrays.asList("Biller Approved", "Bank Approved"); /** TODO: confirm the approval levels for this mandate **/
        List<MandateStatus> mandateStatuses = mandateStatusService.getMandateStatusesByName(names);
        return mandateRepo.getDueMandates(true, new Date(), Constants.STATUS_ACTIVE, Channel.PORTAL, mandateStatuses);
    }

    public Page<Mandate> getMandatesByBillerAndStatus(Long statusId, Pageable pageable, String ownerKey, String requestStatus) {
        if (requestStatus.equals("biller_initiate_mandate")) {
            return mandateRepo.getMandatesByBillerAndStatus(statusId, ownerKey, pageable, Constants.STATUS_MANDATE_SUSPENDED, Constants.STATUS_MANDATE_DELETED);
        } else if (requestStatus.equalsIgnoreCase("biller_suspended_mandate")) {
            return mandateRepo.getSuspendedMandatesByBiller(ownerKey, Constants.STATUS_MANDATE_SUSPENDED, pageable);
        } else {
            return mandateRepo.getMandatesByBillerAndStatus(statusId, ownerKey, pageable, Constants.STATUS_MANDATE_DELETED);
        }

    }

    public Page<Mandate> getMandatesByPSSPAndStatus(Long statusId, Pageable pageable,String owner, String requestStatus) {
        if (requestStatus.equals("pssp_initiate_mandate")) {
            return mandateRepo.getMandatesByPSSPAndStatus(statusId, owner, pageable, Constants.STATUS_MANDATE_SUSPENDED, Constants.STATUS_MANDATE_DELETED);
        } else if (requestStatus.equalsIgnoreCase("pssp_suspended_mandate")) {
            return mandateRepo.getSuspendedMandatesByPSSP(owner, Constants.STATUS_MANDATE_SUSPENDED, pageable);
        } else {
            return mandateRepo.getMandatesByPSSPAndStatus(statusId, owner, pageable, Constants.STATUS_MANDATE_DELETED);
        }

    }

    public Page<Mandate> getMandatesByNIBBSAndStatus(Long statusId, Pageable pageable, NibssUser nibssUser, String requestStatus) {
        if (requestStatus.equals("nibss_initiate_mandate")) {
            return mandateRepo.getMandatesByNIBSSAndStatus(statusId,pageable, Constants.STATUS_MANDATE_SUSPENDED, Constants.STATUS_MANDATE_DELETED);
        } else if (requestStatus.equalsIgnoreCase("nibss_suspended_mandate")) {
            return mandateRepo.getSuspendedMandatesByNIBSS(Constants.STATUS_MANDATE_SUSPENDED, pageable);
        } else {
            return mandateRepo.getMandatesByNIBSSAndStatus(statusId,pageable, Constants.STATUS_MANDATE_DELETED);
        }
    }

    public ResponseEntity<Object> processSaveUpdate(MandateRequest requestObject,
                                                    HttpServletRequest servletRequest,
                                                    UserDetail userDetail, boolean isUpdate) {

        String message = "";
        String mandateCode = "";

        //validate account info
        if (!accountLookUp.validateAccount(requestObject.getAccountNumber(), Constants.ACC_NUMBER_MAX_DIGITS)) {
            return ResponseEntity.badRequest().body(new ErrorDetails(String.format("Account number must be digits and not less than %d characters", Constants.ACC_NUMBER_MAX_DIGITS)));
        }

        //get the user who logged in
        User userOperator = userService.get(userDetail.getUserId());
        if (userOperator == null) {
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
        }


        if (userOperator instanceof BillerUser) {
            Biller billerInDb = ((BillerUser) userOperator).getBiller();
            if (billerInDb.getId() != Long.parseLong(requestObject.getBiller())) {
                return ResponseEntity.badRequest().body(new ErrorDetails("Biller selected is not known!"));
            }
        }

        //get role
        String role = userOperator.getRoles().stream().map(r -> r.getName().getValue()).collect(Collectors.toList()).get(0);

        //only Bank_Biller_Initiators,Biller_Initiators, PSSP_Initiators and NIBSS_Initiators can upload mandates
        String[] permittedRoles = {RoleName.BANK_BILLER_INITIATOR.getValue(), RoleName.BILLER_INITIATOR.getValue(),
                RoleName.NIBSS_INITIATOR.getValue(),RoleName.PSSP_INITIATOR.getValue()};

        boolean found = Arrays.asList(permittedRoles).contains(role);

        if (!found) {
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.NOT_PERMITTED.getValue()));
        }

        Mandate mandate = null;
        try {
            Product product = productService.getProductById(Long.parseLong(requestObject.getProduct()));

            if(product == null){
                return new ResponseEntity<>(new ErrorDetails("Product not found!"), HttpStatus.BAD_REQUEST);
            }

            Biller biller = product.getBiller();
            String rcNumber = biller.getRcNumber();

            //get the subscriber's bank
            Bank bank = bankService.getBankByCode(requestObject.getBankCode());

            if(bank == null){
                return new ResponseEntity<>(new ErrorDetails("Subscriber's bank not found!"), HttpStatus.BAD_REQUEST);
            }

            if (!isUpdate) {
                message = "created.";
                mandate = new Mandate();
                mandateCode = MandateUtils.getMandateCode(String.valueOf(System.currentTimeMillis()), rcNumber, String.valueOf(product.getId()));

            } else {
                message = "updated.";
                mandate = this.getMandateByMandateId(requestObject.getId());
                if (mandate == null) {
                    return new ResponseEntity<>(new ErrorDetails("Mandate was not found!"), HttpStatus.BAD_REQUEST);
                }
                //check if the mandate is suspended
                if (mandate.getRequestStatus() == Constants.STATUS_MANDATE_SUSPENDED) {
                    return new ResponseEntity<>(new ErrorDetails("Mandate is currently suspended!"), HttpStatus.BAD_REQUEST);
                }

                mandateCode = mandate.getMandateCode();
            }

            uploadMandateImage(requestObject, servletRequest, mandateCode, userDetail, mandate,null);

            //generate mandate
            mandate = generateMandate(mandate, requestObject, userOperator, mandateCode, product, bank, role, biller, false);

            return new ResponseEntity<>(new MandateResponse("Mandate successfully " + message, mandate), HttpStatus.OK);
        } catch(ParseException e){
            log.error("Error parsing dates => ", e);
            return new ResponseEntity<>(new ErrorDetails("Date format is incorrect."), HttpStatus.BAD_REQUEST);
        }catch (Exception ex) {
            log.error("Unable to save or update mandate => ", ex);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String getUploadedMandateImage(Mandate mandate, HttpServletRequest request) {
        return fileUploadUtils.decodeToBase64(mandate, request);
    }

    public void uploadMandateImage(MandateRequest requestObject,
                                   HttpServletRequest servletRequest, String mandateCode, UserDetail userDetail, Mandate mandate,
                                   String imgString) throws IOException,Exception{

        fileUploadUtils.processUploads(requestObject, servletRequest, mandateCode, userDetail, mandate,imgString);
    }

    public boolean verifyUserRole(String[] permittedRoles, String userLoggedInRole) {
        return Arrays.asList(permittedRoles).contains(userLoggedInRole);
    }

    public List<Mandate> getMandatesYearToDate(Date startDate,Date today){
        return mandateRepo.getMandatesYearToDate(startDate,today,Constants.STATUS_MANDATE_DELETED);
    }

    public List<Mandate> getBillerUserMandatesYTD(Date startDate,Date today,String ownerKey){
        return mandateRepo.getBillerMandatesYearToDate(startDate,today,Constants.STATUS_MANDATE_DELETED,ownerKey);
    }

    public List<Mandate> getBankUserMandatesYTD(Date startDate,Date today,String apiKey,Long id){
        return mandateRepo.getBankMandatesYearToDate(startDate,today,Constants.STATUS_MANDATE_DELETED,apiKey);
    }
}
