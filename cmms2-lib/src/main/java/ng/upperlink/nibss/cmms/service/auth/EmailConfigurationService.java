package ng.upperlink.nibss.cmms.service.auth;

import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.model.auth.EmailConfiguration;
import ng.upperlink.nibss.cmms.repo.auth.EmailConfigurationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailConfigurationService {
    private EmailConfigurationRepo emailConfigurationRepo;

    @Autowired
    public void setEmailConfigurationRepo(EmailConfigurationRepo emailConfigurationRepo) {
        this.emailConfigurationRepo = emailConfigurationRepo;
    }

    public EmailConfiguration findById(Long id)
    {
        return emailConfigurationRepo.findOne(id);
    }
    public List<String> findByRoleAndBillerOwner(RoleName roleName,String ownerApiKey)
    {
        return emailConfigurationRepo.findByRoleAndBillerOwner(roleName,ownerApiKey);
    }
}
