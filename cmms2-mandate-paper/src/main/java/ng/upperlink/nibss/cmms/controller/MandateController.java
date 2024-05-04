package ng.upperlink.nibss.cmms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.mandates.ExcelMandate;
import ng.upperlink.nibss.cmms.dto.mandates.MandateRequest;
import ng.upperlink.nibss.cmms.dto.mandates.MandateResponse;
import ng.upperlink.nibss.cmms.dto.mandates.RejectionRequests;
import ng.upperlink.nibss.cmms.enums.*;
import ng.upperlink.nibss.cmms.enums.MandateFrequency;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.exceptions.ServerBusinessException;
import ng.upperlink.nibss.cmms.model.*;
import ng.upperlink.nibss.cmms.service.*;
import ng.upperlink.nibss.cmms.service.mandateImpl.MandateService;
import ng.upperlink.nibss.cmms.service.mandateImpl.MandateStatusService;
import ng.upperlink.nibss.cmms.service.mandateImpl.RejectionReasonsService;
import ng.upperlink.nibss.cmms.utils.ExcelUtility;
import ng.upperlink.nibss.cmms.utils.FileUploadUtils;
import ng.upperlink.nibss.cmms.utils.FileUtility;
import ng.upperlink.nibss.cmms.utils.MandateUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springfox.documentation.annotations.ApiIgnore;

