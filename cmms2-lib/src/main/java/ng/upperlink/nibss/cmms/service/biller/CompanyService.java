package ng.upperlink.nibss.cmms.service.biller;


import java.util.List;

import ng.upperlink.nibss.cmms.model.biller.Company;
import ng.upperlink.nibss.cmms.repo.biller.CompanyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    private CompanyRepo companyRepo;
    private IndustryService industryService;

    @Autowired
    public void setCompanyRepo(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }

    @Autowired
    public void setIndustryService(IndustryService industryService) {
        this.industryService = industryService;
    }
    public Company getById(Long id){
        return companyRepo.getCompanyById(id);
    }
    public Page<Company> getAll(Pageable pageable){
        return companyRepo.findAll(pageable);
    }
    public List<Company> getAll(){
        return companyRepo.findAll();
    }
    public  Long countOfSameRCNumber(String rcNumber){
        return companyRepo.countOfSameRCNumber(rcNumber);
    }
    public  Long countOfSameRCNumber(String rcNumber,Long id){
        return companyRepo.countOfSameRCNumber(rcNumber,id);
    }
}