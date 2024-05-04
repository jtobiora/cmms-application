package ng.upperlink.nibss.cmms.repo.emandate;

import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.model.emandate.EmandateConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface EmandateConfigRepo extends JpaRepository<EmandateConfig,Long> {
    @Modifying
    @Query("UPDATE EmandateConfig e SET e.activated = CASE e.activated WHEN true THEN false ELSE true END WHERE e.id = :id")
    int toggle(@Param("id") Long id);
    
    @Query("select e from EmandateConfig e where e.id =:id")
    EmandateConfig getById(@Param("id") Long id);

    @Query("select e from EmandateConfig e where e.username =:username")
    EmandateConfig getByUserName(@Param("username") String username);

    @Query("select e from EmandateConfig e where e.domainName =:domainName")
    EmandateConfig getByDomainName(@Param("domainName") String domainName);

    @Query(" select count(e.id) from EmandateConfig e where e.domainName= :domainName")
    long countByDomainName(@Param("domainName") String domainName);

    @Query(" select count(e.id) from EmandateConfig e where e.domainName = :domainName and e.id <> :id")
    long countByDomainName(@Param("domainName") String domainName, @Param("id") Long id);

    @Query(" select count(e.id) from EmandateConfig e where e.username= :username")
    long countByUsername(@Param("username") String username);

    @Query(" select count(e.id) from EmandateConfig e where e.username = :username and e.id <> :id")
    long countByUsername(@Param("username") String username, @Param("id") Long id);

    @Query(" select count(b.id) from EmandateConfig b where b.notificationUrl= :notificationUrl")
    long countByNotificationUrl(@Param("notificationUrl") String notificationUrl);

    @Query(" select count(b.id) from EmandateConfig b where b.notificationUrl = :notificationUrl and b.id <> :id")
    long countByNotificationUrl(@Param("notificationUrl") String notificationUrl, @Param("id") Long id);

    @Query("select e from EmandateConfig e WHERE e.authorizationStatus IN :allPendingUsers")
    Page<EmandateConfig> getAllPending(@Param("allPendingUsers") List<AuthorizationStatus> allPendingUsers, Pageable pageable);

    @Query("select e from EmandateConfig e WHERE e.authorizationStatus =:authStatus")
    Page<EmandateConfig> getAllApproved(@Param("authStatus") AuthorizationStatus authStatus, Pageable pageable);

    @Query("select e from EmandateConfig e WHERE e.authorizationStatus IN :rejectedStatus")
    Page<EmandateConfig> getAllRejected(@Param("rejectedStatus") List<AuthorizationStatus> rejectedStatus, Pageable pageable);

    @Query("select n from EmandateConfig n where n.id =:id and n.authorizationStatus =:updateStatus")
    EmandateConfig previewUpdate(@Param("id") Long id, @Param("updateStatus")AuthorizationStatus updateStatus);

    @Query("select count(e.id) from EmandateConfig e")
    long countAll(@Param("id") Long id);

    @Query("select e from EmandateConfig e where e.activated = :activeStatus")
    long countAllByStatus(@Param("activeStatus") boolean activeStatus);
}
