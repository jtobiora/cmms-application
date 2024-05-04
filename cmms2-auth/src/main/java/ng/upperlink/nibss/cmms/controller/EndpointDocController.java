package ng.upperlink.nibss.cmms.controller;

import ng.upperlink.nibss.cmms.enums.Module;
import ng.upperlink.nibss.cmms.model.auth.Privilege;
import ng.upperlink.nibss.cmms.service.auth.PrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;
//
//@ApiIgnore
//@RestController
public class EndpointDocController {
//    private final RequestMappingHandlerMapping handlerMapping;
//    private PrivilegeService privilegeService;
//
//    @Autowired
//    public EndpointDocController(RequestMappingHandlerMapping handlerMapping) {
//        this.handlerMapping = handlerMapping;
//    }
//
//    @Autowired
//    public void setPrivilegeService(PrivilegeService privilegeService) {
//        this.privilegeService = privilegeService;
//    }
//
//    @RequestMapping(value = "/endpointdoc", method = RequestMethod.GET)
//    public ResponseEntity showEndpoints() {
//
//        List<Privilege> privileges = new ArrayList<>();
//
//        List<String> stringList = new ArrayList<>();
//        this.handlerMapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {
//
//            System.out.println(">>>>>>>>>>>>>>>>>>");
//            System.out.println("requestMappingInfo => " + requestMappingInfo.getPatternsCondition().getPatterns());
//            System.out.println("requestMappingInfo => " + requestMappingInfo.getName());
//            System.out.println("handlerMethod => " + handlerMethod.getMethod().getName());
//            System.out.println("<<<<<<<<<<<<<<<<<<<");
//
//            String url = String.valueOf(requestMappingInfo.getPatternsCondition().getPatterns());
//            if (!url.contains("swagger") || !url.toLowerCase().contains("create") || !url.toLowerCase().contains("update") || !url.toLowerCase().contains("toggle") ){
//                Privilege privilege = new Privilege();
//                privilege.setUrl(url);
//                privilege.setName(handlerMethod.getMethod().getName());
//                privilege.setActivated(true);
//
//                if (url.contains("/auth")) {
//                    privilege.setModule(Module.AUTH);
//                } else if (url.contains("/bank")) {
//                    privilege.setModule(Module.BANK);
//                } else if (url.contains("/biller")) {
//                    privilege.setModule(Module.BILLER);
//                } else if (url.contains("/nibss")) {
//                    privilege.setModule(Module.NIBSS);
//                } else {
//                    privilege.setModule(Module.REPORT);
//                }
//
//                privileges.add(privilege);
//            }
//
//        });
//
//        //save into the db. Ensure the old is flushed
////        privilegeService.save(privileges);
//
//        return ResponseEntity.ok().body("Success");
 //   }
}