package ng.upperlink.nibss.cmms.repo;

import ng.upperlink.nibss.cmms.model.mandate.BulkMandate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BulkMandateRepo extends JpaRepository<BulkMandate,Long>{

    @Query("SELECT coalesce(max(m.id), 0) FROM BulkMandate m")
    Long getMaxId();

    @Query("select b from BulkMandate b")
    public BulkMandate getAll();

    @Query("select b from BulkMandate b where b.id = :id")
    public BulkMandate getById(@Param("id")Long id);

    @Query("select b from BulkMandate b where b.mandateId = :mandateId")
    public BulkMandate getByMandateId(@Param("mandateId")Long mandateId);

}
