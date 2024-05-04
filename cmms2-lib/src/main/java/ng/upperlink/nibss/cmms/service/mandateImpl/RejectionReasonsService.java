package ng.upperlink.nibss.cmms.service.mandateImpl;

import ng.upperlink.nibss.cmms.model.RejectionReason;
import ng.upperlink.nibss.cmms.repo.RejectionReasonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RejectionReasonsService {
    private RejectionReasonRepo rejectionReasonRepo;

    @Autowired
    public void setRejectionReasonRepo(RejectionReasonRepo rejectionReasonRepo){
        this.rejectionReasonRepo = rejectionReasonRepo;
    }

    public List<RejectionReason> getAll(){
        return rejectionReasonRepo.getAllRejectionReasons();
    }

    public RejectionReason getOne(Long id){
        return rejectionReasonRepo.getRejectionReasonsById(id);
    }
}
