package ng.upperlink.nibss.cmms.controller;

import com.codahale.metrics.annotation.Timed;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.service.biller.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST controller for managing Company.
 */
@RestController
@RequestMapping("/company")
public class CompanyController {

    private final Logger log = LoggerFactory.getLogger(CompanyController.class);

    public CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    @Timed
    public ResponseEntity<?> getAllCompanies() {
        try {
            return ResponseEntity.ok(companyService.getAll());
        }catch(Exception e){
            log.error("Failed to retrieve all companies",e);
            return new ResponseEntity<>(new ErrorDetails("Unable to load data."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/company/{id}")
    @Timed
    public ResponseEntity<?> getCompany(@PathVariable Long id) {
        log.debug("REST request to get Company : {}", id);

        try {
            return ResponseEntity.ok(companyService.getById(id));
        }catch(Exception e){
            log.error("Failed to load company with id ",id,e);
            return new ResponseEntity<>(new ErrorDetails("Unable to load details."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
