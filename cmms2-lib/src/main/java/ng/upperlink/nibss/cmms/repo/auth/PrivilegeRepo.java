package ng.upperlink.nibss.cmms.repo.auth;

import ng.upperlink.nibss.cmms.enums.Module;
import ng.upperlink.nibss.cmms.model.auth.Privilege;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;


@Transactional
@Repository
public interface PrivilegeRepo extends JpaRepository<Privilege, Long>{

    @Query("select t from Privilege t where t.id in :ids and t.activated = true")
    Set<Privilege> getAllByIdAndActivated(@Param("ids") List<Long> ids);

    @Query("select t from Privilege t where t.activated = true ")
    Page<Privilege> getAllByActivated(Pageable pageable);

    @Query("select t from Privilege t")
    Page<Privilege> getAll(Pageable pageable);

    @Query("select t from Privilege t where t.activated = true ")
    List<Privilege> getAllByActivated();

    @Query("select t from Privilege t")
    List<Privilege> getAll();

    @Query("select t from Privilege t where t.name = :name and t.module = :moduleName")
    Privilege getByNameAmdModule(@Param("name") String name, @Param("moduleName") Module module);

    @Query("select t from Privilege t where t.name = :name and t.id <> :id  and t.module = :moduleName")
    Privilege getByNameAndModuleAndNotId(@Param("name") String name, @Param("id") Long id, @Param("moduleName") Module module);

    @Modifying
    @Query("UPDATE Privilege t SET t.activated = CASE t.activated WHEN true THEN false ELSE true END WHERE t.id = :id")
    int toggle(@Param("id") Long id);
}
