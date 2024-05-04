package ng.upperlink.nibss.cmms.service.mandateImpl;

import ng.upperlink.nibss.cmms.model.Rejection;
import ng.upperlink.nibss.cmms.repo.RejectionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RejectionService {

    private RejectionRepo rejectionRepo;

    @Autowired
    public void setRejectionRepo(RejectionRepo rejectionRepo){
        this.rejectionRepo = rejectionRepo;
    }

    public Rejection save(Rejection rejection){
        return rejectionRepo.save(rejection);
    }
}
