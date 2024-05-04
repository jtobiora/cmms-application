package ng.upperlink.nibss.cmms.service.mandateImpl;

import ng.upperlink.nibss.cmms.model.mandate.BulkMandate;
import ng.upperlink.nibss.cmms.repo.BulkMandateRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class BulkMandateService {

    @Autowired
    private BulkMandateRepo bulkMandateRepo;

    public List<BulkMandate> saveBulkMandate(List<BulkMandate> bulkMandate){
       return bulkMandateRepo.save(bulkMandate);
    }

    public BulkMandate save(BulkMandate mandate){
        return bulkMandateRepo.save(mandate);
    }

    public Long getMaxId(){
        return bulkMandateRepo.getMaxId();
    }

    public BulkMandate getAll(){
        return bulkMandateRepo.getAll();
    }

    public BulkMandate getByMandateId(Long id){
        return bulkMandateRepo.getByMandateId(id);
    }

    public BulkMandate getById(Long id){
        return bulkMandateRepo.getById(id);
    }


}
