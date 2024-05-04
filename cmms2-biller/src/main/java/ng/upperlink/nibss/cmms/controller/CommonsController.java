package ng.upperlink.nibss.cmms.controller;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.FeeBearer;
import ng.upperlink.nibss.cmms.enums.SplitType;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@Slf4j
public class CommonsController {

    @GetMapping("/splitTypes")
    public ResponseEntity<?> getAllSplitTypes() {
        try {
            List<String> typeList =  Stream.of(SplitType.values()).map(SplitType::getValue).collect(Collectors.toList());
            return ResponseEntity.ok(this.operate(typeList));
        }catch (Exception e){
            log.error("Request failed",e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/feeBearers")
    public ResponseEntity<?> getAllFeeBearers() {
        try {
            List<String> bearerList =  Stream.of(FeeBearer.values()).map(FeeBearer::getValue).collect(Collectors.toList());
            return ResponseEntity.ok(this.operate(bearerList));
        }catch(Exception e){
            log.error("Request failed",e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<Integer,String> operate(List<String> objList){
        Map<Integer,String> mapObj = new HashMap<>();
        int index = 1;
        for(String list : objList){
            mapObj.put(index,list);
            index++;
        }
        return mapObj;
    }
}
