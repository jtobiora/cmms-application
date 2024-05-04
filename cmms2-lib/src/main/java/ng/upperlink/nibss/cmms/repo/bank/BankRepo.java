package ng.upperlink.nibss.cmms.repo.bank;

import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.model.bank.Bank;
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
public interface BankRepo extends JpaRepository<Bank, Long> {
    @Modifying
    @Query("UPDATE Bank b SET b.activated = CASE b.activated WHEN true THEN false ELSE true END WHERE b.code = :code")
    int toggle(@Param("code") String code);

    @Query("select b from Bank b where b.code like :propertyName or b.name like :propertyName or b.emandateConfig.notificationUrl like :propertyName or b.nipBankCode like :propertyName ")
    Page<Bank> getBankByAnyPropName(@Param("propertyName") String propertyName, Pageable pageable);

    @Query("select b from Bank b where b.activated =:activeStatus and b.authorizationStatus <>:rejected")
    Page<Bank> getAllActiveStatus(@Param("activeStatus") boolean activeStatus,@Param("rejected") AuthorizationStatus rejected,Pageable pageable);

    @Query("select b from Bank b where b.authorizationStatus <>:rejected")
    Page<Bank> getAllBanks(@Param("rejected") AuthorizationStatus rejected, Pageable pageable);

    @Query("select b from Bank b")
    Page<Bank> getBanks(Pageable pageable);

    @Query("select b from Bank b where b.activated =:activeStatus and b.authorizationStatus <>:rejected")
    List<Bank> getAllActiveStatus(@Param("activeStatus") boolean activeStatus,@Param("rejected") AuthorizationStatus rejected);

    @Query("select b from Bank b where b.apiKey =:apiKey")
    Bank getAllByApiKey(@Param("apiKey") String apiKey);

    @Query("select b from Bank b where b.code =:code")
    Bank getBankByCode(@Param("code") String code);

    @Query("select b from Bank b where b.emandateConfig.username =:username")
    Bank getBankByUserName(@Param("username") String username);

    @Query("select b from Bank b where b.emandateConfig.domainName =:domainName")
    Bank getBankByDomainName(@Param("domainName") String domainName);

    @Query("select b from Bank b where b.id =:id")
    Bank getBankById(@Param("id") Long id);

    @Query("select count(b.id) from Bank b where b.code =:code")
    long countByCode(@Param("code") String code);

    @Query(" select count(b.id) from Bank b where b.code = :code and b.id <> :id")
    long countByCode(@Param("code") String code, @Param("id") Long id);


    @Query(" select count(b.id) from Bank b where b.nipBankCode= :nipBankCode")
    long countByNipBankCode(@Param("nipBankCode") String nipBankCode);

    @Query(" select count(b.id) from Bank b where b.nipBankCode = :nipBankCode and b.id <> :id")
    long countByNipBankCode(@Param("nipBankCode") String nipBankCode, @Param("id") Long id);

    @Query(" select count(b.id) from Bank b where b.name = :name and b.id <> :id")
    long countByName(@Param("name") String name, @Param("id") Long id);

    @Query(" select count(b.id) from Bank b where b.name = :name")
    long countByName(@Param("name") String name);

    @Query("select count(b.id) from Bank b")
    long countBank();

    @Query("select b from Bank b where " +
            "b.code LIKE %:code% " +
            "AND b.name LIKE %:name% " +
            "AND b.activated =:activated " +
            "AND b.nipBankCode LIKE %:nipBankCode%")
    Page<Bank> searchBankEntityUsingStatus(@Param("code") String code,
                          @Param("name") String name,
                          @Param("activated") boolean activated,
                          @Param("nipBankCode") String nipBankCode,
                          Pageable pageable);

    @Query("select b from Bank b where " +
            "b.code LIKE %:code% " +
            "AND b.name LIKE %:name% " +
            "AND b.nipBankCode LIKE %:nipBankCode%")
    Page<Bank> searchBankEntity(@Param("code") String code,
                          @Param("name") String name,
                          @Param("nipBankCode") String nipBankCode,
                          Pageable pageable);


    @Query("select b from Bank b WHERE b.authorizationStatus IN :allPendingUsers")
    Page<Bank> getAllPending(@Param("allPendingUsers") List<AuthorizationStatus> allPendingUsers, Pageable pageable);

    @Query("select b from Bank b WHERE b.authorizationStatus =:authStatus")
    Page<Bank> getAllApproved(@Param("authStatus") AuthorizationStatus authStatus, Pageable pageable);

    @Query("select b from Bank b WHERE b.authorizationStatus IN :rejectedStatus")
    Page<Bank> getAllRejected(@Param("rejectedStatus") List<AuthorizationStatus> rejectedStatus, Pageable pageable);

    @Query("select b from Bank b WHERE b.emandateConfig.authorizationStatus IN :allPendingConfig")
    Page<Bank> getAllPendingConfig(@Param("allPendingConfig") List<AuthorizationStatus> allPendingConfig, Pageable pageable);

    @Query("select b from Bank b WHERE b.emandateConfig.authorizationStatus =:authStatusConfig")
    Page<Bank> getAllApprovedConfig(@Param("authStatusConfig") AuthorizationStatus authStatusConfig, Pageable pageable);

    @Query("select b from Bank b WHERE b.emandateConfig.authorizationStatus IN :rejectedStatusConfig")
    Page<Bank> getAllRejectedConfig(@Param("rejectedStatusConfig") List<AuthorizationStatus> rejectedStatusConfig, Pageable pageable);

    @Query("select n from Bank n where n.id =:id and n.authorizationStatus =:updateStatus")
    Bank previewUpdate(@Param("id") Long id, @Param("updateStatus")AuthorizationStatus updateStatus);

    @Query("select count(b.id) from Bank b where b.id =:id")
    long countAllByBankId(@Param("id") Long psspOwner);

    @Query("select b from Bank b where b.activated = :activeStatus and b.id =:id")
    long countAllByBankIdAndStatus(@Param("activeStatus") boolean activeStatus, @Param("id") Long id);

    @Query("select count(b.id) from Bank b")
    long countAll(@Param("id") Long id);

    @Query("select b from Bank b where b.activated = :activeStatus")
    long countAllByStatus(@Param("activeStatus") boolean activeStatus);
}
