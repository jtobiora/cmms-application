package ng.upperlink.nibss.cmms.controller;

import ng.upperlink.nibss.cmms.service.CountryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by stanlee on 10/04/2018.
 */
@RestController
@RequestMapping("/country")
public class CountryController {

    private static Logger LOG = LoggerFactory.getLogger(CountryController.class);

    private CountryService countryService;

    @Autowired
    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping
    public ResponseEntity getAllCountries(){
        return ResponseEntity.ok(countryService.get());
    }

    @GetMapping("/{id}")
    public ResponseEntity getCountry(@PathVariable("id") Long id){

        if (id == null){
            return ResponseEntity.badRequest().body("Id is null");
        }

        return ResponseEntity.ok(countryService.get(id));
    }
}
