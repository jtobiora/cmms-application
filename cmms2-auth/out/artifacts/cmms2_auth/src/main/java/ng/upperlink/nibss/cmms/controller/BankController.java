package ng.upperlink.nibss.cmms.controller;

import ng.upperlink.nibss.cmms.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by stanlee on 19/04/2018.
 */
@ApiIgnore
@RestController
@RequestMapping("/banks")
public class BankController {

    private BankService bankService;

    @Autowired
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping
    public ResponseEntity getAllBanks(){
        return ResponseEntity.ok(bankService.getAll());
    }

}
