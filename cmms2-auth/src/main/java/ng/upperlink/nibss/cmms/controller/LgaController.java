package ng.upperlink.nibss.cmms.controller;

import ng.upperlink.nibss.cmms.dto.contact.LgaRequest;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.model.contact.Lga;
import ng.upperlink.nibss.cmms.service.contact.LgaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * Created by stanlee on 08/04/2018.
 */
@RestController
@RequestMapping("/state/{state-id}/lga")
public class LgaController {


    private static Logger LOG = LoggerFactory.getLogger(LgaController.class);

    private LgaService lgaService;

    @Autowired
    public void setLgaService(LgaService lgaService) {
        this.lgaService = lgaService;
    }

    @GetMapping
    public ResponseEntity getStateLgasByThirdParty(@PathVariable("state-id") Long stateId){
        return ResponseEntity.ok(lgaService.getAll(stateId));
    }

    @ApiIgnore
    @GetMapping("/upl")
    public ResponseEntity getStateLgas(@PathVariable("state-id") Long stateId){
        return ResponseEntity.ok(lgaService.getAll(stateId));
    }

    @GetMapping("/{id}")
    public ResponseEntity getStateLgaByThirdParty(@PathVariable("state-id") Long stateId, @PathVariable("id") Long id){
        return ResponseEntity.ok(lgaService.get(id,stateId));
    }

    @ApiIgnore
    @GetMapping("/upl/{id}")
    public ResponseEntity getStateLga(@PathVariable("state-id") Long stateId, @PathVariable("id") Long id){
        return ResponseEntity.ok(lgaService.get(id,stateId));
    }

    @ApiIgnore
    @PostMapping
    public ResponseEntity createLga(@Valid @RequestBody LgaRequest request){

        String errorResult = lgaService.validate(request, false, null);
        if (errorResult != null){
            return ResponseEntity.badRequest().body(new ErrorDetails(errorResult));
        }

        Lga lga = lgaService.generate(new Lga(), request);
        lga = lgaService.save(lga);

        return ResponseEntity.ok(lga);
    }

    @ApiIgnore
    @PutMapping
    public ResponseEntity updateLga(@Valid @RequestBody LgaRequest request){

        String errorResult = lgaService.validate(request, true, request.getId());
        if (errorResult != null){
            return ResponseEntity.badRequest().body(new ErrorDetails(errorResult));
        }

        Lga lga = lgaService.get(request.getId(), request.getStateId());
        if (lga == null){
            return ResponseEntity.badRequest().body(new ErrorDetails("Unknown lga provided"));
        }

        lga = lgaService.generate(lga, request);
        lga = lgaService.save(lga);

        return ResponseEntity.ok(lga);
    }

}
