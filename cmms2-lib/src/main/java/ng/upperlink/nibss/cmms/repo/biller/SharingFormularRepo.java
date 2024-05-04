package ng.upperlink.nibss.cmms.repo.biller;

import ng.upperlink.nibss.cmms.model.mandate.SharingFormular;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SharingFormularRepo extends JpaRepository<SharingFormular,Long> {

    @Query("select s from SharingFormular s where s.billerId =:id")
    List<SharingFormular> getListOfSharingFormular(@Param("id")Long id);

    @Modifying
    @Query("delete from SharingFormular s where s.billerId = :billerId")
    void deleteInBulk(@Param("billerId")Long billerId);

    @Query("select s from SharingFormular s where s.fee = :fee")
    public Page<SharingFormular> searchSharingFormular(
            Pageable pageable,
            @Param("fee") BigDecimal fee);

    @Query("select s from SharingFormular s where s.billerId = :billerId ")
    public Page<SharingFormular> searchSharingFormular(
            @Param("billerId")Long billerId,
            Pageable pageable);

    @Query("select s from SharingFormular s where s.beneficiaryId = :beneficiaryId ")
    public Page<SharingFormular> searchSharingFormular(
            Pageable pageable,
            @Param("beneficiaryId") Long beneficiaryId);

    @Query("select s from SharingFormular s where s.beneficiaryId = :beneficiaryId " +
            "AND s.billerId = :billerId")
    public Page<SharingFormular> searchSharingFormular(
            @Param("beneficiaryId") Long beneficiaryId,
            @Param("billerId")Long billerId,
            Pageable pageable
    );

    @Query("select s from SharingFormular s where s.billerId = :billerId " +
            "AND s.fee = :fee")
    public Page<SharingFormular> searchSharingFormular(
            @Param("billerId")Long billerId,
            Pageable pageable,
            @Param("fee") BigDecimal fee);

    @Query("select s from SharingFormular s where s.beneficiaryId = :beneficiaryId " +
            "AND s.fee = :fee")
    public Page<SharingFormular> searchSharingFormular(
            Pageable pageable,
            @Param("beneficiaryId") Long beneficiaryId,
            @Param("fee") BigDecimal fee);

    @Query("select s from SharingFormular s where s.beneficiaryId = :beneficiaryId " +
            "AND s.billerId = :billerId " +
            "AND s.fee = :fee")
    public Page<SharingFormular> searchSharingFormular(
            @Param("beneficiaryId") Long beneficiaryId,
            @Param("billerId")Long billerId,
            Pageable pageable,
            @Param("fee") BigDecimal fee
    );
}
