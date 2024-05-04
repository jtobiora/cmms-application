package ng.upperlink.nibss.cmms.service.contact;

import ng.upperlink.nibss.cmms.model.contact.Country;
import ng.upperlink.nibss.cmms.repo.contact.CountryRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {

    private static Logger LOG = LoggerFactory.getLogger(CountryService.class);

    private CountryRepo countryRepo;

    @Autowired
    public void setCountryRepo(CountryRepo countryRepo) {
        this.countryRepo = countryRepo;
    }


    public List<Country> getAllCountries(){
        return countryRepo.findAllCountries();
    }
    public List<Country> get(){
        return countryRepo.findAll();
    }

    public Country get(Long id){
        return countryRepo.findOne(id);
    }

    public Country save(Country country){
        return countryRepo.save(country);
    }

}
