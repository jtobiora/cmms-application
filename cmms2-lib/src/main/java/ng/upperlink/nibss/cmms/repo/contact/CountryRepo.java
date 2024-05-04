package ng.upperlink.nibss.cmms.repo.contact;

import ng.upperlink.nibss.cmms.model.contact.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;


@Transactional
@Repository
public interface CountryRepo extends JpaRepository<Country, Long> {

    @Query("select c from Country c")
    List<Country> findAllCountries();

}