import javax.naming.SizeLimitExceededException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@RestController
@RequestMapping("/mandate")
public class MandateController {
//    private final Path rootLocation = Paths.get("upload-dir");
//    private Logger logger = LoggerFactory.getLogger(MandateController.class);
//    private MandateService mandateService;
//    private ProductService productService;
//    private FileUploadUtils fileUploadUtils;
//    private BankService bankService;
//    private UserService userService;
//    private MandateStatusService mandateStatusService;
//    private BankUserService bankUserService;
//    private BillerService billerService;
//    private RejectionReasonsService rejectionReasonsService;
//
//    @Autowired
//    public void setRejectionReasonsService(RejectionReasonsService rejectionReasonsService) {
//        this.rejectionReasonsService = rejectionReasonsService;
//    }
//
//    @Autowired
//    public void setBillerService(BillerService billerService) {
//        this.billerService = billerService;
//    }
//
//    @Autowired
//    public void setBankUserService(BankUserService bankUserService) {
//        this.bankUserService = bankUserService;
//    }
//
//    @Autowired
//    public void setUserService(UserService userService) {
//        this.userService = userService;
//    }
//
//    @Autowired
//    public void setBankService(BankService bankService) {
//        this.bankService = bankService;
//    }
//
//    @Autowired
//    public void setUploadFileService(FileUploadUtils fileUploadUtils) {
//        this.fileUploadUtils = fileUploadUtils;
//    }
//
//    @Autowired
//    public void setMandateService(MandateService mandateService) {
//        this.mandateService = mandateService;
//    }
//
//    @Autowired
//    public void setMandateStatusService(MandateStatusService mandateStatusService) {
//        this.mandateStatusService = mandateStatusService;
//    }
//
//    @Autowired
//    public void setProductService(ProductService productService) {
//        this.productService = productService;
//    }
//
//
//    @PostMapping("/add")
//    public ResponseEntity createMandate(@RequestParam("request") String request,
//                                        @RequestParam("file") MultipartFile file,
//                                        HttpServletRequest servletRequest,
//                                        @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
//
//        Mandate mandate = new Mandate();
//
//        try {
//            //Ensure that it is only an OPERATOR that can do this action;
//            if (MakerCheckerType.OPERATOR != MakerCheckerType.valueOf(userDetail.getUserAuthorizationType())) {
//                return ResponseEntity.badRequest().body(new ErrorDetails(Errors.NOT_PERMITTED.getValue()));
//            }
//
//            //get the user who logged in
//            User userOperator = userService.get(userDetail.getUserId());
//            if (userOperator == null) {
//                return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
//            }
//
//                 //construct the object from the request body
//            ObjectMapper mapper = new ObjectMapper();
//            MandateRequest requestObject = mapper.readValue(request, MandateRequest.class);
//
//
//                 //validate to make sure end date is greater than start date
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            Date startDate = sdf.parse(requestObject.getMandateStartDate());
//            Date endDate = sdf.parse(requestObject.getMandateEndDate());
//
//            if (endDate.before(startDate)) {
//                return ResponseEntity.badRequest().body(new ErrorDetails(Errors.INVALID_DATE_FORMAT.getValue()));
//            }
//
//            //check if the person is a bank initiator or a biller initiator
//            // String role = userDetail.getRoles().stream().collect(Collectors.toList()).get(0);
//
//            String role = userOperator.getRoles().stream().map(Role::getName).collect(Collectors.toList()).get(0);
//
//            String[] permittedRoles = {RoleName.BILLER_INITIATOR.getValue(), RoleName.BANK_INITIATOR.getValue()};
//
//            boolean found = Arrays.asList(permittedRoles).contains(role);
//
//            if (!found) {
//                return ResponseEntity.badRequest().body(new ErrorDetails(Errors.NOT_PERMITTED.getValue()));
//            }
//
//            System.out.println(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
//
//            Product product = productService.getAllProductById(Long.parseLong(requestObject.getProduct()));
//            Bank bank = bankService.getBankByCode(requestObject.getBankCode());
//
//            //To be changed later
//            String randomStr = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
//            //String rcNumber = product != null ? product.getBiller().getCompany().getRcNumber() : "";
//            String rcNumber = product != null ? randomStr : "";
//
//            String mandateCode = MandateUtils.getMandateCode(mandateService.getMaxMandate(), rcNumber, String.valueOf(product.getId()));
//
//            //validate file to upload
//            if (fileUploadUtils.validateFileToUpload(file) != null) {
//                return fileUploadUtils.validateFileToUpload(file);
//            }
//
//
//            fileUploadUtils.uploadFile(file, mandateCode, servletRequest, userDetail.getUserId(), mandate);
//
//                           //generate mandate
//            mandate = mandateService.generateMandate(mandate, requestObject, userOperator, mandateCode, product, bank);
//
//            //validate the credentials
//            if (role.equals(RoleName.BANK_INITIATOR.getValue())) {
//                //set the bank to the one being operated by the user creator
//
//            } else if (role.equals(RoleName.BILLER_INITIATOR.getValue())) {
//
//            } else {
//
//            }
//
//
//        } catch (Exception ex) {
//            logger.error("---Exception trace --- {} ", ex);
//            return ResponseEntity.badRequest().body(new ErrorDetails(ex.getMessage()));
//        }
//
//        return ResponseEntity.ok(new MandateResponse("Mandate Successfully Saved", mandate));
//
//    }
//
//
//    @GetMapping("/list")
//    public ResponseEntity<Object> listAllMandates(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail,
//                                                  Pageable pageable) {
//        //restrict view mandates to only those authorized to do so
//        try {
//            return new ResponseEntity<Object>(mandateService.listAllMandates(pageable), HttpStatus.OK);
//        } catch (Exception ex) {
//            logger.error("Exception thrown {}", ex);
//            return new ResponseEntity<Object>(new ErrorDetails(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//    }
//
//    //For Bank users
//    @GetMapping("/view/{id}")
//    public ResponseEntity<Object> showMandateById(@PathVariable("mandateId") Long mandateId,
//                                                  HttpServletRequest request,
//                                                  @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
//
//        //get the current user
//        BankUser bankUser = bankUserService.getById(userDetail.getUserId());
//        if (bankUser == null) {
//            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
//        }
//
//        Bank userBank = bankService.getBankByCode(bankUser.getUserBank());
//
//
//        Mandate mandate = null;
//        List<RejectionReason> rejectionReasons = new ArrayList<>();
//        try {
//            return new ResponseEntity<Object>(mandateService.getMandateByBankAndMandateId(mandateId, userBank), HttpStatus.OK);
//            // rejectionReasons = mandateService.getRejectionReasons();
//        } catch (Exception e) {
//            logger.error("Exception caught {}", e);
//            return new ResponseEntity<Object>(new ErrorDetails(e.getMessage()), HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @PutMapping(value = "/accept/{mandateId}")
//    public ResponseEntity<Object> acceptMandate(@PathVariable("mandateId") Long mandateId,
//                                                HttpServletRequest request,
//                                                @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
//
//        User userOperator = userService.get(userDetail.getUserId());
//        if (userOperator == null) {
//            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
//        }
//
//        String role = userOperator.getRoles().stream().map(Role::getName).collect(Collectors.toList()).get(0);
//
//        //allow only bank initiators access to this activity
//        if (!role.equals(RoleName.BANK_INITIATOR.getValue())) {
//            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.NOT_PERMITTED.getValue()));
//        }
//
//        return processMandate(mandateId, "accept", request, userOperator,null);
//    }
//
//    @PutMapping(value = "/approve/{mandateId}")
//    public ResponseEntity<Object> approveMandate(@PathVariable("mandateId") Long mandateId,
//                                                 HttpServletRequest request,
//                                                 @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
//
//        User userOperator = userService.get(userDetail.getUserId());
//        if (userOperator == null) {
//            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
//        }
//
//        String role = userOperator.getRoles().stream().map(Role::getName).collect(Collectors.toList()).get(0);
//
//        //allow only bank initiators access to this activity
//        if (!role.equals(RoleName.BANK_AUTHORIZER.getValue())) {
//            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.NOT_PERMITTED.getValue()));
//        }
//
//        return processMandate(mandateId, "approve", request, userOperator,null);
//    }
//
//    @PutMapping(value = "/reject/{mandateId}")
//    public ResponseEntity<Object> rejectMandate(
//                                                 @RequestBody RejectionRequests rejectionRequest,
//                                                 @PathVariable("mandateId") Long mandateId,
//                                                 HttpServletRequest request,
//                                                 @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
//
//        User userOperator = userService.get(userDetail.getUserId());
//        if (userOperator == null) {
//            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
//        }
//
//        String role = userOperator.getRoles().stream().map(Role::getName).collect(Collectors.toList()).get(0);
//
//        String[] permittedRoles = {RoleName.BANK_INITIATOR.getValue(), RoleName.BANK_AUTHORIZER.getValue()};
//
//        boolean found = Arrays.asList(permittedRoles).contains(role);
//
//        if (!found) {
//            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.NOT_PERMITTED.getValue()));
//        }
//
//        return processMandate(mandateId, "reject", request, userOperator,rejectionRequest);
//    }
//
//
//
//    @GetMapping("/mandate/delete/{id}")
//    public ResponseEntity<Object> deleteMandate(@PathVariable("id") Long mandateId,
//                                                @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
//
//        return mandateService.performMandateOperations(userDetail, mandateId, "delete");
//    }
//
//    @GetMapping(value = "/suspend/{id}")
//    public ResponseEntity suspendMandate(@PathVariable("id") Long mandateId,
//                                         @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
//
//        return mandateService.performMandateOperations(userDetail, mandateId, "suspend");
//    }
//
//    @GetMapping("/activate/{id}")
//    public ResponseEntity activateMandate(@PathVariable("id") Long mandateId,
//                                          @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
//
//        return mandateService.performMandateOperations(userDetail, mandateId, "activate");
//    }
//
//    //for search
//    @GetMapping("/status")
//    public ResponseEntity<Object> getMandateStatuses() {
//        return new ResponseEntity<Object>(mandateStatusService.getMandateStatuses(), HttpStatus.OK);
//    }
//
//    @GetMapping(value = "/frequencies")
//    public ResponseEntity<Object> getMandateFrequencies(){
//        return new ResponseEntity<Object>(MandateFrequency.getMandateFrequencies(),HttpStatus.OK);
//    }
//
//    @PostMapping("/bulkUpload")
//    public ResponseEntity<Object> createBulkMandates(@RequestParam("file") MultipartFile file,
//                                                     HttpServletRequest request,
//                                                     @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){
//
//        return this.processBulkMandateUploads(userDetail,request,file);
//    }
//
//    public ResponseEntity<Object> processBulkMandateUploads(UserDetail userDetail,
//                                                            HttpServletRequest request,MultipartFile multipartFile){
//
//        if (multipartFile.getOriginalFilename() == null
//                || !multipartFile.getOriginalFilename().toLowerCase().endsWith(".zip")) {
//            return ResponseEntity.badRequest().body(new ErrorDetails("Invalid file or file type. Only zip files are allowed"));
//
//        } else {
//            String tempDir = request.getServletContext().getRealPath("/resources/temp") + File.separator
//                    + userDetail.getUserId() + File.separator + System.currentTimeMillis();
//
//
//            try {
//                if (!multipartFile.isEmpty()) {
//
//                    logger.info(String.format("---Setting temp upload path to [%s]---", tempDir));
//
//                    ZipFile zipFile = null;
//                    File tempDestination = new File(tempDir);
//                    logger.info("---tempDestination.exists()--" + tempDestination.exists());
//                    if (!tempDestination.exists()) {
//                        boolean made = tempDestination.mkdirs(); // create the new
//                        // temp path
//                        logger.info("---tempDestination.mkdirs()--" + made);
//                    }
//
//                    // transfer the file to the temp path
//                    multipartFile.transferTo(new File(tempDir + File.separator + multipartFile.getOriginalFilename()));
//
//                    //unzip the file
//                    FileUtility.unzip(tempDir + File.separator + multipartFile.getOriginalFilename(), tempDir);
//
//                    //get the unzipped file name(s)
//                    zipFile = new ZipFile(tempDir + File.separator + multipartFile.getOriginalFilename());
//
//                    //all the entries in the unzipped file folder
//                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
//
//
//                    List<ZipEntry> recordFiles = Collections
//                            .list(entries).stream().filter(e -> e.getName().endsWith(".xls")
//                                    || e.getName().endsWith(".xlsx") || e.getName().endsWith(".csv"))
//                            .collect(Collectors.toList());
//
//                    if (recordFiles.size() != 1) {
//                        // reject based on the multiple files
//                        logger.warn("--No file ends with xls,xlsx or csv--");
//                    } else {
//                        ZipEntry zipEntry= zipFile.getEntry(recordFiles.get(0).getName());
//                        List<ExcelMandate> mandates = new ArrayList<>();
//                        if (zipEntry.getName().endsWith(".xls") || zipEntry.getName().endsWith(".xlsx")) {
//                            logger.warn("--file ends with xls or xls--");
//                            mandates = processExcelMandateUpload(zipFile.getInputStream(zipEntry), zipEntry.getName(), request);
//
//                        } else {
//
//                        }
//                        String[] extensions = new String[] { "png", "jpg", "jpeg", "pdf" };
//                        File allFiles = new File(tempDir);
//                        int successRecords = 0, failedRecords = 0;
//
//                        for (ExcelMandate m : mandates) {
//                            InputStream input = null;
//                            OutputStream os = null;
//                            try {
//                                @SuppressWarnings("unchecked")
//                                List<File> files = (List<File>) FileUtils.listFiles(allFiles, null, true);
//
//                                Optional<File> fileFilter = files.stream()
//                                        .filter(f -> f.getName().equals(m.getMandateFile())).findFirst();
//
//
//                                if (fileFilter.isPresent()) {
//                                    File newFile = fileFilter.get();
//                                    String originalFileName = newFile.getName();
//                                    String contentType = "application/octet-stream";
//                                    int size = (int) newFile.length();
//                                    System.out.println("Name ==== " + originalFileName);
//                                    System.out.println("Size ==== " + size);
//                                }
//                            }catch(Exception ex) {
//
//                            }
//                        }
//                    }
//                }else {
//                    logger.info("Multipart form is empty");
//                }
//            }catch(Exception ex) {
//                logger.error(null, ex);
//            }
//        }
//        return null;
//
//    }
//
//    protected List<ExcelMandate> processExcelMandateUpload(InputStream inputStream, String fileName,
//                                                      HttpServletRequest request) {
//        ExcelUtility excelReader = null;
//        List<ExcelMandate> mandates = new ArrayList<>();
//        List<String[]> data = null;
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//        if (fileName.endsWith(".xls")) {
//            excelReader = new ExcelUtility(ExcelUtility.ExcelType.XLS);
//        }else if (fileName.endsWith(".xlsx")) {
//            excelReader = new ExcelUtility(ExcelUtility.ExcelType.XLSX);
//        }
//
//        excelReader.setStartRow(1);
//        excelReader.setColumnCount(13);
//
//        try {
//            data = excelReader.readInputStream(inputStream);
//
//            //loop through the data
//            data.stream().forEach(d ->{
//                ExcelMandate m = new ExcelMandate();
//                m.setMandateCode(d[0]);
//                m.setSubscriberMandateCode(d[1]);
//                m.setAccountNumber(d[2].replaceAll("\\.0*$", ""));
//                m.setPayerName(d[3]);
//                m.setAccountName(d[4]);
//                m.setAmount(new BigDecimal(d[5] == "" ? "0" : d[5]));
//                m.setNarration(d[6]);
//                m.setPhoneNumber(d[7] != null ? d[7].replaceAll("\\.0*$", "") : "");
//                m.setEmail(d[8]);
//                m.setAddress(d[9]);
//                m.setFrequency(Integer.parseInt(d[10].replaceAll("\\.0*$", "")));
//                if (d[11].trim().equalsIgnoreCase("true")) {
//                    m.setMandateType(true);
//                } else {
//                    m.setMandateType(false);
//                }
//                m.setMandateFile(d[12]);
//
//                mandates.add(m);
//            });
//
//
//        }catch(Exception ex) {
//            logger.info("");
//        }
//        return mandates;
//    }
//
//    @GetMapping("/testCall")
//    public ResponseEntity testCall(){
//        return new ResponseEntity<Object>("Test Call done here",HttpStatus.OK);
//    }
//
//
//
//    public ResponseEntity<Object> processMandate(Long id, String action,
//                                                 HttpServletRequest request, User userOperator,RejectionRequests req) {
//        String role = userOperator.getRoles().stream().map(Role::getName).collect(Collectors.toList()).get(0);
//        String message = "";
//        Mandate mandate = null;
//        boolean flag = false;
//        try {
//            mandate = mandateService.getMandateByMandateId(id);
//            mandate.setLastActionBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
//
//            //This action can only be performed by Bank Initiator
//            if ("accept".equalsIgnoreCase(action)
//                    && mandate.getStatus().getId() == Constants.BILLER_AUTHORIZE_MANDATE
//                    && role.equalsIgnoreCase(RoleName.BANK_INITIATOR.getValue())) {
//
//                mandate.setStatus(mandateStatusService.getMandateStatusById(Constants.BANK_AUTHORIZE_MANDATE));
//                mandate.setAcceptedBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
//                mandate.setLastActionBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
//                mandate.setDateAccepted(new Date());
//                mandate.setDateModified(new Date());
//
//                mandateService.modifyMandate(mandate);
//                message = "Mandate <b>" + mandate.getMandateCode() + "</b> was successfully Approved!";
//                flag = true;
//                //send a mail after this action
//
//
//                //This action can only be performed by a bank authorizer
//            } else if ("approve".equalsIgnoreCase(action)
//                    && mandate.getStatus().getId() == Constants.BANK_AUTHORIZE_MANDATE &&
//                    role.equalsIgnoreCase(RoleName.BANK_AUTHORIZER.getValue())) {
//
//                mandate.setStatus(mandateStatusService.getMandateStatusById(Constants.BANK_APPROVE_MANDATE));
//                mandate.setApprovedBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
//                mandate.setDateApproved(new Date());
//                mandate.setLastActionBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
//
//                mandate.setDateModified(new Date());
//                message = "Mandate <b>" + mandate.getMandateCode() + "</b> was successfully Authorized!";
//
//                mandateService.modifyMandate(mandate);
//                flag = true;
//                //send a mail
//            } else if("reject".equalsIgnoreCase(action)){
//                mandate.setStatus(mandateStatusService.getMandateStatusById(Constants.BANK_REJECT_MANDATE));
//                mandate.setLastActionBy(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
//                mandate.setDateModified(new Date());
//
//                Rejection r = new Rejection();
//                r.setComment(req.getComment());
//                r.setDateRejected(new Date());
//                r.setUser(new User(userOperator.getId(), userOperator.getName(), userOperator.getEmailAddress(), userOperator.isActivated(), userOperator.getUserType()));
//                r.setRejectionReason(rejectionReasonsService.getOne(req.getRejectionId()));
//                mandate.setRejection(r);
//                message = "Mandate was successfully Rejected!";
//
//                mandateService.modifyMandate(mandate);
//
//                flag = true;
//            }
//            if(flag) {
//                return new ResponseEntity<Object>(new MandateResponse(message, mandate), HttpStatus.OK);
//            }
//
//        } catch (Exception e) {
//            logger.error("Exception thrown {}", e.getMessage());
//            return new ResponseEntity<Object>(new ErrorDetails(e.getMessage()), HttpStatus.BAD_REQUEST);
//        }
//
//        return new ResponseEntity<Object>(new ErrorDetails(Errors.INVALID_REQUEST.getValue()), HttpStatus.BAD_REQUEST);
//    }
//

}
