package ng.upperlink.nibss.cmms.controller;


import com.fasterxml.jackson.annotation.JsonView;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import ng.upperlink.nibss.cmms.dashboard.*;
import ng.upperlink.nibss.cmms.dto.*;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dashboard.BillerDashboard;
import ng.upperlink.nibss.cmms.dashboard.MandateDashboard;
import ng.upperlink.nibss.cmms.dashboard.UsersDashboard;

import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.UserType;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.NibssUser;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.bank.BankUser;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.biller.BillerUser;
import ng.upperlink.nibss.cmms.model.mandate.Mandate;
import ng.upperlink.nibss.cmms.model.mandate.MandateStatus;
import ng.upperlink.nibss.cmms.model.pssp.Pssp;
import ng.upperlink.nibss.cmms.model.pssp.PsspUser;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.biller.BillerService;
import ng.upperlink.nibss.cmms.service.mandateImpl.MandateService;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private BillerService billerService;

    private MandateService mandateService;

    @Autowired
    private BankService bankService;

    //@Lazy
    @Autowired
    public void setMandateService(MandateService mandateService){
        this.mandateService = mandateService;
    }

    private static final UserType NIBSS = UserType.NIBSS;
    private static final UserType BANK = UserType.BANK;
    private static final UserType BILLER = UserType.BILLER;
    private static final UserType PSSP = UserType.PSSP;
    private Long nibssActive,nibssInActive,bankActive,bankInactive,billerActive,billerInactive,psspActive,psspInactive;

    /*
    *    NIBSS DASHBOARD
    */
    @GetMapping("/users")
    public ResponseEntity getUsers(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){
        //get the user who logged in
        User userOperator = userService.get(userDetail.getUserId());
        if (userOperator == null) {
            return ResponseEntity.ok().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
        }

        if(userOperator instanceof NibssUser){
            List<User> userList = userService.getAllUsers();

            Map<UserType, Long> userMap = userList.stream().collect(groupingBy(User::getUserType, counting()));
            Long nibssUsers = userMap.get(UserType.NIBSS) == null ? 0L : userMap.get(UserType.NIBSS);
            Long bankUsers = userMap.get(UserType.BANK) == null ? 0L : userMap.get(UserType.BANK);
            Long billerUsers = userMap.get(UserType.BILLER) == null ? 0L : userMap.get(UserType.BILLER);
            Long psspUsers = userMap.get(UserType.PSSP) == null ? 0L : userMap.get(UserType.PSSP);

            return ResponseEntity.ok(new UsersDashboard(nibssUsers,
                    bankUsers,billerUsers,psspUsers));
        }

      return ResponseEntity.ok(new ErrorDetails(String.format("This view is not available for %s users.",userOperator.getUserType().getValue())));
    }

    @GetMapping("/users/activeStatus")
    public ResponseEntity getUsersByStatus(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){
        User userOperator = userService.get(userDetail.getUserId());
        if (userOperator == null) {
            return ResponseEntity.ok().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
        }

        if(userOperator instanceof NibssUser){
            List<User> userList = userService.getAllUsers();

            Map<UserType, Map<Boolean, List<User>>> userMap = userList.stream()
                    .collect(groupingBy(User::getUserType, groupingBy(User::isActivated)));


            if(userMap == null || userMap.isEmpty()){
                return ResponseEntity.ok(new ErrorDetails("No details found"));
            }

            for(UserType key : userMap.keySet()) {
                switch (key) {
                    case NIBSS:
                        List<Long> listNibss = userService.setStatusCount(userMap,UserType.NIBSS);
                        nibssActive = listNibss.get(0);
                        nibssInActive = listNibss.get(1);
                        break;
                    case BANK:
                        List<Long> listBank = userService.setStatusCount(userMap,UserType.BANK);
                        bankActive = listBank.get(0);
                        bankInactive = listBank.get(1);
                        break;
                    case BILLER:
                        List<Long> listBiller = userService.setStatusCount(userMap,UserType.BILLER);
                        billerActive = listBiller.get(0);
                        billerInactive = listBiller.get(1);
                        break;
                    case PSSP:
                        List<Long> listPssp = userService.setStatusCount(userMap,UserType.PSSP);
                        psspActive = listPssp.get(0);
                        psspInactive = listPssp.get(1);
                        break;
                    default:
                }
            }

            return ResponseEntity.ok(new UserStatus(
                    new Status(nibssActive,nibssInActive),
                    new Status(bankActive,bankInactive),
                    new Status(billerActive,billerInactive),
                    new Status(psspActive,psspInactive)));

        }

        return ResponseEntity.ok(new ErrorDetails(String.format("This view is not available for %s users.",userOperator.getUserType().getValue())));
    }

    @GetMapping("/biller")
    public ResponseEntity loadBillers(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){
        User userOperator = userService.get(userDetail.getUserId());
        if (userOperator == null) {
            return ResponseEntity.ok().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
        }

        if(userOperator instanceof NibssUser){
            List<Biller> billerList = billerService.getAllBillers();

            Map<Boolean, Long> userMap = billerList.stream().collect(groupingBy(Biller::isActivated, counting()));

            Long activeBillers = userMap.containsKey(true) ? userMap.get(true) : 0L;
            Long inActiveBillers = userMap.containsKey(false) ? userMap.get(false) : 0L;
            Long totalBillers = activeBillers + inActiveBillers;

            return ResponseEntity.ok(new BillerDashboard(totalBillers,activeBillers,inActiveBillers));
        }

        return ResponseEntity.ok(new ErrorDetails(String.format("This view is not available for %s users.",userOperator.getUserType().getValue())));

    }

    @JsonView(MandateView.Summary.class)
    @GetMapping("/mandates/nibss")
    public ResponseEntity loadMandatesForNIBSS(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){
       try{
           User userOperator = userService.get(userDetail.getUserId());
           if (userOperator == null) {
               return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
           }

           if(userOperator instanceof NibssUser){

               List<Date> date = userService.computeTimeSpan();
               List<Mandate> mandateList = mandateService.getMandatesYearToDate(date.get(0),date.get(1));
               return this.pullMandateResponse(mandateList,date);
           }

           return ResponseEntity.ok(new ErrorDetails(String.format("This view is not available for %s users.",userOperator.getUserType().getValue())));
       }catch(ParseException ex){
           log.error("Date parsing error reported {} ", ex.getMessage());
       }

       return null;
    }

    @JsonView(MandateView.BankDetail.class)
    @GetMapping("/mandates/nibss/banks")
    public ResponseEntity loadMandatesForNIBSSByBanks(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){
        try{
            User userOperator = userService.get(userDetail.getUserId());
            if (userOperator == null) {
                return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
            }

            if(userOperator instanceof NibssUser){
                List<Date> date = userService.computeTimeSpan();

                List<Mandate> mandateList = mandateService.getMandatesYearToDate(date.get(0),date.get(1));

                List<Bank> bankList = mandateList.stream().map(m -> m.getProduct().getBiller().getBank()).collect(Collectors.toList());

                if(bankList == null){
                    return new ResponseEntity(new ErrorDetails(Errors.UNKNOWN_USER.getValue()), HttpStatus.OK);
                }

                MandateReport report = new MandateReport();
                List<MandateDashboard> boardList = new ArrayList<>();
                for(Bank bank : bankList){
                    List<Mandate> bankMandate = mandateList.stream().filter(m -> m.getBiller().getBank() == bank).collect(Collectors.toList());
                    Long total = bankMandate.stream().count();
                    Long approved = bankMandate.stream().filter(m -> m.getStatus().getId() == Constants.BANK_APPROVE_MANDATE).count();
                    Long pending = bankMandate.stream().filter(m -> m.getStatus().getId() != Constants.BANK_APPROVE_MANDATE).count();
                    Long rejected = bankMandate.stream().filter(m -> m.getStatus().getName().contains("rejected")).count();

                    MandateDashboard board = new MandateDashboard(DateFormatUtils.format(date.get(0), "yyyy-MMM-dd"),DateFormatUtils.format(date.get(1), "yyyy-MMM-dd"),
                            bank.getName(),total,approved,pending,rejected);

                    boardList.add(board);
                }
                report.setMandateReport(boardList);
                return ResponseEntity.ok(report);
            }
            return ResponseEntity.ok(new ErrorDetails(String.format("This view is not available for %s users.",userOperator.getUserType().getValue())));
        }catch(ParseException ex){
            log.error("could not parse dates entered {} ",ex.getMessage());
            ex.printStackTrace();
        }

        return null;
    }

     /*
     *
     *  BANK USER DASHBOARD
     */
     @JsonView(MandateView.Summary.class)
     @GetMapping("/mandates/bank")
     public ResponseEntity loadMandatesForBanks(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
         try {
             //get the user who logged in
             User userOperator = userService.get(userDetail.getUserId());
             if (userOperator == null) {
                 return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
             }

             if (userOperator instanceof BankUser) {
                 Bank bank = ((BankUser) userOperator).getUserBank();
                 if (bank == null) {
                     return ResponseEntity.ok().body(new ErrorDetails("Bank is unknown!"));
                 }

                 List<Date> date = userService.computeTimeSpan();

                 List<Mandate> mandateList = mandateService.getBankUserMandatesYTD(date.get(0), date.get(1), bank.getApiKey(),bank.getId());

                 Long total = mandateList.stream().count();

                 Long approved = mandateList.stream().filter(m -> m.getStatus().getId() == Constants.BANK_APPROVE_MANDATE).count();
                 Long pending = mandateList.stream().filter(m -> m.getStatus().getId() != Constants.BANK_APPROVE_MANDATE).count();
                 Long rejected = mandateList.stream().filter(m -> m.getStatus().getName().contains("rejected")).count();

                 return ResponseEntity.ok(new MandateDashboard(DateFormatUtils.format(date.get(0), "yyyy-MMM-dd"), DateFormatUtils.format(date.get(1), "yyyy-MMM-dd"), total, approved, pending, rejected));
             }
             return ResponseEntity.ok(new ErrorDetails(String.format("This view is not available for %s users.", userOperator.getUserType().getValue())));
         } catch (Exception ex) {
             ex.printStackTrace();
         }

         return null;
     }

    @JsonView(MandateView.BillerDetail.class)
    @GetMapping("/mandates/bank/billers")
    public ResponseEntity loadMandatesForBankBillers(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){
        try{
            //get the user who logged in
            User userOperator = userService.get(userDetail.getUserId());
            if (userOperator == null) {
                return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
            }

            if(userOperator instanceof BankUser){
                Bank bank = ((BankUser) userOperator).getUserBank();
                if (bank == null) {
                    return ResponseEntity.ok().body(new ErrorDetails("Bank is unknown!"));
                }


                List<Date> date = userService.computeTimeSpan();
                List<Mandate> mandateList = mandateService.getBankUserMandatesYTD(date.get(0),date.get(1),bank.getApiKey(),bank.getId());

                Set<Biller> billerList = mandateList.stream().map(m -> m.getBiller()).collect(Collectors.toSet());

                MandateReport report = new MandateReport();
                List<MandateDashboard> boardList = new ArrayList<>();

                for(Biller biller : billerList){

                    List<Mandate> billerMandate = mandateList.stream().filter(m -> m.getBiller() == biller).collect(Collectors.toList());

                    Long total = billerMandate.stream().count();
                    Long approved = billerMandate.stream().filter(m -> m.getStatus().getId() == Constants.BANK_APPROVE_MANDATE).count();
                    Long pending = billerMandate.stream().filter(m -> m.getStatus().getId() != Constants.BANK_APPROVE_MANDATE).count();
                    Long rejected = billerMandate.stream().filter(m -> m.getStatus().getName().contains("rejected")).count();

                    MandateDashboard board = new MandateDashboard(DateFormatUtils.format(date.get(0), "yyyy-MMM-dd"),
                            DateFormatUtils.format(date.get(1), "yyyy-MMM-dd"),
                            bank.getName(),biller.getName(),total,approved,pending,rejected);

                    boardList.add(board);
                }
                report.setMandateReport(boardList);

                return ResponseEntity.ok(report);

            }
            return ResponseEntity.ok(new ErrorDetails(String.format("This view is not available for %s users.",userOperator.getUserType().getValue())));
        }catch(ParseException ex){
            return ResponseEntity.ok("Error page");
        }
    }

    @GetMapping("/bank/billers")
    public ResponseEntity loadBankBillers(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){
        //get the user who logged in
        User userOperator = userService.get(userDetail.getUserId());
        if (userOperator == null) {
            return ResponseEntity.ok().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
        }

        Bank bank = ((BankUser) userOperator).getUserBank();
        if (bank == null) {
            return ResponseEntity.ok().body(new ErrorDetails("Bank is unknown!"));
        }

        List<Biller> billerList = billerService.getAllBillers().stream().filter(f -> f.getBank() == bank).collect(Collectors.toList());

        return ResponseEntity.ok(new BillerDashboard(billerList.stream().count(),billerList.stream().filter(b -> b.isActivated()).count(),
                billerList.stream().filter(b -> !b.isActivated()).count()));
    }


    /*
    *
    *  BILLER USER DASHBOARD
    */
    @GetMapping("/mandates/biller")
    public ResponseEntity loadMandatesForBillers(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){
        try{
            //get the user who logged in
            User userOperator = userService.get(userDetail.getUserId());
            if (userOperator == null) {
                return ResponseEntity.ok().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
            }

            if(userOperator instanceof BillerUser){
                Biller biller = ((BillerUser) userOperator).getBiller();

                if (biller == null) {
                    return ResponseEntity.ok().body(new ErrorDetails("Biller is not known!"));
                }

                List<Date> date = userService.computeTimeSpan();

                List<Mandate> mandateList = mandateService.getBillerUserMandatesYTD(date.get(0),date.get(1),biller.getApiKey());

                Long total = mandateList.stream().count();
                Long approved = mandateList.stream().filter(m -> m.getStatus().getId() == Constants.BILLER_AUTHORIZE_MANDATE).count();
                Long pending = mandateList.stream().filter(m -> m.getStatus().getId() == Constants.BILLER_INITIATE_MANDATE).count();
                Long rejected = mandateList.stream().filter(m -> m.getStatus().getId() == Constants.BILLER_REJECT_MANDATE).count();

                return ResponseEntity.ok(new MandateDashboard(DateFormatUtils.format(date.get(0), "yyyy-MMM-dd"),DateFormatUtils.format(date.get(1), "yyyy-MMM-dd"),total,approved,pending,rejected));

            }
            return ResponseEntity.ok(new ErrorDetails(String.format("This view is not available for %s users.",userOperator.getUserType().getValue())));
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    @JsonView(MandateView.SubscriberDetail.class)
    @GetMapping("/mandates/biller/subscribers")
    public ResponseEntity loadMandatesForBillerSubscriber(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        try {
            //get the user who logged in
            User userOperator = userService.get(userDetail.getUserId());
            if (userOperator == null) {
                return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
            }

            Biller biller = ((BillerUser) userOperator).getBiller();

            if (biller == null) {
                return ResponseEntity.badRequest().body(new ErrorDetails("Biller is not known!"));
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            List<Date> date = userService.computeTimeSpan();

            List<Mandate> mandateList = mandateService.getBillerUserMandatesYTD(date.get(0), date.get(1), biller.getApiKey());

            //get a list of all subscribers
            Set<String> subscribers = mandateList.stream().map(Mandate::getEmail).collect(Collectors.toSet());

            MandateReport report = new MandateReport();
            List<MandateDashboard> boardList = new ArrayList<>();

            for (String subscriberEmail : subscribers) {

                MandateDashboard board = new MandateDashboard();
                List<Mandate> list = mandateList.stream().filter(f -> f.getEmail().equalsIgnoreCase(subscriberEmail)).collect(Collectors.toList());

                Long total = list.stream().count();
                Long approved = list.stream().filter(m -> m.getStatus().getId() == Constants.BILLER_AUTHORIZE_MANDATE).count();
                Long pending = list.stream().filter(m -> m.getStatus().getId() == Constants.BILLER_INITIATE_MANDATE).count();
                Long rejected = list.stream().filter(m -> m.getStatus().getId() == Constants.BILLER_REJECT_MANDATE).count();

                board.setSubscriber(subscriberEmail);
                board.setApprovedMandates(approved);
                board.setPendingMandates(pending);
                board.setRejectedMandates(rejected);
                board.setTotalMandates(total);
                board.setStartDate(DateFormatUtils.format(date.get(0), "yyyy-MMM-dd"));
                board.setEndDate(DateFormatUtils.format(date.get(1), "yyyy-MMM-dd"));

                boardList.add(board);
            }
            report.setMandateReport(boardList);

            return ResponseEntity.ok(report);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

   /*
   *
   *  PSSP USER DASHBOARD
   */

    @GetMapping("/pssp/billers")
    public ResponseEntity loadPSSPBillers(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
//        //get the user who logged in
       try{
           User userOperator = userService.get(userDetail.getUserId());
           if (userOperator == null) {
               return ResponseEntity.badRequest().body(new ErrorDetails(Errors.UNKNOWN_USER.getValue()));
           }

           if(userOperator instanceof PsspUser){
               Pssp pssp = ((PsspUser) userOperator).getPssp();
               if (pssp == null) {
                   return ResponseEntity.badRequest().body(new ErrorDetails("PSSP is unknown!"));
               }

            List<Biller> billerList = null;
            try {
                billerList = billerService.getAllByBillerOwner(true,userDetail);
            } catch (CMMSException e) {
                e.printStackTrace();
                return ErrorDetails.setUpErrors("Unable to retrieve", Arrays.asList(e.getMessage()),e.getCode());
            }


               Long totalBillers = billerList.stream().count();
               Long activeBillers = billerList.stream().filter(b -> b.isActivated() == true).count();
               Long inActiveBillers = billerList.stream().filter(b -> b.isActivated() == false).count();

               return ResponseEntity.ok(new BillerDashboard(totalBillers,activeBillers,inActiveBillers));
           }

           return ResponseEntity.ok(new ErrorDetails(String.format("This view is not available for %s users.",userOperator.getUserType().getValue())));

       }catch(Exception e){
           log.error("Biller search was not successful. ",e);
           return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.OK);
       }
    }

    public ResponseEntity pullMandateResponse(List<Mandate> mandateList, List<Date> date){
        Long total = mandateList.stream().count();
        Long approved = mandateList.stream().filter(m -> m.getStatus().getId() == Constants.BANK_APPROVE_MANDATE).count();
        Long pending = mandateList.stream().filter(m -> m.getStatus().getId() != Constants.BANK_APPROVE_MANDATE).count();
        Long rejected = mandateList.stream().filter(m -> m.getStatus().getName().contains("rejected")).count();
        return ResponseEntity.ok(new MandateDashboard(DateFormatUtils.format(date.get(0), "yyyy-MMM-dd"),DateFormatUtils.format(date.get(1), "yyyy-MMM-dd"),total,approved,pending,rejected));

    }

}
