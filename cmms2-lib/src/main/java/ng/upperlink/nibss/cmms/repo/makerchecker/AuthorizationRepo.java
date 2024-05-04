package ng.upperlink.nibss.cmms.repo.makerchecker;

import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.enums.makerchecker.EntityType;
import ng.upperlink.nibss.cmms.model.authorization.AuthorizationTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface AuthorizationRepo extends JpaRepository<AuthorizationTable,Long> {

    @Query("select a from AuthorizationTable a WHERE a.id =:id and a.authorizationStatus IN :allPendingUsers and a.entityType =:entityType")
    Page<AuthorizationTable> getAllPending(@Param("entityType")EntityType entityType,@Param("allPendingUsers") List<AuthorizationStatus> allPendingUsers, Pageable pageable);

    @Query("select a from AuthorizationTable a WHERE a.authorizationStatus =:authStatus and a.entityType =:entityType")
    Page<AuthorizationTable> getAllApproved(@Param("entityType")EntityType entityType, @Param("authStatus") AuthorizationStatus authStatus, Pageable pageable);

    @Query("select a from AuthorizationTable a WHERE a.authorizationStatus IN :rejectedStatus and a.entityType =:entityType")
    Page<AuthorizationTable> getAllRejected(@Param("entityType")EntityType entityType,@Param("rejectedStatus") List<AuthorizationStatus> rejectedStatus, Pageable pageable);

    @Query("select a from AuthorizationTable a where a.id =:id and a.authorizationStatus =:updateStatus")
    AuthorizationTable previewUpdate(@Param("id") Long id, @Param("updateStatus")AuthorizationStatus updateStatus);

}
