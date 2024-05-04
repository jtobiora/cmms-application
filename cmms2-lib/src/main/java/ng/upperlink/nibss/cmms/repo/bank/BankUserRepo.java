package ng.upperlink.nibss.cmms.repo.bank;

import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.bank.BankUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@Repository
public interface BankUserRepo  extends JpaRepository<BankUser, Long>{

    @Modifying
    @Query("UPDATE BankUser b SET b.activated = CASE b.activated WHEN true THEN false ELSE true END WHERE b.id = :id")
    int toggle(@Param("id") Long id);

    @Query("select b from BankUser b where b.userBank.id =:bankId and b.authorizationStatus <>:rejected")
    Page<BankUser> getAllByBankId(@Param("bankId") Long bankId,@Param("rejected") AuthorizationStatus rejected, Pageable pageable);

//    @Query("select b from BankUser b where b.makerChecker.approvalStatus = :approvalStatus and b.userBank.id =:bankId")
//    Page<BankUser> getAllByApprovalStatusAndBankCode(@Param("approvalStatus") Boolean approvalStatus, @Param("bankId") Long bankId, Pageable pageable);

    @Query("select b from BankUser b where b.id = :id")
    BankUser getById(@Param("id") Long id);

    @Query("select b from BankUser b where b.staffNumber like :propertyName or b.name.firstName like :propertyName or b.name.lastName like :propertyName or b.emailAddress like :propertyName or b.phoneNumber like :propertyName")
    Page<BankUser> getAllByPropName(@Param("propertyName") String propertyName,Pageable pageable);

//    @Query("select b from BankUser b where b.staffNumber like :propertyName or b.name.firstName like :propertyName or b.name.lastName like :propertyName or b.emailAddress like :propertyName or b.phoneNumber like :propertyName and b.userBank.id=:bankId")
//    Page<BankUser> getAllByPropNameAndBankCode(@Param("propertyName") String propertyName, @Param("bankId") Long bankId, Pageable pageable);

    @Query("select b from BankUser b where b.activated = :activated and b.userBank.id = :bankId and b.authorizationStatus <>:rejected")
    Page<BankUser> getAllByStatusAndBankId(@Param("activated") boolean activated, @Param("bankId") Long bankId,@Param("rejected") AuthorizationStatus rejected, Pageable pageable);

    @Query("select b from BankUser b where b.activated = :activated and b.userBank.id =:id and b.authorizationStatus <>:rejected")
    List<BankUser> getAllByActiveStatus(@Param("id")Long id,@Param("activated") boolean activated,@Param("rejected") AuthorizationStatus rejected);

    @Query("select b from BankUser b where b.activated = :activated and b.userBank.id =:id and b.authorizationStatus <>:rejected")
    Page<BankUser> getAllByActiveStatus(@Param("id")Long id,@Param("activated") boolean activated,@Param("rejected") AuthorizationStatus rejected,Pageable pageable);

    @Query("select b.emailAddress from BankUser b where b.activated = true and b.makerCheckerType = 'AUTHORIZER' and b.userBank.id=:bankId and b.authorizationStatus <>:rejected")
    List<String> getAllActiveAuthorizerEmailAddress(@Param("bankId") Long bankId,@Param("rejected") AuthorizationStatus rejected);

    @Query(" select count(u.id) from User u where u.emailAddress = :emailAddress")
    long countByEmailAddress(@Param("emailAddress") String var1);

    @Query(" select count(u.id) from User u where u.emailAddress = :emailAddress and u.id <> :id")
    long countByEmailAddressAndNotId(@Param("emailAddress") String var1, @Param("id") Long var2);

    @Query("select b from BankUser b inner join b.roles r where r.name =:roleName and b.userType = :userType and b.activated =:activated and b.userBank = :userBank")
    List<BankUser> getUsersByUserTypeAndRole(@Param("roleName")RoleName roleName, @Param("userType") UserType userType, @Param("activated")boolean activated, @Param("userBank")Bank userBank);

//    @Query("select b from BankUser b where b.authorizationStatus <>:rejected")
//    Page<BankUser> getAllBankUsers(@Param("rejected") AuthorizationStatus rejected, Pageable pageable);

    @Query("select b from BankUser b WHERE b.userBank.id =:id and b.authorizationStatus IN :allPendingUsers")
    Page<BankUser> getAllPendingUsers(@Param("id") Long id,@Param("allPendingUsers") List<AuthorizationStatus> allPendingUsers, Pageable pageable);

    @Query("select b from BankUser b WHERE b.userBank.id =:id and b.authorizationStatus =:authStatus")
    Page<BankUser> getAllApprovedUsers(@Param("id") Long id,@Param("authStatus") AuthorizationStatus authStatus, Pageable pageable);

    @Query("select b from BankUser b WHERE b.userBank.id =:id and b.authorizationStatus IN :rejectedStatus")
    Page<BankUser> getAllRejectedUsers(@Param("id")Long id,@Param("rejectedStatus") List<AuthorizationStatus> rejectedStatus, Pageable pageable);

    @Query("select n from BankUser n where n.id =:id and n.authorizationStatus =:updateStatus")
    BankUser previewUpdate(@Param("id") Long id, @Param("updateStatus")AuthorizationStatus updateStatus);

    @Query("select b from BankUser b")
    List<BankUser> getAllBankUsers();

    @Query("select b from BankUser b where b.activated = :activeStatus")
    List<BankUser> getByStatus(@Param("activeStatus") boolean activeStatus);

    //########################### search ####################
    @Query("select b from BankUser b inner join b.roles r where " +
            "r.description LIKE %:role% " +
            "AND b.emailAddress LIKE %:email% " +
            "AND b.activated =:activated")
    Page<BankUser> search(@Param("email")String email,
                            @Param("role")String role,
                            @Param("activated") boolean activated,
                            Pageable pageable);

    @Query("select b from BankUser b inner join b.roles r where " +
            "r.description LIKE %:role% " +
            "AND b.emailAddress LIKE %:email% " +
            "AND b.createdAt between :from and :to")
    Page<BankUser> search(@Param("email")String email,
                            @Param("role")String role,
                            @Param("from") Date from,
                            @Param("to")Date to,
                            Pageable pageable);

    @Query("select b from BankUser b inner join b.roles r where " +
            "r.description LIKE %:role% " +
            "AND b.emailAddress LIKE %:email% " +
            "AND b.createdAt between :from and :to " +
            "AND b.activated =:activated")
    Page<BankUser> search(@Param("email")String email,
                            @Param("role")String role,
                            @Param("from") Date from,
                            @Param("to")Date to,
                            @Param("activated") boolean activated,
                            Pageable pageable);

    @Query("select b from BankUser b inner join b.roles r where " +
            "r.description LIKE %:role% " +
            "AND b.emailAddress LIKE %:email%")
    Page<BankUser> search(@Param("email")String email,
                            @Param("role")String role,
                            Pageable pageable);


}
