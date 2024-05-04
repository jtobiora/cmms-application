package ng.upperlink.nibss.cmms.controller;

import ng.upperlink.nibss.cmms.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Eva on 5/18/2018.
 */

@RestController
public class SearchController {
/*
    private AgentService agentService;
    private AgentMgrService agentMgrService;
    private NibssService nibssService;
    private EnrollerService enrollerService;
    private BranchService branchService;

    @Autowired
    public SearchController(AgentService agentService,AgentMgrService agentMgrService,NibssService nibssService,EnrollerService enrollerService,BranchService branchService){
        this.agentService = agentService;
        this.agentMgrService = agentMgrService;
        this.nibssService = nibssService;
        this.enrollerService = enrollerService;
        this.branchService = branchService;
    }

    @GetMapping("/user/agent/search/{anykey}")
    public ResponseEntity searchAgent(@PathVariable String anykey, @RequestParam int pageNumber, @RequestParam int pageSize){
        return ResponseEntity.ok(agentService.getAllByAnyKey(anykey,new PageRequest(pageNumber,pageSize)));
    }
    @GetMapping("/user/agt-mgr/search/{anykey}")
    public ResponseEntity searchAgentMgr(@PathVariable String anykey,@RequestParam int pageNumber, @RequestParam int pageSize){
        return ResponseEntity.ok(agentMgrService.getAllByAnyKey(anykey,new PageRequest(pageNumber,pageSize)));
    }

    @GetMapping("/user/nibss/search/{anykey}")
    public ResponseEntity searchNibss(@PathVariable String anykey,@RequestParam int pageNumber, @RequestParam int pageSize){
        return ResponseEntity.ok(nibssService.getAllByAnyKey(anykey,new PageRequest(pageNumber,pageSize)));
    }

    @GetMapping("/user/enroller/search/{anykey}")
    public ResponseEntity searchEnroller(@PathVariable String anykey,@RequestParam int pageNumber, @RequestParam int pageSize){
        return ResponseEntity.ok(enrollerService.getAllByAnyKey(anykey,new PageRequest(pageNumber,pageSize)));
    }

    @GetMapping("/user/branch/search/{anykey}")
    public ResponseEntity searchBranch(@PathVariable String anykey,@RequestParam int pageNumber, @RequestParam int pageSize){
        return ResponseEntity.ok(branchService.getAllByAnyKey(anykey,new PageRequest(pageNumber,pageSize)));
    }*/
}
