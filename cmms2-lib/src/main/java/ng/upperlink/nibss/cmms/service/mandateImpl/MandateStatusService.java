package ng.upperlink.nibss.cmms.service.mandateImpl;

import ng.upperlink.nibss.cmms.dto.mandates.MandateStatusResponse;
import ng.upperlink.nibss.cmms.enums.MandateStatusType;
import ng.upperlink.nibss.cmms.model.mandate.MandateStatus;
import ng.upperlink.nibss.cmms.repo.MandateStatusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;

@Service
@Slf4j
public class MandateStatusService {
    private MandateStatusRepo mandateStatusRepo;

    @Autowired
    public void setMandateStatusRepo(MandateStatusRepo mandateStatusRepo){
        this.mandateStatusRepo = mandateStatusRepo;
    }



    public MandateStatus getMandateStatusById(Long id){
        return mandateStatusRepo.getMandateStatusById(id);
    }

    public MandateStatus getMandateStatusByStatusName(MandateStatusType mandateStatusType){
        return mandateStatusRepo.getMandateStatusByStatusName(mandateStatusType);
    }
    public List<MandateStatusResponse> getMandateStatuses(){
        return this.getIdAndDescription();
    }
    
    @Cacheable(value = "mandateStatusesByName", unless = "#result == null || #result.isEmpty()")
    public List<MandateStatus> getMandateStatusesByName(List<String> names) {
        try {
            return mandateStatusRepo.getMandateStatusesByName(names);
        } catch (Exception e) {
            log.error("An exception occurred while trying to fetch mandate by statuses", e);
        }
        return new ArrayList<>();
    }

    public static List<MandateStatusResponse> getIdAndDescription(){
        List<MandateStatusResponse> mResponseList = new ArrayList<>();
        for(MandateStatusType mStatus : MandateStatusType.values()){
            if(mStatus.getDescription().equalsIgnoreCase("none")){
                continue;
            }
            MandateStatusResponse res = new MandateStatusResponse();
            res.setId(mStatus.getId());
            res.setDescription(mStatus.getDescription());

            mResponseList.add(res);
        }

        return mResponseList;
    }

    public static List<MandateStatusResponse> getMandateStatuses(String userType){
        List<MandateStatusResponse> res = getIdAndDescription();

        switch(userType){
            case "bankUsers" :
                System.out.println("Size list");
                System.out.println(res.size());
                return  res.stream().filter(f -> f.getId() == 4L && f.getId() == 5L &&
                        f.getId() == 6L && f.getId() == 7L && f.getId() == 8L && f.getId() == 9L
                && f.getId() == 10L).collect(Collectors.toList());
            case "billerUsers":
                return res.stream().filter(f -> f.getId() == 1 && f.getId() == 2 &&
                        f.getId() == 3 && f.getId() == 4 && f.getId() == 5 && f.getId() == 8
                        && f.getId() == 9).collect(Collectors.toList());
        }
        return null;
    }
}
