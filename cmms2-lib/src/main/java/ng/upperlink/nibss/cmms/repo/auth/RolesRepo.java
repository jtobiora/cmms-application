package ng.upperlink.nibss.cmms.repo.auth;

import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.enums.RoleType;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.model.auth.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;


@Transactional
@Repository
public interface RolesRepo extends JpaRepository<Role, Long>{

    @Query("select r from Role r")
    Page<Role> getAll(Pageable pageable);

//    @Query("select new ng.upperlink.nibss.cmms.dto.auth.RoleResponse(r.id, r.name, r.description, r.userType, r.activated, r.privileges) from Role r")
//    Page<RoleResponse> getSimpleResponseAll(Pageable pageable);

    @Query("select r from Role r where r.activated = true and r.userType =:userType")
    List<Role> getAllActivated(@Param("userType") UserType userType);

    @Query("select r from Role r where r.activated = true and r.userType =:userType and r.roleType =:roleType")
    List<Role> getAllActivatedByRoleType(@Param("userType") UserType userType, @Param("roleType") RoleType roleType);

    @Query("select r from Role r where r.userType = :userType")
    List<Role> getAllRolesByUserType(@Param("userType") UserType userType);

    @Query("select r from Role r where r.activated = true and r.id in (:ids)")
    List<Role> getAllActivated(@Param("ids") Collection<Long> ids);

    @Query("select r from Role r where r.activated = true and r.id =:id")
    List<Role> getAllActivated(@Param("id") Long id);

    @Query("select r from Role r where r.name =:name ")
    Role getByName(@Param("name") RoleName name);

    @Query("select r from Role r where r.id = :id ")
    Role getById(@Param("id") Long id);

    @Query("select r.name from Role r where r.userType = ?1 and r.activated = true order by r.name asc ")
    List<String> getAllNamesByUserType(@Param("userType") UserType userType);

    @Query("select r from Role r where r.name = :name and r.id <> :id")
    Role getByNameAndNotId(@Param("name") RoleName name, @Param("id") Long id);

    @Modifying
    @Query("UPDATE Role r SET r.activated = CASE r.activated WHEN true THEN false ELSE true END WHERE r.id =:id")
    int toggle(@Param("id") Long id);

    @Query("select r from Role r where r.userType = :userType ")
    List<Role> getAnyRoleByUserType(@Param("userType") UserType userType, Pageable pageable);

}
