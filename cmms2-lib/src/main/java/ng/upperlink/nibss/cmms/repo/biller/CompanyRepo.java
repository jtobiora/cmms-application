package ng.upperlink.nibss.cmms.repo.biller;


import ng.upperlink.nibss.cmms.model.biller.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface CompanyRepo extends JpaRepository<Company, Long> {

    @Query("select c from Company c where c.id = :id")
    Company getCompanyById(@Param("id") Long id);

    @Query("select c from Company c where c.industry.id = :id")
    Company getCompanyByIndustry(@Param("id") Long id);

    @Query("select count(c.id) from Company c where c.rcNumber = :rcNumber")
    long countOfSameRCNumber(@Param("rcNumber") String rcNumber);

    @Query("select count(c.id) from Company c where c.rcNumber = :rcNumber and c.id <>:id")
    long countOfSameRCNumber(@Param("rcNumber") String rcNumber,@Param("id") Long id);
}
