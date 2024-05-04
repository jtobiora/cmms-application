package ng.upperlink.nibss.cmms.repo.pssp;

import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.model.biller.BillerUser;
import ng.upperlink.nibss.cmms.model.pssp.PsspUser;
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
public interface PsspUserRepo  extends JpaRepository<PsspUser, Long>{

    @Modifying
    @Query("UPDATE PsspUser p SET p.activated = CASE p.activated WHEN true THEN false ELSE true END WHERE p.id = :id")
    int toggle(@Param("id") Long id);


    @Query("select p from PsspUser p where p.id = :id")
    PsspUser getById(@Param("id") Long id);

    @Query("select p from PsspUser p  where p.activated = :activated and p.pssp.id =:psspId and p.authorizationStatus <>:rejected")
    List<PsspUser> getAllByStatus(@Param("activated") boolean activated, @Param("psspId") Long psspId,@Param("rejected") AuthorizationStatus rejected);

    @Query("select p from PsspUser p where p.activated = :activated and p.pssp.id = :psspId and p.authorizationStatus <>:rejected")
    Page<PsspUser> getAllByStatus(@Param("activated") boolean activated, @Param("psspId") Long psspId, Pageable pageable,@Param("rejected") AuthorizationStatus rejected);

    @Query("select p from PsspUser p where p.activated = :activated and p.authorizationStatus <>:rejected")
    List<PsspUser> getAllByActiveStatus(@Param("activated") boolean activated);

    @Query("select p from PsspUser p where p.activated = :activated and p.authorizationStatus <>:rejected")
    Page<PsspUser> getAllByActiveStatus(@Param("activated") boolean activated,Pageable pageable);

    @Query("select p from PsspUser p")
    Page<PsspUser> getAll(Pageable pageable);

    @Query(" select count(u.id) from User u where u.emailAddress = :emailAddress")
    long countByEmailAddress(@Param("emailAddress") String var1);

    @Query(" select count(u.id) from User u where u.emailAddress = :emailAddress and u.id <> :id")
    long countByEmailAddressAndNotId(@Param("emailAddress") String var1, @Param("id") Long var2);

//    @Query("select p from PsspUser p inner join p.roles r where r.name =:roleName and p.userType = :userType and p.activated =:activated and p.userBank = :userBank")
//    List<PsspUser> getUsersByUserTypeAndRole(@Param("roleName")RoleName roleName, @Param("userType") UserType userType, @Param("activated")boolean activated, @Param("userBank")Bank userBank);

    @Query("select p from PsspUser p WHERE p.pssp.id =:id and p.authorizationStatus IN :allPendingUsers")
    Page<PsspUser> getAllPending(@Param("id") Long id, @Param("allPendingUsers") List<AuthorizationStatus> allPendingUsers, Pageable pageable);

    @Query("select p from PsspUser p WHERE p.pssp.id =:id and p.authorizationStatus =:authStatus")
    Page<PsspUser> getAllApproved(@Param("id") Long id, @Param("authStatus") AuthorizationStatus authStatus, Pageable pageable);

    @Query("select p from PsspUser p WHERE p.pssp.id =:id and p.authorizationStatus IN :rejectedStatus")
    Page<PsspUser> getAllRejected(@Param("id") Long id, @Param("rejectedStatus") List<AuthorizationStatus> rejectedStatus, Pageable pageable);

    @Query("select p from PsspUser p where p.id =:id and p.authorizationStatus =:updateStatus")
    PsspUser previewUpdate(@Param("id") Long id, @Param("updateStatus")AuthorizationStatus updateStatus);

    @Query("select p from PsspUser p")
    List<PsspUser> getAllPSSPUsers();

    @Query("select count(p.id) from PsspUser p where p.pssp.id =:id")
    long countAllByBillerId(@Param("id") Long id);

    @Query("select p from PsspUser p where p.activated = :activeStatus and p.pssp.id =:id")
    long countAllByBillerIdAndStatus(@Param("activeStatus") boolean activeStatus,@Param("id") Long id);

    @Query("select count(p.id) from PsspUser p")
    long countAll(@Param("id") Long id);

    @Query("select p from PsspUser p where p.activated = :activeStatus")
    long countAllByStatus(@Param("activeStatus") boolean activeStatus);

    //###########################search ===============
    @Query("select b from PsspUser b inner join b.roles r where " +
            "r.description LIKE %:role% " +
            "AND b.emailAddress LIKE %:email% " +
            "AND b.activated =:activated")
    Page<PsspUser> search(@Param("email")String email,
                            @Param("role")String role,
                            @Param("activated") boolean activated,
                            Pageable pageable);

    @Query("select b from PsspUser b inner join b.roles r where " +
            "r.description LIKE %:role% " +
            "AND b.emailAddress LIKE %:email% " +
            "AND b.createdAt between :from and :to")
    Page<PsspUser> search(@Param("email")String email,
                            @Param("role")String role,
                            @Param("from") Date from,
                            @Param("to")Date to,
                            Pageable pageable);

    @Query("select b from PsspUser b inner join b.roles r where " +
            "r.description LIKE %:role% " +
            "AND b.emailAddress LIKE %:email% " +
            "AND b.createdAt between :from and :to " +
            "AND b.activated =:activated")
    Page<PsspUser> search(@Param("email")String email,
                            @Param("role")String role,
                            @Param("from") Date from,
                            @Param("to")Date to,
                            @Param("activated") boolean activated,
                            Pageable pageable);

    @Query("select b from PsspUser b inner join b.roles r where " +
            "r.description LIKE %:role% " +
            "AND b.emailAddress LIKE %:email%")
    Page<PsspUser> search(@Param("email")String email,
                            @Param("role")String role,
                            Pageable pageable);

}
