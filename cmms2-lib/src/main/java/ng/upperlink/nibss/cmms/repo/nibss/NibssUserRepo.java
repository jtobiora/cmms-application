package ng.upperlink.nibss.cmms.repo.nibss;

import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.model.NibssUser;
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
public interface NibssUserRepo extends JpaRepository<NibssUser, Long> {
    @Modifying
    @Query("UPDATE NibssUser n SET n.activated = CASE n.activated WHEN true THEN false ELSE true END WHERE n.id = :id")
    int toggle(@Param("id") Long id);

    @Query("select n from NibssUser n where n.staffNumber like :anyKey or n.name.firstName like :anyKey or n.name.lastName like :anyKey or n.emailAddress like :anyKey or n.emailAddress like :anyKey ")
    Page<NibssUser> getAllByAnyKey(@Param("anyKey") String anyKey, Pageable pageable);

    @Query("select n from NibssUser n where n.activated = true and n.userType = :userType")
    Page<NibssUser> getAllActivated(@Param("userType") UserType userType, Pageable pageable);

    @Query("select n from NibssUser n where n.id = :id and n.userType = :userType")
    NibssUser get(@Param("id") Long id, @Param("userType") UserType userType);

    @Query("select n from NibssUser n where n.id = :userId and n.activated = true")
    NibssUser getByUser(@Param("userId") Long userId);

    @Query("select n from NibssUser n")
    Page<NibssUser> getUsers(Pageable pageable);

    @Query("select n from NibssUser n inner join n.roles r WHERE (r.description LIKE %:role% ) AND (n.emailAddress LIKE %:email%) " +
            "AND (n.name.firstName LIKE %:firstName%) AND (n.name.middleName LIKE %:middleName%) " +
            "AND (n.name.lastName LIKE %:lastName%) " +
            "AND (n.phoneNumber LIKE %:phoneNumber%) " +
            "AND (n.staffNumber LIKE %:staffNumber%)")
    Page<NibssUser> searchNIBSSUsers(@Param("role") String role,
                                     @Param("email") String email,
                                     @Param("firstName") String firstName,
                                     @Param("middleName") String middleName,
                                     @Param("lastName") String lastName,
                                     @Param("phoneNumber") String phoneNumber,
                                     @Param("staffNumber") String staffNumber,
                                     Pageable pageable);


    @Query("select n from NibssUser n inner join n.roles r WHERE (r.description LIKE %:role% ) AND (n.emailAddress LIKE %:email%) " +
            "AND (n.name.firstName LIKE %:firstName%) AND (n.name.middleName LIKE %:middleName%) " +
            "AND (n.name.lastName LIKE %:lastName%) " +
            "AND (n.phoneNumber LIKE %:phoneNumber%) " +
            "AND (n.staffNumber LIKE %:staffNumber%) " +
            "AND n.activated =:activated")
    Page<NibssUser> searchNIBSSUsersByStatusInclusive(@Param("role") String role,
                                                      @Param("email") String email,
                                                      @Param("firstName") String firstName,
                                                      @Param("middleName") String middleName,
                                                      @Param("lastName") String lastName,
                                                      @Param("phoneNumber") String phoneNumber,
                                                      @Param("staffNumber") String staffNumber,
                                                      @Param("activated") boolean activated,
                                                      Pageable pageable);


    @Query("select n from NibssUser n where n.userType = :userType and n.authorizationStatus  <> :rejected")
    Page<NibssUser> getAll(@Param("userType") UserType userType,
                           @Param("rejected") AuthorizationStatus rejected, Pageable pageable);

    @Query("select n from NibssUser n WHERE n.authorizationStatus IN :allPendingUsers")
    Page<NibssUser> getAllPendingUsers(@Param("allPendingUsers") List<AuthorizationStatus> allPendingUsers, Pageable pageable);

    @Query("select n from NibssUser n WHERE n.authorizationStatus =:authStatus")
    Page<NibssUser> getAllApprovedUsers(@Param("authStatus") AuthorizationStatus authStatus, Pageable pageable);

    @Query("select n from NibssUser n WHERE n.authorizationStatus IN :rejectedStatus")
    Page<NibssUser> getAllRejectedUsers(@Param("rejectedStatus") List<AuthorizationStatus> rejectedStatus, Pageable pageable);

    @Query("select n from NibssUser n where n.id =:id and n.authorizationStatus =:updateStatus")
    NibssUser previewUpdate(@Param("id") Long id, @Param("updateStatus") AuthorizationStatus updateStatus);

    @Query("select n from NibssUser n where n.activated = :activeStatus and n.userType = :userType")
    List<NibssUser> getAllActivated(@Param("userType") UserType userType,@Param("activeStatus")boolean activeStatus);

    @Query("select n from NibssUser n")
    List<NibssUser> getAllNIBSSUsers();

    @Query("select n from NibssUser n")
    Page<NibssUser> getNIBSSUserPageable(Pageable pageable);

    //###########################search ===============
    @Query("select b from NibssUser b inner join b.roles r where " +
            "r.description LIKE %:role% " +
            "AND b.emailAddress LIKE %:email% " +
            "AND b.activated =:activated")
    Page<NibssUser> search(@Param("email")String email,
                            @Param("role")String role,
                            @Param("activated") boolean activated,
                            Pageable pageable);

    @Query("select b from NibssUser b inner join b.roles r where " +
            "r.description LIKE %:role% " +
            "AND b.emailAddress LIKE %:email% " +
            "AND b.createdAt between :from and :to")
    Page<NibssUser> search(@Param("email")String email,
                            @Param("role")String role,
                            @Param("from") Date from,
                            @Param("to")Date to,
                            Pageable pageable);

    @Query("select b from NibssUser b inner join b.roles r where " +
            "r.description LIKE %:role% " +
            "AND b.emailAddress LIKE %:email% " +
            "AND b.createdAt between :from and :to " +
            "AND b.activated =:activated")
    Page<NibssUser> search(@Param("email")String email,
                            @Param("role")String role,
                            @Param("from") Date from,
                            @Param("to")Date to,
                            @Param("activated") boolean activated,
                            Pageable pageable);

    @Query("select b from NibssUser b inner join b.roles r where " +
            "r.description LIKE %:role% " +
            "AND b.emailAddress LIKE %:email%")
    Page<NibssUser> search(@Param("email")String email,
                            @Param("role")String role,
                            Pageable pageable);
}
