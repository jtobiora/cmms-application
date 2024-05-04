package ng.upperlink.nibss.cmms.repo.biller;

import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillerRepo extends JpaRepository<Biller,Long> {

    @Query("select b from Biller b")
    Page<Biller> getAllBillers(Pageable pageable);

    @Query("select b from Biller b")
    List<Biller> getAllBillers();

    @Query("select b from Biller b where b.activated = :activated and b.authorizationStatus <>:rejected")
    List<Biller> getActiveBillers(@Param("activated") boolean activated,@Param("rejected") AuthorizationStatus rejected );

    @Query("select b from Biller b where b.activated =:activated and b.authorizationStatus <>:rejected")
    Page<Biller> getActiveBillers(@Param("activated") boolean activeStatus,@Param("rejected") AuthorizationStatus rejected, Pageable pageable);

    @Query("select b from Biller b where b.billerOwner =:billerOwner and b.activated = :activated and b.authorizationStatus <>:rejected")
    List<Biller> getByBillerOwner(@Param("billerOwner") String billerOwner,@Param("activated") boolean activated,@Param("rejected") AuthorizationStatus rejected );

    @Query("select b from Biller b where b.billerOwner =:billerOwner and b.activated =:activated and b.authorizationStatus <>:rejected")
    Page<Biller> getByBillerOwner(@Param("billerOwner") String billerOwner,@Param("activated") boolean activeStatus,@Param("rejected") AuthorizationStatus rejected, Pageable pageable);

    @Query("select b from Biller b where b.id = :billerId")
    Biller getById(@Param("billerId")Long billerId);

    @Query(" select b from Biller b where b.emandateConfig.domainName= :domainName")
    Biller getByDomainName(@Param("domainName") String domainName);

    @Query("select b from Biller b where b.rcNumber =:rcNumber")
    Biller getBillerByRcNumber(@Param("rcNumber") String rcNumber);

    @Query("select b from Biller b where b.emandateConfig.username =:username")
    Biller getBillerByUserName(@Param("username") String username);

    @Query("select b from Biller b where b.bankAsBiller.id =:bankAsBillerId")
    Biller getBillerByBankAsBiller(@Param("bankAsBillerId") Long bankAsBillerId);

    @Query("select b from Biller b where b.billerOwner =:billerOwner")
    Biller getBillerByBillerOwner(@Param("billerOwner") String billerOwner);

    @Modifying
    @Query("UPDATE Biller b SET b.activated = CASE b.activated WHEN true THEN false ELSE true END WHERE b.rcNumber = :rcNumber")
    int toggle(@Param("rcNumber") String rcNumber);

    @Query(" select count(b.id) from Biller b where b.rcNumber =:rcNumber and b.id <> :id")
    long countByBillerCode(@Param("rcNumber") String rcNumber, @Param("id") Long id);

    @Query(" select count(b.id) from Biller b where b.bankAsBiller.id =:bankAsBillerId")
    long countByBankAsBiller(@Param("bankAsBillerId") Long bankAsBillerId);
//
//    @Query(" select count(b.id) from Biller b where b.notificationUrl= :notificationUrl")
//    long countByNotificationUrl(@Param("notificationUrl") String notificationUrl);
//
//    @Query(" select count(b.id) from Biller b where b.notificationUrl = :notificationUrl and b.id <> :id")
//    long countByNotificationUrl(@Param("notificationUrl") String notificationUrl, @Param("id") Long id);

    @Query("select count(b.id) from Biller b")
    long countBiller();

//    @Query(" select count(b.id) from Biller b where b.domainName= :domainName")
//    long countByDomainName(@Param("domainName") String domainName);
//
//    @Query(" select count(b.id) from Biller b where b.domainName = :domainName and b.id <> :id")
//    long countByDomainName(@Param("domainName") String domainName, @Param("id") Long id);

    @Query("select count(b.id) from Biller b where b.rcNumber =:rcNumber")
    long countByCode(@Param("rcNumber") String rcNumber);

    @Query("select count(b.id) from Biller b where b.accountNumber =:accountNumber")
    long countAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("select b from Biller b WHERE b.billerOwner =:billerOwner and b.authorizationStatus IN :allPendingUsers")
    Page<Biller> getAllPending(@Param("billerOwner")String billerOwner,@Param("allPendingUsers") List<AuthorizationStatus> allPendingUsers, Pageable pageable);

    @Query("select b from Biller b WHERE b.billerOwner =:billerOwner and b.authorizationStatus =:authStatus")
    Page<Biller> getAllApproved(@Param("billerOwner")String billerOwner,@Param("authStatus") AuthorizationStatus authStatus, Pageable pageable);

    @Query("select b from Biller b WHERE b.billerOwner =:billerOwner and b.authorizationStatus IN :rejectedStatus")
    Page<Biller> getAllRejected(@Param("billerOwner")String billerOwner,@Param("rejectedStatus") List<AuthorizationStatus> rejectedStatus, Pageable pageable);

    @Query("select b from Biller b where b.id =:id and b.authorizationStatus =:updateStatus")
    Biller previewUpdate(@Param("id") Long id, @Param("updateStatus")AuthorizationStatus updateStatus);

    @Query("select b from Biller b WHERE b.billerOwner =:billerOwner and b.emandateConfig.authorizationStatus IN :allPendingConfig")
    Page<Biller> getAllPendingConfig(@Param("billerOwner")String billerOwner,@Param("allPendingConfig") List<AuthorizationStatus> allPendingConfig, Pageable pageable);

    @Query("select b from Biller b WHERE b.billerOwner =:billerOwner and b.emandateConfig.authorizationStatus =:authStatusConfig")
    Page<Biller> getAllApprovedConfig(@Param("billerOwner")String billerOwner,@Param("authStatusConfig") AuthorizationStatus authStatusConfig, Pageable pageable);

    @Query("select b from Biller b WHERE b.billerOwner =:billerOwner and b.emandateConfig.authorizationStatus IN :rejectedStatusConfig")
    Page<Biller> getAllRejectedConfig(@Param("billerOwner")String billerOwner,@Param("rejectedStatusConfig") List<AuthorizationStatus> rejectedStatusConfig, Pageable pageable);

    @Query("select b from Biller b where b.id =:id and b.emandateConfig.authorizationStatus =:updateStatusConfig")
    Biller previewUpdateConfig(@Param("id") Long id, @Param("updateStatusConfig")AuthorizationStatus updateStatusConfig);

    @Query("select count(b.id) from Biller b where b.billerOwner =:billerOwner")
    long countBillersByOwner(@Param("billerOwner") String billerOwner);


    @Query("select b from Biller b where b.activated = :activeStatus and b.billerOwner =:billerOwner")
    long countAllByBillerIdAndStatus(@Param("activeStatus") boolean activeStatus,@Param("billerOwner") String billerOwner);

    @Query("select count(b.id) from Biller b")
    long countAll(@Param("id") Long id);

    @Query("select b from Biller b where b.activated = :activeStatus")
    long countAllByStatus(@Param("activeStatus") boolean activeStatus);

    @Query("select b from Biller b where b.billerOwner =:billerOwner and b.authorizationStatus <>:rejected")
    List<Biller> getByBillerOwner(@Param("billerOwner") String billerOwner,@Param("rejected") AuthorizationStatus rejected );

    @Query("select b from Biller b where b.billerOwner =:billerOwner and b.authorizationStatus <>:rejected")
    Page<Biller> getByBillerOwner(@Param("billerOwner") String billerOwner,@Param("rejected") AuthorizationStatus rejected, Pageable pageable);

}