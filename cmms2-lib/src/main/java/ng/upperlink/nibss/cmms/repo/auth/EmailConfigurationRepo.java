package ng.upperlink.nibss.cmms.repo.auth;

import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.model.auth.EmailConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface EmailConfigurationRepo extends JpaRepository<EmailConfiguration,Long> {
    @Query(value = "select e from EmailConfiguration e where e.roleName =:roleName and e.ownerApiKey =:ownerApiKey",nativeQuery = true)
    List<String> findByRoleAndBillerOwner(@Param("roleName")RoleName roleName,@Param("ownerApiKey")String ownerApiKey);
}
