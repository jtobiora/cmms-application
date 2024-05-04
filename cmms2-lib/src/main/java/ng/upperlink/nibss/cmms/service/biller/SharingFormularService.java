package ng.upperlink.nibss.cmms.service.biller;

import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.biller.SharingFormularRequest;
import ng.upperlink.nibss.cmms.dto.biller.SharingFormularResponse;
import ng.upperlink.nibss.cmms.dto.biller.SharingPattern;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.SplitType;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.mandate.Beneficiary;
import ng.upperlink.nibss.cmms.model.mandate.Fee;
import ng.upperlink.nibss.cmms.model.mandate.SharingFormular;
import ng.upperlink.nibss.cmms.repo.biller.SharingFormularRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class SharingFormularService {
    private FeeService feeService;
    private BillerService billerService;
    private BeneficiaryService beneficiaryService;

    @Autowired
    public void setBeneficiaryService(BeneficiaryService beneficiaryService){
        this.beneficiaryService = beneficiaryService;
    }

    @Autowired
    public void setFeeService(FeeService feeService){
        this.feeService = feeService;
    }

    @Autowired
    public void setBillerService(BillerService billerService){
        this.billerService = billerService;
    }

    private SharingFormularRepo sharingFormularRepo;

    @Autowired
    public void setSharingFormularRepo(SharingFormularRepo sharingFormularRepo){
        this.sharingFormularRepo = sharingFormularRepo;
    }


    public List<SharingFormular> saveSharingFormular(List<SharingFormular> sharingList){
        return sharingFormularRepo.save(sharingList);
    }

    public void save(SharingFormular sharingFormular){
        sharingFormularRepo.save(sharingFormular);
    }

    public List<SharingFormular> findSharingFormularByBillers(Long id){
        return sharingFormularRepo.getListOfSharingFormular(id);
    }

    public ResponseEntity processSharingFormular(SharingFormularRequest requestObj, BindingResult result){
        List<SharingFormular> listOfSharingFormular = new ArrayList<>();

        //obtain the sharing pattern
        List<SharingPattern> sharingPatternList = requestObj.getFormular();

        Long billerId = requestObj.getBillerId();

        //get the biller
        Biller biller = billerId != null ? billerService.getBillerById(billerId) : null;

        //verify if a sharing formular has been created for this biller
        List<SharingFormular> sharingListInDb = this.findSharingFormularByBillers(billerId);

        if(!sharingListInDb.isEmpty()){
            return ResponseEntity.badRequest().body(new ErrorDetails("A sharing formular has been set up for this biller!"));
        }

        //get the fee using the biller id
        Fee fee = biller != null ? feeService.getFeeConfigByBillerId(billerId) : null;

        if(fee == null){
            return ResponseEntity.badRequest().body(new ErrorDetails("Fee set up has not been done for this biller!"));
        }

            if(fee.getSplitType() == SplitType.PERCENTAGE){
                //the total sum of all the fees given to beneficiaries must not be more than 100%
                BigDecimal totalSharedPercentageAmount = sharingPatternList.stream()
                        .map(SharingPattern::getFee)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal percentageAmount =  fee.getPercentageAmount();
                if (totalSharedPercentageAmount.compareTo(fee.getPercentageAmount()) == 1) {
                    return ResponseEntity.badRequest().body(new ErrorDetails("Total fee in % cannot be more than the configured fee!"));
                }
            }else if(fee.getSplitType() == SplitType.FIXED){
                //get the total fixed amount
                BigDecimal totalSharedAmount= sharingPatternList.stream()
                        .map(SharingPattern::getFee)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if(totalSharedAmount.compareTo(fee.getMarkUpFee()) == 1){
                    return ResponseEntity.badRequest().body(new ErrorDetails("Total fee cannot be greater than the configured fee!"));
                }
            }


        for(SharingPattern req : sharingPatternList) {
            if(result.hasErrors()) {
                return ResponseEntity.badRequest().body(new ErrorDetails(Errors.INVALID_REQUEST.getValue()));
            }
            SharingFormular sharingFormular = new SharingFormular();
            sharingFormular.setBeneficiaryId(req.getBeneficiaryId());
            sharingFormular.setFee(req.getFee());
            sharingFormular.setBillerId(billerId);
            listOfSharingFormular.add(sharingFormular);
        }

        //check if one beneficiary is assigned more than one fee by the biller
        Set<Long> bidSet = listOfSharingFormular.stream().map(SharingFormular::getBeneficiaryId).collect(Collectors.toSet());

        if(bidSet.size() < listOfSharingFormular.size()){
            return ResponseEntity.badRequest().body(new ErrorDetails("A beneficiary cannot be added to more than one sharing fee!"));
        }

        List<SharingFormular> list = this.saveSharingFormular(listOfSharingFormular);


//                //get all the beneficiary ids
        List<Long> ids  = list.stream().map(SharingFormular::getBeneficiaryId).collect(toList());

        List<Beneficiary> beneficiaries = beneficiaryService.getBeneficiaries(ids);

        Map<Long,Long> formularMap = list.stream().collect(Collectors.toMap(SharingFormular::getBeneficiaryId,SharingFormular::getId));

        Map<Long,Beneficiary> beneficiaryMap = new HashMap<>();

        for(Beneficiary b : beneficiaries){
            beneficiaryMap.put(b.getId(),b);
        }

        //return a response
        List<SharingFormularResponse> responseList = new ArrayList<>();
        sharingPatternList.forEach(req -> {
            SharingFormularResponse response= new SharingFormularResponse();
            response.setId(formularMap.get(req.getBeneficiaryId()));
            response.setBiller(biller);
            response.setBeneficiary(beneficiaryMap.get(req.getBeneficiaryId()));
            response.setFee(req.getFee());
            responseList.add(response);
        });

        return new ResponseEntity(responseList, HttpStatus.OK);

    }

    public ResponseEntity updateSharingFormular(SharingFormularRequest requestObj,
                                                BindingResult result,UserDetail userDetail){
        List<SharingFormular> listOfSharingFormular = new ArrayList();

        List<SharingPattern> sharingPatternList = requestObj.getFormular();

        Long billerId = requestObj.getBillerId();

        Biller biller = billerId != null ? this.billerService.getBillerById(billerId) : null;

        if (biller == null) {
            return ResponseEntity.badRequest().body(new ErrorDetails("Biller not found!"));
        }

        //get the sharing formular in the db using the biller id
        List<SharingFormular> sharingListInDb = this.findSharingFormularByBillers(billerId);

        if (sharingListInDb == null) {
            return ResponseEntity.badRequest().body(new ErrorDetails(String.format("Sharing formular not found for %s", biller.getName())));
        }

        //get the fee using the biller id
        Fee fee = biller != null ? feeService.getFeeConfigByBillerId(billerId) : null;

        if (fee == null) {
            return ResponseEntity.badRequest().body(new ErrorDetails(String.format("Fee not yet configured for %s", biller.getName())));
        }

        if (fee.getSplitType() == SplitType.PERCENTAGE) {
            //the total sum of all the fees given to beneficiaries must not be more than the one configured
            BigDecimal totalSharedPercentageAmount = sharingPatternList.stream()
                    .map(SharingPattern::getFee)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (totalSharedPercentageAmount.compareTo(fee.getPercentageAmount()) == 1) {
                return ResponseEntity.badRequest().body(new ErrorDetails("Total fee in % cannot be more than the configured fee!"));
            }
        } else if (fee.getSplitType() == SplitType.FIXED) {
            //get the total fixed amount
            BigDecimal totalSharedFixedAmount = sharingPatternList.stream()
                    .map(SharingPattern::getFee)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalSharedFixedAmount.compareTo(fee.getMarkUpFee()) == 1) {
                return ResponseEntity.badRequest().body(new ErrorDetails("Total fee cannot be greater than the configured fee!"));
            }
        }


        for (SharingPattern req : sharingPatternList) {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(new ErrorDetails(Errors.INVALID_REQUEST.getValue()));
            }
            SharingFormular sharingFormular = new SharingFormular();
            sharingFormular.setBeneficiaryId(req.getBeneficiaryId());
            sharingFormular.setFee(req.getFee());
            sharingFormular.setBillerId(billerId);

            listOfSharingFormular.add(sharingFormular);
        }


        //check if one beneficiary is assigned more than one fee per the biller
        Set<Long> bidSet = listOfSharingFormular.stream().map(SharingFormular::getBeneficiaryId).collect(Collectors.toSet());

        if (bidSet.size() < listOfSharingFormular.size()) {
            return ResponseEntity.badRequest().body(new ErrorDetails("A beneficiary cannot be added to more than one sharing fee!"));
        }

        //delete the existing sharing formular and then save another
        this.delete(requestObj.getBillerId());

        List<SharingFormular> list = this.saveSharingFormular(listOfSharingFormular);

        ////                //get all the beneficiary ids
        List<Long> ids  = list.stream().map(SharingFormular::getBeneficiaryId).collect(toList());

        List<Beneficiary> beneficiaries = beneficiaryService.getBeneficiaries(ids);

        Map<Long,Long> formularMap = list.stream().collect(Collectors.toMap(SharingFormular::getBeneficiaryId,SharingFormular::getId));

        Map<Long,Beneficiary> beneficiaryMap = new HashMap<>();

        for(Beneficiary b : beneficiaries){
            beneficiaryMap.put(b.getId(),b);
        }

        //return a response
        List<SharingFormularResponse> responseList = new ArrayList<>();
        sharingPatternList.forEach(req -> {
            SharingFormularResponse response= new SharingFormularResponse();
            response.setId(formularMap.get(req.getBeneficiaryId()));
            response.setBiller(biller);
            response.setBeneficiary(beneficiaryMap.get(req.getBeneficiaryId()));
            response.setFee(req.getFee());
            responseList.add(response);
        });

        return new ResponseEntity(responseList,HttpStatus.OK);

    }

    public void delete(Long billerId){
        sharingFormularRepo.deleteInBulk(billerId);
    }

    public Page<SharingFormular> searchSharingFormular(Long beneficiaryId,Long billerId, Pageable pageable){
        return sharingFormularRepo.searchSharingFormular(beneficiaryId,billerId,pageable);
    }

    public Page<SharingFormular> searchSharingFormular(Long billerId, Pageable pageable){
        return sharingFormularRepo.searchSharingFormular(billerId,pageable);
    }

    public Page<SharingFormular> searchSharingFormular( Pageable pageable,Long beneficiaryId){
        return sharingFormularRepo.searchSharingFormular(pageable,beneficiaryId);
    }


    //
    public Page<SharingFormular> searchSharingFormular(Long beneficiaryId,Long billerId, Pageable pageable,BigDecimal fee){
        return sharingFormularRepo.searchSharingFormular(beneficiaryId,billerId,pageable,fee);
    }

    public Page<SharingFormular> searchSharingFormular(Long billerId, Pageable pageable,BigDecimal fee){
        return sharingFormularRepo.searchSharingFormular(billerId,pageable,fee);
    }

    public Page<SharingFormular> searchSharingFormular( Pageable pageable,Long beneficiaryId,BigDecimal fee){
        return sharingFormularRepo.searchSharingFormular(pageable,beneficiaryId,fee);
    }

    public Page<SharingFormular> searchSharingFormular( Pageable pageable,BigDecimal fee){
        return sharingFormularRepo.searchSharingFormular(pageable,fee);
    }

}
