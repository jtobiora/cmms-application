package ng.upperlink.nibss.cmms.repo;

import ng.upperlink.nibss.cmms.dto.AuditRequest;
import ng.upperlink.nibss.cmms.model.Audit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;


@Transactional
@Repository
public interface AuditRepo extends JpaRepository<Audit, Long> {


    @Query(value = "select new ng.upperlink.nibss.cmms.dto.AuditRequest(" +
            "a.id, a.macAddress, a.ipAddress,a.userName, a.auditCreatedAt,a.action, a.agentCode) " +
            "from Audit a where a.auditCreatedAt between ?1 and ?2 order by a.auditCreatedAt desc ",
    countQuery = "select count(a) from Audit a where a.auditCreatedAt between ?1 and ?2")
    Page<AuditRequest> findByDateRange(Date startDate, Date endDate, Pageable pageable);


    @Query(value = "select new ng.upperlink.nibss.cmms.dto.AuditRequest(" +
            "a.id, a.macAddress, a.ipAddress,a.userName, a.auditCreatedAt,a.action, a.agentCode) " +
            "from Audit a where a.agentCode =?1 and a.auditCreatedAt between ?2 and ?3 order by a.auditCreatedAt desc ",
            countQuery = "select count(a) from Audit a where a.auditCreatedAt between ?2 and ?3 and a.agentCode = ?1")
    Page<AuditRequest> findByAgentCodeAndDateRange(String agentCode, Date startDate, Date endDate, Pageable pageable);


    @Query(value = "select new ng.upperlink.nibss.cmms.dto.AuditRequest(" +
            "a.id, a.macAddress, a.ipAddress,a.userName, a.auditCreatedAt,a.action, a.agentCode) " +
            "from Audit a where a.userName =?1 and a.auditCreatedAt between ?2 and ?3 order by a.auditCreatedAt desc ",
            countQuery = "select count(a) from Audit a where a.auditCreatedAt between ?2 and ?3 and a.userName = ?1")
    Page<AuditRequest> findByUsernameAndDateRange(String username, Date startDate, Date endDate, Pageable pageable);


    @Query(value = "select new ng.upperlink.nibss.cmms.dto.AuditRequest(" +
            "a.id, a.macAddress, a.ipAddress,a.userName, a.auditCreatedAt,a.action, a.agentCode) " +
            "from Audit a where a.userName =?1 and a.agentCode = ?2 and a.auditCreatedAt between ?3 and ?4 order by a.auditCreatedAt desc ",
            countQuery = "select count(a) from Audit a where a.auditCreatedAt between ?3 and ?4 and a.userName = ?1 and a.agentCode = ?2")
    Page<AuditRequest> findByUsernameAndAgentCodeAndDateRange(String username, String agentCode, Date startDate, Date endDate, Pageable pageable);


    @Query(value = "select new ng.upperlink.nibss.cmms.dto.AuditRequest(" +
            "a.id, a.macAddress, a.ipAddress,a.userName, a.auditCreatedAt,a.action, a.agentCode) " +
            "from Audit a  order by a.auditCreatedAt desc ",
            countQuery = "select count(a) from Audit a")
    Page<AuditRequest> findAllTrimmedDown(Pageable pageable);


    @Query(value = "select new ng.upperlink.nibss.cmms.dto.AuditRequest(" +
            "a.id, a.macAddress, a.ipAddress,a.userName, a.auditCreatedAt,a.action, a.agentCode) " +
            "from Audit a where a.userName = ?1 and a.agentCode = ?2 order by a.auditCreatedAt desc ",
            countQuery = "select count(a) from Audit a where a.userName = ?1 and a.agentCode = ?2")
    Page<AuditRequest> findAllByUsernameAndAgentCodeTrimmedDown(String userName, String agentCode, Pageable pageable);


    @Query(value = "select new ng.upperlink.nibss.cmms.dto.AuditRequest(" +
            "a.id, a.macAddress, a.ipAddress,a.userName, a.auditCreatedAt,a.action, a.agentCode) " +
            "from Audit a  where a.userName = ?1 order by a.auditCreatedAt desc ",
            countQuery = "select count(a) from Audit a where a.userName = ?1")
    Page<AuditRequest> findAllByUsernameTrimmedDown(String userName, Pageable pageable);


    @Query(value = "select new ng.upperlink.nibss.cmms.dto.AuditRequest(" +
            "a.id, a.macAddress, a.ipAddress,a.userName, a.auditCreatedAt,a.action, a.agentCode) " +
            "from Audit a  where a.agentCode = ?1 order by a.auditCreatedAt desc ",
            countQuery = "select count(a) from Audit a where a.agentCode = ?1")
    Page<AuditRequest> findAllByAgentCodeTrimmedDown(String agentCode, Pageable pageable);

}
