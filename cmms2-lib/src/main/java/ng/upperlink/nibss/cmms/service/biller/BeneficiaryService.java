package ng.upperlink.nibss.cmms.service.biller;

import ng.upperlink.nibss.cmms.dto.biller.BeneficiaryRequest;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.mandate.Beneficiary;
import ng.upperlink.nibss.cmms.repo.biller.BeneficiaryRepo;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class BeneficiaryService {
    private BeneficiaryRepo beneficiaryRepo;
    private BankService bankService;

    @Autowired
    public void setBeneficiaryRepo(BeneficiaryRepo beneficiaryRepo){
        this.beneficiaryRepo = beneficiaryRepo;
    }


    @Autowired
    public void setBankService(BankService bankService){
        this.bankService = bankService;
    }

    public Beneficiary getOne(Long id){
        return beneficiaryRepo.findBeneficiaryById(id);
    }

    public List<Beneficiary> getAll(){
        return beneficiaryRepo.getAllBeneficiaries();
    }

    public Page<Beneficiary> getAllPaginated(Pageable pageable){
        return beneficiaryRepo.getAllBeneficiaries(pageable);
    }

    public Beneficiary generate(BeneficiaryRequest request,Beneficiary beneficiaryInDb,boolean isUpdate,Bank bank){
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setAccountName(request.getAccountName());
        beneficiary.setAccountNumber(request.getAccountNumber());
        beneficiary.setBank(bank);
        beneficiary.setBeneficiaryName(request.getBeneficiaryName());

        if(isUpdate){
            beneficiary.setId(beneficiaryInDb.getId());
        }else{
            beneficiary.setActivated(true);
        }

        return this.save(beneficiary);
    }

    public Beneficiary save(Beneficiary beneficiary){
        return beneficiaryRepo.save(beneficiary);
    }

    public List<Beneficiary> getBeneficiaries(List<Long> list){
        return beneficiaryRepo.getBeneficiaries(list);
    }

    public Beneficiary toggleBeneficiary(Long id){
        Beneficiary beneficiaryToBeToggled = this.getOne(id);
        if (beneficiaryToBeToggled == null) {
            return new Beneficiary();
        } else {
            boolean activated = beneficiaryToBeToggled.isActivated();
            beneficiaryToBeToggled.setActivated(!activated);
            return this.save(beneficiaryToBeToggled);
        }
    }

    public Page<Beneficiary> searchBeneficiary(String beneficiaryName,String accNumber,String accName,boolean flag,
                                               boolean activated,Pageable pageable){
        return flag ? beneficiaryRepo.searchBeneficiary(beneficiaryName,accNumber,accName,pageable) :
         beneficiaryRepo.searchBeneficiary(beneficiaryName,accNumber,accName,activated,pageable);
    }


}
