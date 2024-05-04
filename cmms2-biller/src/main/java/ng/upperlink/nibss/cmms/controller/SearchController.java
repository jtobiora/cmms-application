package ng.upperlink.nibss.cmms.controller;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.biller.FeeSearchRequest;
import ng.upperlink.nibss.cmms.dto.search.BeneficiarySearchRequest;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.repo.SearchRepo;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.biller.BeneficiaryService;
import ng.upperlink.nibss.cmms.service.biller.BillerUserService;
import ng.upperlink.nibss.cmms.service.biller.FeeService;
import ng.upperlink.nibss.cmms.service.biller.SharingFormularService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {
    private BillerUserService billerUserService;
    private BankService bankService;
    private BeneficiaryService beneficiaryService;
    private FeeService feeService;
    private SharingFormularService sharingFormularService;
    private SearchRepo searchRepo;

    @Autowired
    public void setSearchRepo(SearchRepo searchRepo){
        this.searchRepo = searchRepo;
    }

    @Autowired
    public void setBillerUserService(BillerUserService billerUserService) {
        this.billerUserService = billerUserService;
    }

    @Autowired
    public void setFeeService(FeeService feeService) {
        this.feeService = feeService;
    }

    @Autowired
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    @Autowired
    public void setBeneficiaryService(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    @Autowired
    public void setSharingFormularService(SharingFormularService sharingFormularService) {
        this.sharingFormularService = sharingFormularService;
    }

//    @PostMapping("/beneficiary")
//    public ResponseEntity<Object> searchBeneficiaries(@RequestBody BeneficiarySearchRequest request,
//            @RequestParam Optional<Integer> pageNumber, @RequestParam Optional<Integer> pageSize) {
//
//        boolean flag = StringUtils.isEmpty(request.getActivated()) ? true : false;
//
//        boolean status = Boolean.parseBoolean(request.getActivated());
//
//        try {
//            if(pageNumber.isPresent()&& pageSize.isPresent()) {
//                int pNum = pageNumber.get();
//                int pSize = pageSize.get();
//                return new ResponseEntity<Object>(beneficiaryService.searchBeneficiary(
//                        request.getBeneficiaryName(), request.getAccountNumber(), request.getAccountName(), flag, status, new PageRequest(pNum,pSize)), HttpStatus.OK);
//            }
//        } catch (Exception ex) {
//            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.INVALID_DATA_PROVIDED.getValue()));
//        }
//       return null;
//    }

//    @PostMapping("/fees")
//    public ResponseEntity searchFees(@Valid @RequestBody FeeSearchRequest request, Pageable pageable) {
//        if (StringUtils.isEmpty(request.getFixedAmount()) && StringUtils.isEmpty(request.getPercentageAmount())) {
//            if (StringUtils.isEmpty(request.getSplitType()) && StringUtils.isEmpty(request.getFeeBearer())) {
//                return ResponseEntity.ok(feeService.searchFees(request.getBillerAccNumber(), request.getBillerName(), pageable));
//            }
//            if (StringUtils.isEmpty(request.getSplitType()) && !(StringUtils.isEmpty(request.getFeeBearer()))) {
//                FeeBearer feeBearer = FeeBearer.find(request.getFeeBearer());
//                return ResponseEntity.ok(feeService.searchFees(request.getBillerAccNumber(), request.getBillerName(), feeBearer, pageable));
//            }
//
//            if (StringUtils.isEmpty(request.getFeeBearer()) && !(StringUtils.isEmpty(request.getSplitType()))) {
//                SplitType splitType = SplitType.find(request.getSplitType());
//                return ResponseEntity.ok(feeService.searchFees(request.getBillerAccNumber(), request.getBillerName(), splitType, pageable));
//            }
//
//            FeeBearer feeBearer = FeeBearer.find(request.getFeeBearer());
//            SplitType splitType = SplitType.find(request.getSplitType());
//            return ResponseEntity.ok(feeService.searchFees(request.getBillerAccNumber(), request.getBillerName(), splitType, feeBearer, pageable));
//        }
//
//        if (StringUtils.isEmpty(request.getPercentageAmount())) {
//            BigDecimal fixedAmt = null;
//            try {
//                fixedAmt = new BigDecimal(request.getFixedAmount());
//            } catch (NumberFormatException ex) {
//                return ResponseEntity.badRequest().body(new ErrorDetails("Fixed Amount must not contain characters!"));
//            }
//
//            if (StringUtils.isEmpty(request.getSplitType()) && StringUtils.isEmpty(request.getFeeBearer())) {
//                return ResponseEntity.ok(feeService.searchFees(request.getBillerAccNumber(), request.getBillerName(), fixedAmt, pageable));
//            }
//            if (StringUtils.isEmpty(request.getSplitType()) && !(StringUtils.isEmpty(request.getFeeBearer()))) {
//                FeeBearer feeBearer = FeeBearer.find(request.getFeeBearer());
//                return ResponseEntity.ok(feeService.searchFees(request.getBillerAccNumber(), request.getBillerName(), feeBearer, fixedAmt, pageable));
//            }
//            if (StringUtils.isEmpty(request.getFeeBearer()) && !(StringUtils.isEmpty(request.getSplitType()))) {
//                SplitType splitType = SplitType.find(request.getSplitType());
//                return ResponseEntity.ok(feeService.searchFees(request.getBillerAccNumber(), request.getBillerName(), splitType, fixedAmt, pageable));
//            }
//            FeeBearer feeBearer = FeeBearer.find(request.getFeeBearer());
//            SplitType splitType = SplitType.find(request.getSplitType());
//            return ResponseEntity.ok(feeService.searchFees(request.getBillerAccNumber(), request.getBillerName(), splitType, feeBearer, fixedAmt, pageable));
//        }
//
//        if (StringUtils.isEmpty(request.getFixedAmount())) {
//            BigDecimal percentageAmount = null;
//            try {
//                percentageAmount = new BigDecimal(request.getPercentageAmount());
//            } catch (NumberFormatException ex) {
//                return ResponseEntity.badRequest().body(new ErrorDetails("Percentage Amount must not contain characters!"));
//            }
//            if (StringUtils.isEmpty(request.getSplitType()) && StringUtils.isEmpty(request.getFeeBearer())) {
//                return ResponseEntity.ok(feeService.searchFees(request.getBillerAccNumber(), request.getBillerName(), percentageAmount, pageable));
//            }
//            if (StringUtils.isEmpty(request.getSplitType()) && !(StringUtils.isEmpty(request.getFeeBearer()))) {
//                FeeBearer feeBearer = FeeBearer.find(request.getFeeBearer());
//                return ResponseEntity.ok(feeService.searchFees(request.getBillerAccNumber(), request.getBillerName(), feeBearer, percentageAmount, pageable));
//            }
//            if (StringUtils.isEmpty(request.getFeeBearer()) && !(StringUtils.isEmpty(request.getSplitType()))) {
//                SplitType splitType = SplitType.find(request.getSplitType());
//                return ResponseEntity.ok(feeService.searchFees(request.getBillerAccNumber(), request.getBillerName(), splitType, percentageAmount, pageable));
//            }
//            FeeBearer feeBearer = FeeBearer.find(request.getFeeBearer());
//            SplitType splitType = SplitType.find(request.getSplitType());
//            return ResponseEntity.ok(feeService.searchFees(request.getBillerAccNumber(), request.getBillerName(), splitType, feeBearer, percentageAmount, pageable));
//        }
//        return null;
//    }

//    @PostMapping("/sharingformular")
//    public ResponseEntity searchSharingFormular(@Valid @RequestBody SharingFormularSearchReq request, Pageable pageable) {
//        Long billerId;
//        Long beneficiaryid;
//        BigDecimal fee = null;
//        try {
//            if (StringUtils.isEmpty(request.getFee())) {
//                if (StringUtils.isEmpty(request.getBillerId()) && StringUtils.isEmpty(request.getBeneficiaryId())) {
//                    return ResponseEntity.ok(new ArrayList<>());
//                }
//                if (StringUtils.isEmpty(request.getBillerId())) {
//                        beneficiaryid = Long.parseLong((request.getBeneficiaryId()));
//                        return ResponseEntity.ok(sharingFormularService.searchSharingFormular(pageable, beneficiaryid));
//                }
//                if (StringUtils.isEmpty(request.getBeneficiaryId())) {
//                        billerId = Long.parseLong((request.getBillerId()));
//                        return ResponseEntity.ok(sharingFormularService.searchSharingFormular(billerId, pageable));
//                }
//                    billerId = Long.parseLong(request.getBillerId());
//                    beneficiaryid = Long.parseLong((request.getBeneficiaryId()));
//                    return ResponseEntity.ok(sharingFormularService.searchSharingFormular(beneficiaryid, billerId, pageable));
//
//            } else {
//                if (StringUtils.isEmpty(request.getBillerId()) && StringUtils.isEmpty(request.getBeneficiaryId())) {
//                        fee = new BigDecimal(request.getFee());
//                        return ResponseEntity.ok(sharingFormularService.searchSharingFormular(pageable, fee));
//                }
//
//                if (StringUtils.isEmpty(request.getBillerId())) {
//                        fee = new BigDecimal(request.getFee());
//                        beneficiaryid = Long.parseLong((request.getBeneficiaryId()));
//                        return ResponseEntity.ok(sharingFormularService.searchSharingFormular(pageable, beneficiaryid, fee));
//                }
//                if (StringUtils.isEmpty(request.getBeneficiaryId())) {
//                        fee = new BigDecimal(request.getFee());
//                        billerId = Long.parseLong((request.getBillerId()));
//                        return ResponseEntity.ok(sharingFormularService.searchSharingFormular(billerId, pageable, fee));
//                }
//
//                    fee = new BigDecimal(request.getFee());
//                    billerId = Long.parseLong(request.getBillerId());
//                    beneficiaryid = Long.parseLong((request.getBeneficiaryId()));
//                    return ResponseEntity.ok(sharingFormularService.searchSharingFormular(beneficiaryid, billerId, pageable, fee));
//            }
//        }catch (NumberFormatException  | NullPointerException ex){
//            return ResponseEntity.badRequest().body(new ErrorDetails("Invalid input received!"));
//        }
//
//    }


    ///SEARCH using CRITERIA_BUILDER
    @PostMapping("/fees")
    public ResponseEntity searchFees(@RequestBody FeeSearchRequest request,
                                     @RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize){

          try {
              return searchRepo.searchFees(request.getMarkUpFee(),request.getFeeBearer(),request.getBillerAccNumber(),request.getBillerName(),
                      request.getDebitAtTrans(),request.getMarkUpFeeSelected(),request.getBeneficiaryBankCode(),new PageRequest(pageNumber,pageSize));

          }catch (Exception e){
              log.error("Could not complete search request",e);
              return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
          }
    }

    @PostMapping("/beneficiary")
    public ResponseEntity<Object> searchBeneficiaries(@RequestBody BeneficiarySearchRequest request,
                                                      @RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {

        return searchRepo.searchBeneficiary(
                        request.getBeneficiaryName(), request.getAccountNumber(), request.getAccountName(),request.getActivated(), new PageRequest(pageNumber,pageSize));

    }




}
