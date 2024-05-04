package ng.upperlink.nibss.cmms.controller.util;

import ng.upperlink.nibss.cmms.dto.AuthorizationRequest;
import ng.upperlink.nibss.cmms.dto.Id;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.biller.ProductRequest;
import ng.upperlink.nibss.cmms.dto.biller.ProductSearchRequest;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationAction;
import ng.upperlink.nibss.cmms.enums.makerchecker.EntityType;
import ng.upperlink.nibss.cmms.enums.makerchecker.InitiatorActions;
import ng.upperlink.nibss.cmms.enums.makerchecker.ViewAction;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.biller.Product;
import ng.upperlink.nibss.cmms.repo.SearchRepo;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.auth.RoleService;
import ng.upperlink.nibss.cmms.service.biller.BillerService;
import ng.upperlink.nibss.cmms.service.biller.BillerUserService;
import ng.upperlink.nibss.cmms.service.biller.ProductService;
import ng.upperlink.nibss.cmms.service.makerchecker.OtherAuthorizationService;
import ng.upperlink.nibss.cmms.util.email.SmtpMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

//@Slf4j
//@RestController
//@RequestMapping("/product")
//@Api(value = "BillersResource")
public class ProductController {
//
//    private static Logger LOG = LoggerFactory.getLogger(BillerController.class);

    private BillerService billerService;
    private UserService userService;
    private BillerUserService billerUserService;
    private SmtpMailSender smtpMailSender;
    private ProductService productService;
    private RoleService roleService;
    private SearchRepo searchRepo;

    @Value("${email_from}")
    private String fromEmail;

    @Value("${encryption.salt}")
    private String salt;

    private UserType userType = UserType.BILLER;
    private OtherAuthorizationService otherAuthorizationService;
    @Autowired
    public void setOtherAuthorizationService(OtherAuthorizationService otherAuthorizationService) {
        this.otherAuthorizationService = otherAuthorizationService;
    }

    @Autowired
    public void setSearchRepo(SearchRepo searchRepo){
        this.searchRepo = searchRepo;
    }



    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }
    @Autowired
    public void setBillerService(BillerService billerService) {
        this.billerService = billerService;
    }
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setSmtpMailSender(SmtpMailSender smtpMailSender)
    {
        this.smtpMailSender = smtpMailSender;
    }

    @GetMapping
    public ResponseEntity getAllByStatusAndBillerId(@RequestParam boolean status, @RequestParam Long billerId , @RequestParam(required = false)int pageNumber, @RequestParam(required = false) int pageSize){

        if(pageNumber!=0 && pageSize !=0){
            return ResponseEntity.ok(productService.getAllActiveProductsByBillerId(new PageRequest(pageNumber,pageSize),billerId,status));
        }else

            return ResponseEntity.ok(productService.getAllActiveProductsByBiller(billerId,status));
    }

//    @GetMapping
//    public ResponseEntity getAllProducts(@RequestParam Long billerId,@RequestParam boolean status,@RequestParam int pageNumber, @RequestParam int pageSize){
//        return ResponseEntity.ok(productService.getAllActiveProductsByBillerId(new PageRequest(pageNumber,pageSize),billerId,status));
//    }


//    @GetMapping
//    public ResponseEntity<?> get(@RequestParam Long billerId, @RequestParam boolean status )
//    {
//        return ResponseEntity.ok(productService.getAllActiveProductsByBiller(status,billerId));
//    }

    @GetMapping("/all")
    public ResponseEntity getAll(@RequestParam Long billerId,@RequestParam int pageNumber, @RequestParam int pageSize)
    {
        return ResponseEntity.ok(productService.getAllProducts(billerId,new PageRequest(pageNumber,pageSize)));
    }

    @GetMapping("/{id}")
    public ResponseEntity getProductById(@PathVariable Long id)
    {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping("/search")
    public ResponseEntity searchProduct(@Valid @RequestBody ProductSearchRequest request, Pageable pageable){
//        boolean flag = StringUtils.isEmpty(request.getActivated()) ? true : false;
//
//        boolean status = Boolean.parseBoolean(request.getActivated());

//        return ResponseEntity.ok(productService.searchProducts(request.getDescription(),request.getProductName(),request.getBillerName(),flag,status,pageable));

        return searchRepo.searchProduct(request.getDescription(),request.getProductName(),request.getBillerName(),request.getActivated(),pageable);
    }

    @PostMapping
    public ResponseEntity<?> addNewProduct(@Valid @RequestBody ProductRequest productRequest,
                                             @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail)
    {
        try {
            return productService.setup(productRequest,userDetail,null, InitiatorActions.CREATE,false);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Creation failed:", Arrays.asList(e.getMessage()),e.getCode());
        }
    }

    @PutMapping
    public ResponseEntity updateProduct(@Valid @RequestBody ProductRequest productRequest,
                                       @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail)
    {
        try {
            return productService.setup(productRequest,userDetail,null,InitiatorActions.UPDATE,true);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Update failed:", Arrays.asList(e.getMessage()),e.getCode());
        }
    }

    @PutMapping("/toggle")
    public ResponseEntity toggleProduct(@Valid @RequestBody Id id,
                                       @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail)
    {
        try {
            return ResponseEntity.ok(productService.toggleInit(id,userDetail));
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Toggle failed:", Arrays.asList(e.getMessage()),e.getCode());
        }
    }

    private String generateDetails(Product product, boolean isUpdate){

        String details = "";

        details += "<strong>Product Name :</strong> " +product.getName()+"<br/>";
        details += "<strong>Biller :</strong> " + product.getBiller().getName() + "<br/>";
        details += "<strong>Product Amount :</strong> " + product.getAmount() + "<br/>";

        return details;
    }
    @GetMapping("/viewAction")
    public ResponseEntity getAllPendingUsere(@RequestParam ViewAction viewAction, @RequestParam int pageNumber, @RequestParam int pageSize,

                                             @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){
        try {
            return ResponseEntity.ok(productService.selectView(userDetail,viewAction,new PageRequest(pageNumber,pageSize)));
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Retrieval failed:", Arrays.asList(e.getMessage()),e.getCode());
        }
    }

    @GetMapping("/previewUpdate")
    public ResponseEntity previewUpdate ( @RequestParam Long id,
                                          @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail)
    {
        try {
            return ResponseEntity.status(200).body(productService.previewUpdate(id));
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Preview failed:", Arrays.asList(e.getMessage()),e.getCode());
        }
    }

    @PutMapping("/authorization")
    public ResponseEntity authorizationAction(@Valid @RequestBody AuthorizationRequest request, @RequestParam AuthorizationAction action,
                                              @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){
        Product bank = null;
        try {
            bank = (Product) otherAuthorizationService.performAuthorization(request, action, userDetail, EntityType.PRODUCT);
            return ResponseEntity.status(200).body(bank);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Authorization failed:", Arrays.asList(e.getMessage()),e.getCode());
        }
    }

}
