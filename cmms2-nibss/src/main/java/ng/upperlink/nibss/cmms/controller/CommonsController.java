package ng.upperlink.nibss.cmms.controller;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.enums.Channel;
import ng.upperlink.nibss.cmms.enums.MandateCategory;
import ng.upperlink.nibss.cmms.enums.MandateFrequency;
import ng.upperlink.nibss.cmms.enums.MandateRequestType;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CommonsController {

    @GetMapping("/mandateCategories")
    public ResponseEntity<?> getAllMandateCategories() {
        try{
            return ResponseEntity.ok(MandateCategory.getMandateCategories());
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorDetails("Could not fetch mandate category!"), HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/mandateTypes")
    public ResponseEntity<?> getAllMandateTypes() {
        try{
            return ResponseEntity.ok(MandateRequestType.getMandateTypes());
        }catch (Exception e){
            return new ResponseEntity<>(new ErrorDetails("Could not fetch mandate Type!"), HttpStatus.NO_CONTENT);
        }

    }

    @GetMapping("/mandateChannels")
    public ResponseEntity<?> getAllMandateChannels() {
        try {
            return ResponseEntity.ok(Channel.getMandateChannels());
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorDetails("Could not fetch mandate channels!"), HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/mandateFrequencies")
    public ResponseEntity<?> getAllFrequencies() {
       try {
           return ResponseEntity.ok(MandateFrequency.getMandateFrequencies());
       }catch (Exception e){
           return new ResponseEntity<>(new ErrorDetails("Could not fetch mandate frequency!"), HttpStatus.NO_CONTENT);
       }
    }

//    @GetMapping(value = "/uploads/template")
//    public ResponseEntity<InputStreamResource> getReportTemplate(
//            @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) throws Exception {
//
//        User user = userService.get(userDetail.getUserId());
//
//        if (user == null) {
//            throw new CustomGenericException("User not found!");
//        }
//
//        Biller biller = null;
//
//        if (UserType.find(userDetail.getUserType()) == UserType.BILLER) {
//            biller = ((BillerUser)user).getBiller();
//            return excelParser.getGeneratedExcelFile(userDetail,biller);
//        }
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//
//    }

//    @PostMapping(value = "/upload",produces = {"application/json"},consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseEntity uploadBulkMandate(
//            @RequestPart("file") MultipartFile file,@RequestPart("token")String token,
//            HttpServletRequest request) throws Exception {
//
//        return null;
//    }

    //upload Mandate in bulk.
//    @PostMapping(value = "/uploads/bulk", headers = ("content-type=multipart/*"))
//    public ResponseEntity uploadBulkMandate(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail,
//                                             @RequestParam("file") MultipartFile file) throws Exception{
//
//        //Allow Billers to upload mandates
//        if (UserType.find(userDetail.getUserType()) == UserType.BILLER) {
//            User user = userService.get(userDetail.getUserId());
//            if (user == null){
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//            }
//
//            //get role
//            String role = user.getRoles().stream().map(r -> r.getName().getValue()).collect(Collectors.toList()).get(0);
//
//            //Only Biller_Initiators can upload mandates for billers
//            String[] permittedRoles = {RoleName.BILLER_INITIATOR.getValue()};
//
//            boolean found = Arrays.asList(permittedRoles).contains(role);
//
//            if (!found) {
//                throw new ExcelReaderException(Errors.NOT_PERMITTED.getValue());
//            }
//
//
//            Biller biller = null;
//            if(user instanceof BillerUser){
//                biller = ((BillerUser)user).getBiller();
//            }
//
//            return excelParser.bulkCreation(file,  user,biller,role);
//        }
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//    }


//    @PutMapping("/uploads/image")
//    public ResponseEntity uploadMandateImage(@Valid @RequestBody MandateActionRequest req,
//                                             HttpServletRequest servletRequest, @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){
//        try {
//           // Long maxId = mandateService.getMaxMandate();
//            BulkMandate bulkMandate = bulkMandateService.getByMandateId(req.getMandateId());
//
//            if(bulkMandate == null){
//                return new ResponseEntity<Object>(new ErrorDetails("Mandate not Found!"),HttpStatus.NOT_FOUND);
//            }
//
//            ObjectMapper mapper = new ObjectMapper();
//            Mandate mandate = mapper.readValue(bulkMandate.getMandateInJson(),Mandate.class);
//
//            String mandateCode = MandateUtils.getMandateCode(String.valueOf(System.currentTimeMillis()), mandate.getBiller().getRcNumber(),String.valueOf(mandate.getProduct().getId()));
//
//            mandateService.uploadMandateImage(null,servletRequest,mandateCode,userDetail,mandate,req.getAction());
//
//            //update the mandate id to reflect the one after the last mandate saved
//            mandate.setId(mandateService.getMaxMandate());
//
//            return ResponseEntity.ok(mandateService.saveMandate(mandate));
//        } catch(Exception ex){
//            log.error("Error in uploading mandate image! {}",ex.getMessage());
//            return new ResponseEntity<Object>(new ErrorDetails("Error uploading image!"),HttpStatus.BAD_REQUEST);
//        }
//
//    }
}


