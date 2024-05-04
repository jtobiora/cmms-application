package ng.upperlink.nibss.cmms.repo.emandate;

import ng.upperlink.nibss.cmms.model.emandate.EmandatePasswordRecoveryCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface EmandatePasswordRecoveryRepo extends JpaRepository<EmandatePasswordRecoveryCode, Long> {
@Query("SELECT r FROM EmandatePasswordRecoveryCode r WHERE r.recoveryCode = ?1")
public EmandatePasswordRecoveryCode findRecoveryCode(String recoveryCode);

}
