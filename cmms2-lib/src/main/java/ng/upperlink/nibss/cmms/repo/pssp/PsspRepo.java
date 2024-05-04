package ng.upperlink.nibss.cmms.repo.pssp;

import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.model.pssp.Pssp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PsspRepo extends JpaRepository<Pssp,Long> {

    @Query("select p from Pssp p where p.psspOwner = :psspOwner and p.authorizationStatus <>:rejected")
    List<Pssp> getByPsspOwner(@Param("psspOwner") String psspOwner,@Param("rejected") AuthorizationStatus rejected);

    @Query("select p from Pssp p where p.psspOwner = :psspOwner and p.authorizationStatus <>:rejected")
    Page<Pssp> getByPsspOwner(@Param("psspOwner") String psspOwner, Pageable pageable,@Param("rejected") AuthorizationStatus rejected);

    @Query("select p from Pssp p where p.activated = :activated and p.authorizationStatus <>:rejected")
    List<Pssp> getActivePssp(@Param("activated") boolean activated,@Param("rejected") AuthorizationStatus rejected);

    @Query("select p from Pssp p where p.activated =:activeStatus and p.authorizationStatus <>:rejected")
    Page<Pssp> getActivePssp(@Param("activeStatus") boolean activeStatus,@Param("rejected") AuthorizationStatus rejected, Pageable pageable);

    @Query("select p from Pssp p where p.apiKey =:apiKey")
    Pssp getAllByApiKey(@Param("apiKey") String apiKey);

    @Query("select count(p.id) from Pssp p where p.apiKey =:apiKey")
    long countAPIKey(@Param("apiKey") String apiKey);

    @Query("select count(p.id) from Pssp p where p.accountNumber =:accountNumber")
    long countAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("select count(p.id) from Pssp p where p.accountNumber =:accountNumber and p.id <>:id")
    long countAccountNumber(@Param("accountNumber") String accountNumber,@Param("id") Long id);

    @Query("select p from Pssp p where p.psspCode =:psspCode")
    Pssp getPsspByCode(@Param("psspCode") String psspCode);

    @Query("select p from Pssp p where p.psspOwner =:psspOwner")
    Pssp getPSSPByOwner(@Param("psspOwner") String psspOwner);

    @Query(" select count(p.id) from Pssp p where p.psspCode =:psspCode and p.id <> :id")
    long countByPSSPCode(@Param("psspCode") String psspCode, @Param("id") Long id);

    @Query("select count(p.id) from Pssp p")
    long countPssp();

    @Query("select count(p.id) from Pssp p where p.rcNumber = :rcNumber")
    long countOfSameRCNumber(@Param("rcNumber") String rcNumber);

    @Query("select count(p.id) from Pssp p where p.rcNumber = :rcNumber and p.id <>:id")
    long countOfSameRCNumber(@Param("rcNumber") String rcNumber,@Param("id") Long id);

    @Query("select p from Pssp p WHERE p.psspOwner =:psspOwner and p.authorizationStatus IN :allPendingUsers")
    Page<Pssp> getAllPending(@Param("psspOwner") String psspOwner,@Param("allPendingUsers") List<AuthorizationStatus> allPendingUsers, Pageable pageable);

    @Query("select p from Pssp p WHERE p.psspOwner =:psspOwner and p.authorizationStatus =:authStatus")
    Page<Pssp> getAllApproved(@Param("psspOwner") String psspOwner,@Param("authStatus") AuthorizationStatus authStatus, Pageable pageable);

    @Query("select p from Pssp p WHERE p.psspOwner =:psspOwner and p.authorizationStatus IN :rejectedStatus")
    Page<Pssp> getAllRejected(@Param("psspOwner") String psspOwner,@Param("rejectedStatus") List<AuthorizationStatus> rejectedStatus, Pageable pageable);

    @Query("select n from Pssp n where n.id =:id and n.authorizationStatus =:updateStatus")
    Pssp previewUpdate(@Param("id") Long id, @Param("updateStatus")AuthorizationStatus updateStatus);

    @Query("select count(p.id) from Pssp p where p.psspOwner =:psspOwner")
    long countAllByPsspId(@Param("psspOwner") Long psspOwner);

    @Query("select p from Pssp p where p.activated = :activeStatus and p.psspOwner =:psspOwner")
    long countAllByPsspIdAndStatus(@Param("activeStatus")boolean activeStatus,@Param("psspOwner") String psspOwner);

    @Query("select count(p.id) from Pssp p")
    long countAll(@Param("id") Long id);

    @Query("select p from Pssp p where p.activated = :activeStatus")
    long countAllByStatus(@Param("activeStatus") boolean activeStatus);
}