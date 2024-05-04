package ng.upperlink.nibss.cmms.repo.biller;

import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ng.upperlink.nibss.cmms.model.biller.Industry;

import java.util.List;


/**
 * Spring Data  repository for the Industry entity.
 */
@Repository
public interface IndustryRepo extends JpaRepository<Industry, Long> {

    @Query("select count(b.id) from Industry b where b.name =:name")
    long countByName(@Param("name") String name);

    @Query("select b from Industry b where b.id = :id")
    Industry getById(@Param("id") Long id);
    @Query(" select count(b.id) from Industry b where b.name = :name and b.id <> :id")
    long countByName(@Param("name") String name, @Param("id") Long id);

    @Query("select b from Industry b where b.authorizationStatus <>:rejected")
    Page<Industry> getAllIndustry(@Param("rejected") AuthorizationStatus rejected, Pageable pageable);

    @Query("select b from Industry b WHERE b.authorizationStatus IN :allPendingUsers")
    Page<Industry> getAllPending(@Param("allPendingUsers") List<AuthorizationStatus> allPendingUsers, Pageable pageable);

    @Query("select b from Industry b WHERE b.authorizationStatus =:authStatus")
    Page<Industry> getAllApproved(@Param("authStatus") AuthorizationStatus authStatus, Pageable pageable);

    @Query("select b from Industry b WHERE b.authorizationStatus IN :rejectedStatus")
    Page<Industry> getAllRejected(@Param("rejectedStatus") List<AuthorizationStatus> rejectedStatus, Pageable pageable);

    @Query("select n from Industry n where n.id =:id and n.authorizationStatus =:updateStatus")
    Industry previewUpdate(@Param("id") Long id, @Param("updateStatus")AuthorizationStatus updateStatus);

    @Query("select new ng.upperlink.nibss.cmms.dto.response.IndustryResponse(n) from Industry n where n.id =:id")
    Industry findPartial(@Param("id") Long id);

}
