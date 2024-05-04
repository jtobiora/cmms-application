package ng.upperlink.nibss.cmms.repo;

import ng.upperlink.nibss.cmms.enums.MandateStatusType;
import ng.upperlink.nibss.cmms.model.mandate.MandateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface MandateStatusRepo extends JpaRepository<MandateStatus, Long> {

    @Query("select s from MandateStatus s where s.id =:id")
    MandateStatus getMandateStatusById(@Param("id")Long id);

    @Query("select s from MandateStatus s where s.id in (:statuses)")
    List<MandateStatus> getMandateStatuses(@Param("statuses")Long[] statuses);
    
    @Query("SELECT s FROM MandateStatus s WHERE s.name IN ?1")
    List<MandateStatus> getMandateStatusesByName(List<String> names); // get mandate statuses by name list

    @Query("SELECT s FROM MandateStatus s WHERE s.statusName =:mandateStatus")
    MandateStatus getMandateStatusByStatusName(@Param("mandateStatus") MandateStatusType mandateStatusType); // get mandate statuses by name

    @Query("select s from MandateStatus s")
    List<MandateStatus> getAllMandatesStatuses();
}
