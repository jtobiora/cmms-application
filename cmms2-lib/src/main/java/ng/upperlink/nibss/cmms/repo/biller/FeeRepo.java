package ng.upperlink.nibss.cmms.repo.biller;

import ng.upperlink.nibss.cmms.enums.FeeBearer;
import ng.upperlink.nibss.cmms.enums.SplitType;
import ng.upperlink.nibss.cmms.model.mandate.Fee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FeeRepo extends JpaRepository<Fee,Long>{

    @Query("select f from Fee f where f.biller.id =:billerId")
    Fee findFeeConfigByBillerId(@Param("billerId")Long billerid);

    @Query("select f from Fee f where f.id =:feeId")
    Fee findFeeConfigById(@Param("feeId")Long feeId);

    @Query("select f from Fee f")
    List<Fee> findAllBillerFeeConfig();

    @Query("select f from Fee f")
    Page<Fee> findAllBillerFeeConfig(Pageable pageable);

    @Query("select f from Fee f where f.billerDebitAccountNumber LIKE %:accNum% " +
            "AND f.biller.name LIKE %:billerName% ")
    Page<Fee> searchFees(@Param("accNum") String accNum,
                          @Param("billerName") String billerName,
                          Pageable pageable);

    @Query("select f from Fee f where f.billerDebitAccountNumber LIKE %:accNum% " +
            "AND f.biller.name LIKE %:billerName% " +
            "AND f.feeBearer = :feeBearer")
    Page<Fee> searchFees(@Param("accNum") String accNum,
                         @Param("billerName") String billerName,
                         @Param("feeBearer") FeeBearer feeBearer,
                         Pageable pageable);

    @Query("select f from Fee f where f.billerDebitAccountNumber LIKE %:accNum% " +
            "AND f.biller.name LIKE %:billerName% " +
            "AND f.splitType = :splitType")
    Page<Fee> searchFees(@Param("accNum") String accNum,
                         @Param("billerName") String billerName,
                         @Param("splitType") SplitType splitType,
                         Pageable pageable);

    @Query("select f from Fee f where f.billerDebitAccountNumber LIKE %:accNum% " +
            "AND f.biller.name LIKE %:billerName% " +
            "AND f.splitType = :splitType " +
            "AND f.feeBearer = :feeBearer")
    Page<Fee> searchFees(@Param("accNum") String accNum,
                         @Param("billerName") String billerName,
                         @Param("splitType") SplitType splitType,
                         @Param("feeBearer") FeeBearer feeBearer,
                         Pageable pageable);

    @Query("select f from Fee f where f.billerDebitAccountNumber LIKE %:accNum% " +
            "AND f.biller.name LIKE %:billerName% " +
            "AND f.markUpFee = :markUpFee")
    Page<Fee> searchFees(@Param("accNum") String accNum,
                         @Param("billerName") String billerName,
                         @Param("markUpFee") BigDecimal markUpFee,
                         Pageable pageable);

    @Query("select f from Fee f where f.billerDebitAccountNumber LIKE %:accNum% " +
            "AND f.biller.name LIKE %:billerName% " +
            "AND f.feeBearer = :feeBearer " +
            "AND f.markUpFee = :markUpFee")
    Page<Fee> searchFees(@Param("accNum") String accNum,
                         @Param("billerName") String billerName,
                         @Param("feeBearer") FeeBearer feeBearer,
                         @Param("markUpFee") BigDecimal markUpFee,
                         Pageable pageable);

    @Query("select f from Fee f where f.billerDebitAccountNumber LIKE %:accNum% " +
            "AND f.biller.name LIKE %:billerName% " +
            "AND f.splitType = :splitType " +
            "AND f.markUpFee = :markUpFee")
    Page<Fee> searchFees(@Param("accNum") String accNum,
                         @Param("billerName") String billerName,
                         @Param("splitType") SplitType splitType,
                         @Param("markUpFee") BigDecimal markUpFee,
                         Pageable pageable);

    @Query("select f from Fee f where f.billerDebitAccountNumber LIKE %:accNum% " +
            "AND f.biller.name LIKE %:billerName% " +
            "AND f.splitType = :splitType " +
            "AND f.feeBearer = :feeBearer " +
            "AND f.markUpFee = :markUpFee")
    Page<Fee> searchFees(@Param("accNum") String accNum,
                         @Param("billerName") String billerName,
                         @Param("splitType") SplitType splitType,
                         @Param("feeBearer") FeeBearer feeBearer,
                         @Param("markUpFee") BigDecimal markUpFee,
                         Pageable pageable);


    //////////
    @Query("select f from Fee f where f.billerDebitAccountNumber LIKE %:accNum% " +
            "AND f.biller.name LIKE %:billerName% " +
            "AND f.percentageAmount = :percentageAmount")
    Page<Fee> searchFees(@Param("percentageAmount") BigDecimal percentageAmount,
                         @Param("accNum") String accNum,
                         @Param("billerName") String billerName,
                         Pageable pageable);

    @Query("select f from Fee f where f.billerDebitAccountNumber LIKE %:accNum% " +
            "AND f.biller.name LIKE %:billerName% " +
            "AND f.feeBearer = :feeBearer " +
            "AND f.percentageAmount = :percentageAmount")
    Page<Fee> searchFees( @Param("percentageAmount") BigDecimal percentageAmount,
                         @Param("accNum") String accNum,
                         @Param("billerName") String billerName,
                         @Param("feeBearer") FeeBearer feeBearer,
                         Pageable pageable);

    @Query("select f from Fee f where f.billerDebitAccountNumber LIKE %:accNum% " +
            "AND f.biller.name LIKE %:billerName% " +
            "AND f.splitType = :splitType " +
            "AND f.percentageAmount = :percentageAmount")
    Page<Fee> searchFees(@Param("percentageAmount") BigDecimal percentageAmount,
                         @Param("accNum") String accNum,
                         @Param("billerName") String billerName,
                         @Param("splitType") SplitType splitType,
                         Pageable pageable);

    @Query("select f from Fee f where f.billerDebitAccountNumber LIKE %:accNum% " +
            "AND f.biller.name LIKE %:billerName% " +
            "AND f.splitType = :splitType " +
            "AND f.feeBearer = :feeBearer " +
            "AND f.percentageAmount = :percentageAmount")
    Page<Fee> searchFees(@Param("percentageAmount") BigDecimal percentageAmount,
                         @Param("accNum") String accNum,
                         @Param("billerName") String billerName,
                         @Param("splitType") SplitType splitType,
                         @Param("feeBearer") FeeBearer feeBearer,
                         Pageable pageable);

}
