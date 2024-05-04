package ng.upperlink.nibss.cmms.controller;

import ng.upperlink.nibss.cmms.dto.StateRequest;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.model.State;
import ng.upperlink.nibss.cmms.service.StateService;
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
@RequestMapping("/state")
public class StateController {

    private static Logger LOG = LoggerFactory.getLogger(StateController.class);

    private StateService stateService;

    @Autowired
    public void setStateService(StateService stateService) {
        this.stateService = stateService;
    }

    @GetMapping
    public ResponseEntity getStatesByThirdParty(){
        return ResponseEntity.ok(stateService.get());
    }

    @ApiIgnore
    @GetMapping("/upl")
    public ResponseEntity getStates(){
        return ResponseEntity.ok(stateService.get());
    }

    @GetMapping("/{id}")
    public ResponseEntity getStateByThirdParty(@PathVariable("id") Long id){
        return ResponseEntity.ok(stateService.get(id));
    }

    @ApiIgnore
    @GetMapping("/upl/{id}")
    public ResponseEntity getState(@PathVariable("id") Long id){
        return ResponseEntity.ok(stateService.get(id));
    }

    @ApiIgnore
    @PostMapping
    public ResponseEntity createState(@Valid @RequestBody StateRequest request){

        String errorResult = stateService.validate(request, false, null);
        if (errorResult != null){
            return ResponseEntity.badRequest().body(new ErrorDetails(errorResult));
        }

        State state = stateService.generate(new State(), request);
        state = stateService.save(state);

        return ResponseEntity.ok(state);
    }

    @ApiIgnore
    @PutMapping
    public ResponseEntity updateState(@Valid @RequestBody StateRequest request){

        String errorResult = stateService.validate(request, true, request.getId());
        if (errorResult != null){
            return ResponseEntity.badRequest().body(new ErrorDetails(errorResult));
        }

        State state = stateService.get(request.getId());
        if (state == null){
            return ResponseEntity.badRequest().body(new ErrorDetails("Unknown data provided"));
        }

        state = stateService.generate(state, request);
        state = stateService.save(state);

        return ResponseEntity.ok(state);
    }

}
