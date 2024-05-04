package ng.upperlink.nibss.cmms.repo.biller;

import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.biller.BillerUser;
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
public interface BillerUserRepo extends JpaRepository<BillerUser,Long> {

    @Modifying
    @Query("UPDATE BillerUser b SET b.activated = CASE b.activated WHEN true THEN false ELSE true END WHERE b.id = :id")
    int toggle(@Param("id") Long id);

    @Query("select b from BillerUser b where b.biller.id =:billerId and b.authorizationStatus <>:rejected")
    Page<BillerUser> getAllByBillerId(@Param("billerId") Long billerId,@Param("rejected") AuthorizationStatus rejected, Pageable pageable);

    @Query("select b from BillerUser b where b.id = :id")
    BillerUser getById(@Param("id") Long id);

    @Query("select b from BillerUser b where b.activated = :activated and b.biller.id =:billerId and b.authorizationStatus <>:rejected")
    Page<BillerUser> getAllByActiveStatusAndBillerId(@Param("activated") boolean activated, @Param("billerId") Long billerId,@Param("rejected") AuthorizationStatus rejected, Pageable pageable);

    @Query("select b from BillerUser b inner join b.roles r where r.name =:roleName and b.userType = :userType and b.activated =:activated and b.biller = :biller")
    List<BillerUser> getUsersByUserTypeAndRole(@Param("roleName")RoleName roleName, @Param("userType") UserType userType, @Param("activated")boolean activated,@Param("biller")Biller biller);

    @Query("select b.emailAddress from BillerUser b where b.activated = true  and b.biller.id =:billerId ")
    List<String> getAllActiveAuthorizerEmailAddress(@Param("billerId") Long billerId);

    @Query("select b from BillerUser b where b.authorizationStatus <>:rejected")
    Page<BillerUser> getAll(@Param("rejected") AuthorizationStatus rejected, Pageable pageable);

    @Query("select b from BillerUser b WHERE b.biller.id =:id and b.authorizationStatus IN :allPendingUsers")
    Page<BillerUser> getAllPendingUsers(@Param("id") Long id,@Param("allPendingUsers") List<AuthorizationStatus> allPendingUsers, Pageable pageable);

    @Query("select b from BillerUser b WHERE b.biller.id =:id and b.authorizationStatus =:authStatus")
    Page<BillerUser> getAllApprovedUsers(@Param("id") Long id,@Param("authStatus") AuthorizationStatus authStatus, Pageable pageable);

    @Query("select b from BillerUser b WHERE b.biller.id =:id and b.authorizationStatus IN :rejectedStatus")
    Page<BillerUser> getAllRejectedUsers(@Param("id") Long id,@Param("rejectedStatus") List<AuthorizationStatus> rejectedStatus, Pageable pageable);

    @Query("select n from BillerUser n where n.id =:id and n.authorizationStatus =:updateStatus")
    BillerUser previewUpdate(@Param("id") Long id, @Param("updateStatus")AuthorizationStatus updateStatus);

    @Query("select count(b.id) from BillerUser b where b.biller.id =:id")
    long countAllByBillerId(@Param("id") Long id);

    @Query("select b from BillerUser b where b.activated = :activeStatus and b.biller.id =:id")
    long countAllByBillerIdAndStatus(@Param("activeStatus") boolean activeStatus,@Param("id") Long id);

    @Query("select count(b.id) from BillerUser b")
    long countAll(@Param("id") Long id);

    @Query("select b from BillerUser b where b.activated = :activeStatus")
    long countAllByStatus(@Param("activeStatus") boolean activeStatus);

    //###########################search ===============
    @Query("select b from BillerUser b inner join b.roles r where " +
            "r.description LIKE %:role% " +
            "AND b.emailAddress LIKE %:email% " +
            "AND b.activated =:activated")
    Page<BillerUser> search(@Param("email") String email,
                                @Param("role") String role,
                                @Param("activated") boolean activated,
                                Pageable pageable);

    @Query("select b from BillerUser b inner join b.roles r where " +
            "r.description LIKE %:role% " +
            "AND b.emailAddress LIKE %:email% " +
            "AND b.createdAt between :from and :to")
    Page<BillerUser> search(@Param("email")String email,
                                @Param("role")String role,
                                @Param("from") Date from,
                                @Param("to")Date to,
                                Pageable pageable);

    @Query("select b from BillerUser b inner join b.roles r where " +
            "r.description LIKE %:role% " +
            "AND b.emailAddress LIKE %:email% " +
            "AND b.createdAt between :from and :to " +
            "AND b.activated =:activated")
    Page<BillerUser> search(@Param("email")String email,
                            @Param("role")String role,
                            @Param("from") Date from,
                            @Param("to")Date to,
                            @Param("activated") boolean activated,
                            Pageable pageable);

    @Query("select b from BillerUser b inner join b.roles r where " +
            "r.description LIKE %:role% " +
            "AND b.emailAddress LIKE %:email%")
    Page<BillerUser> search(@Param("email")String email,
                            @Param("role")String role,
                            Pageable pageable);

}