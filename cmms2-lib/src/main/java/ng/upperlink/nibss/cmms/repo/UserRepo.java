package ng.upperlink.nibss.cmms.repo;

import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.model.User;
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
public interface UserRepo extends JpaRepository<User, Long>{

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.loggedToken = :token WHERE u.id = :id")
    void updateToken(@Param("token") String token,@Param("id") Long id);

    @Query("select u from User u")
    Page<User> getAllUsers(Pageable pageable);

    @Query("select u from User u")
    List<User> getAllUsers();

    @Query("select u from User u where u.emailAddress = :emailAddress")
    User findUserByEmailAddress(@Param("emailAddress") String emailAddress);

    @Query("select u from User u where u.emailAddress = :emailAddress and u.userType = :userType")
    User findUserByEmailAddressAndUserType(@Param("emailAddress") String emailAddress, @Param("userType") UserType userType);

    @Query("select u from User u where u.emailAddress = :emailAddress and u.userType in :userType")
    User findUserByEmailAddressAndUserType(@Param("emailAddress") String emailAddress, @Param("userType") List<UserType> userType);


    @Query("select u from User u where u.emailAddress = :emailAddress and u.id <> :id")
    User findUserByEmailAddressAndNotId(@Param("emailAddress") String emailAddress, @Param("id") Long id);

    @Query(" select count(u.id) from User u where u.emailAddress = :emailAddress")
    long countByEmailAddress(@Param("emailAddress") String emailAddress);

    @Query(" select count(u.id) from User u where u.emailAddress = :emailAddress and u.id <> :id")
    long countByEmailAddressAndNotId(@Param("emailAddress") String emailAddress, @Param("id") Long id);

    @Query(" select count(u.id) from User u where u.phoneNumber = :phoneNumber")
    long countByPhoneNum(@Param("phoneNumber") String phoneNumber);

    @Query(" select count(u.id) from User u where u.phoneNumber = :phoneNumber and u.id <> :id")
    long countByPhoneNumAndNotId(@Param("phoneNumber") String phoneNumber, @Param("id") Long id);

   /* @Modifying
    @Query("UPDATE User u SET u.activated = CASE u.activated WHEN true THEN false ELSE true END WHERE u.id = :id")
    int toggleBiller(@Param("id") Long id);*/
    
    // Get all users for passwword update
    @Query("SELECT new ng.upperlink.nibss.cmms.model.User(u.id, u.emailAddress, u.password, u.change_password, u.passwordUpdatedAt) FROM User u")
    List<User> getUsersForPasswordUpdate();

    @Query("select count(u) from User u where u.userType = ?1")
    long countByType(UserType userType);

    @Query("select upper(u.emailAddress) from User u")
    List<String> getAllEmailAddress();

    @Query("select u from User u inner join u.roles r where r.name =:roleName and u.userType = :userType and u.activated =:activated")
    List<User> getUsersByUserType(@Param("roleName")RoleName roleName,@Param("userType") UserType userType,@Param("activated")boolean activated);

}
