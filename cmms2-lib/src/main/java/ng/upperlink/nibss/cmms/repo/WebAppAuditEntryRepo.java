package ng.upperlink.nibss.cmms.repo;

import ng.upperlink.nibss.cmms.dto.WebAppAuditEntryDto;
import ng.upperlink.nibss.cmms.model.WebAppAuditEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
@Transactional
public interface WebAppAuditEntryRepo extends JpaRepository<WebAppAuditEntry, Long> {


    @Query(value = "select  new ng.upperlink.nibss.cmms.dto.WebAppAuditEntryDto(w.id,w.user,w.className,w.dateCreated,w.action) " +
            "from WebAppAuditEntry w order by  w.id desc ",
    countQuery = "select count(w) from WebAppAuditEntry  w")
    Page<WebAppAuditEntryDto> findAllStrippedDown(Pageable pageable);


    @Query(value = "select  new ng.upperlink.nibss.cmms.dto.WebAppAuditEntryDto(w.id,w.user,w.className,w.dateCreated,w.action) " +
            "from WebAppAuditEntry w  where w.dateCreated between ?1 and ?2 order by  w.id desc ",
            countQuery = "select count(w) from WebAppAuditEntry  w where w.dateCreated between  ?1 and ?2")
    Page<WebAppAuditEntryDto> findByDateRange(Date startDate, Date endDate, Pageable pageable);



    @Query("select  new ng.upperlink.nibss.cmms.dto.WebAppAuditEntryDto(w.id,w.user,w.className," +
            "w.dateCreated,w.action,w.oldObject,w.newObject) from WebAppAuditEntry w  where w.id = ?1")
    WebAppAuditEntryDto findByIdStrippedDown(long id);



    @Query(value = "select new ng.upperlink.nibss.cmms.dto.WebAppAuditEntryDto(w.id,w.user,w.className,w.dateCreated,w.action) " +
            "from WebAppAuditEntry w where w.user = ?1  order by  w.id desc ",
            countQuery = "select count(w) from WebAppAuditEntry  w where w.user = ?1")
    Page<WebAppAuditEntryDto> findByUser(String user, Pageable pageable);


    @Query(value = "select  new ng.upperlink.nibss.cmms.dto.WebAppAuditEntryDto(w.id,w.user,w.className,w.dateCreated,w.action) " +
            "from WebAppAuditEntry w  where w.user = ?1 and w.dateCreated between ?2 and ?3 order by  w.id desc ",
            countQuery = "select count(w) from WebAppAuditEntry  w where w.user = ?1 and w.dateCreated between  ?2 and ?3")
    Page<WebAppAuditEntryDto> findByUserAndDateRange(String user,Date startDate, Date endDate, Pageable pageable);


    @Query(value = "select  new ng.upperlink.nibss.cmms.dto.WebAppAuditEntryDto(w.id,w.user,w.className,w.dateCreated,w.action) " +
            "from WebAppAuditEntry w  where w.className = ?1  order by  w.id desc ",
            countQuery = "select count(w) from WebAppAuditEntry  w where w.className = ?1")
    Page<WebAppAuditEntryDto> findByClassName(String className, Pageable pageable);

    @Query(value = "select  new ng.upperlink.nibss.cmms.dto.WebAppAuditEntryDto(w.id,w.user,w.className,w.dateCreated,w.action) " +
            "from WebAppAuditEntry w  where w.className = ?1 and w.dateCreated between ?2 and ?3 order by  w.id desc ",
            countQuery = "select count(w) from WebAppAuditEntry  w where w.className = ?1 and w.dateCreated between  ?2 and ?3")
    Page<WebAppAuditEntryDto> findByClassNameAndDateRange(String className, Date startDate, Date endDate, Pageable pageable);

    @Query(value = "select  new ng.upperlink.nibss.cmms.dto.WebAppAuditEntryDto(w.id,w.user,w.className,w.dateCreated,w.action) " +
            "from WebAppAuditEntry w  where w.user = ?1 and w.className = ?2 order by  w.id desc ",
            countQuery = "select count(w) from WebAppAuditEntry  w where w.user = ?1 and w.className = ?2")
    Page<WebAppAuditEntryDto> findByUserAndClassName(String username, String className, Pageable pageable);


    @Query(value = "select  new ng.upperlink.nibss.cmms.dto.WebAppAuditEntryDto(w.id,w.user,w.className,w.dateCreated,w.action) " +
            "from WebAppAuditEntry w  where w.user = ?1 and w.className = ?2 and  w.dateCreated between ?3 and ?4 order by  w.id desc ",
            countQuery = "select count(w) from WebAppAuditEntry  w where w.user = ?1 and  w.className = ?2 and  w.dateCreated between  ?3 and ?4")
    Page<WebAppAuditEntryDto> findByUserAndClassNameAndDateRange(String username, String className, Date startDate,
                                                                 Date endDate, Pageable pageable);
}
