package ng.upperlink.nibss.cmms.controller;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.mandates.MandateSearchRequest;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.repo.SearchRepo;
import ng.upperlink.nibss.cmms.service.mandateImpl.MandateSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.ParseException;

@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {
    private MandateSearchService mandateSearchService;
    private SearchRepo searchRepo;

    @Autowired
    public void setSearchRepo(SearchRepo searchRepo){
        this.searchRepo = searchRepo;
    }

    @Autowired
    public void setMandateSearchService(MandateSearchService mandateSearchService){
        this.mandateSearchService = mandateSearchService;
    }

    @PostMapping("/mandate")
    public ResponseEntity searchMandates(@Valid @RequestBody MandateSearchRequest r, Pageable pageable) throws ParseException {

      try{
          return searchRepo.searchMandates(r.getMandateCode(),r.getMandateStartDate(),r.getMandateEndDate(),r.getMandateStatus(),r.getSubscriberCode(),
                  r.getAccName(),r.getAccNumber(),r.getBvn(),r.getEmail(),r.getBankCode(),
                  r.getProductName(),r.getMandateType(),r.getMandateCategory(),r.getChannel(),r.getAddress(),r.getPayerName(),r.getAmount(),r.getFrequency(),pageable);

      }catch (Exception ex){
          log.error("Exception thrown while trying to search for mandates ",ex);
          return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

}
