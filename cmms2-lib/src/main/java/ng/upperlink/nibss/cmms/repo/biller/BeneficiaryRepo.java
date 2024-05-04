package ng.upperlink.nibss.cmms.repo.biller;

import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.mandate.Beneficiary;
import ng.upperlink.nibss.cmms.model.mandate.Fee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface BeneficiaryRepo extends JpaRepository<Beneficiary,Long> {

    @Query("select b from Beneficiary b where b.id = :id")
    Beneficiary findBeneficiaryById(@Param("id")Long id);

    @Query("select b from Beneficiary b")
    List<Beneficiary> getAllBeneficiaries();

    @Query("select b from Beneficiary b")
    Page<Beneficiary> getAllBeneficiaries(Pageable pageable);

    @Query("select b from Beneficiary b where b.id IN :idList")
    List<Beneficiary> getBeneficiaries(@Param("idList")List<Long> idList);

    @Query("select b from Beneficiary b where b.beneficiaryName LIKE %:beneficiaryName% " +
            "AND b.accountNumber LIKE %:accountNumber% " +
            "AND b.accountName LIKE %:accountName% ")
    Page<Beneficiary> searchBeneficiary(@Param("beneficiaryName") String beneficiaryName,
                                        @Param("accountNumber") String accountNumber,
                                        @Param("accountName") String accountName,
                                        Pageable pageable);

    @Query("select b from Beneficiary b where b.beneficiaryName LIKE %:beneficiaryName% " +
            "AND b.accountNumber LIKE %:accountNumber% " +
            "AND b.accountName LIKE %:accountName% " +
            "AND b.activated = :activated")
    Page<Beneficiary> searchBeneficiary(@Param("beneficiaryName") String beneficiaryName,
                          @Param("accountNumber") String accountNumber,
                          @Param("accountName") String accountName,
                          @Param("activated") boolean activated,
                          Pageable pageable);

}
