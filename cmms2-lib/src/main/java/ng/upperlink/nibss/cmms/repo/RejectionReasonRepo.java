package ng.upperlink.nibss.cmms.repo;

import ng.upperlink.nibss.cmms.model.RejectionReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface RejectionReasonRepo extends JpaRepository<RejectionReason,Long> {

    @Query("select r from RejectionReason r")
    List<RejectionReason> getAllRejectionReasons();

    @Query("select r from RejectionReason r where r.id = :id")
    RejectionReason getRejectionReasonsById(@Param("id")Long id);
}
