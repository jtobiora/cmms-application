package ng.upperlink.nibss.cmms.controller;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.AuthorizationRequest;
import ng.upperlink.nibss.cmms.dto.Id;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.biller.ProductRequest;
import ng.upperlink.nibss.cmms.dto.biller.ProductSearchRequest;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationAction;
import ng.upperlink.nibss.cmms.enums.makerchecker.EntityType;
import ng.upperlink.nibss.cmms.enums.makerchecker.InitiatorActions;
import ng.upperlink.nibss.cmms.enums.makerchecker.ViewAction;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.biller.Product;
import ng.upperlink.nibss.cmms.repo.SearchRepo;
import ng.upperlink.nibss.cmms.service.biller.ProductService;
import ng.upperlink.nibss.cmms.service.makerchecker.OtherAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/product")
@Slf4j
public class ProductController {

    private ProductService productService;
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
    public void setSearchRepo(SearchRepo searchRepo) {
        this.searchRepo = searchRepo;
    }


    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping({"/{pageNumber}/{pageSize}"})
    public ResponseEntity getAllActive(@RequestParam boolean status, @RequestParam Long billerId, @RequestParam Optional<Integer> pageNumber, @RequestParam Optional<Integer> pageSize) {

        try {
            if (pageNumber.isPresent() && pageSize.isPresent()) {
                int pNum = pageNumber.get();
                int pSize = pageSize.get();
                return ResponseEntity.ok(productService.getAllActiveProductsByBillerId(new PageRequest(pNum, pSize), billerId, status));
            } else
                return ResponseEntity.ok(productService.getAllActiveProductsByBiller(billerId, status));
        } catch (Exception e) {
            log.error("Could not fetch products", e);
            return new ResponseEntity<>(new ErrorDetails("Unable to load products."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity getAllProducts(@RequestParam Long billerId, @RequestParam int pageNumber, @RequestParam int pageSize) {
        try {
            return ResponseEntity.ok(productService.getAllProducts(billerId, new PageRequest(pageNumber, pageSize)));
        } catch (Exception e) {
            log.error("Could not load products", e);
            return new ResponseEntity<>(new ErrorDetails("Unable to load products."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.getAllActiveProductsByBiller(true, id));
        } catch (Exception e) {
            log.error("Could not load product with id {} ", id, e);
            return new ResponseEntity<>(new ErrorDetails("Unable to load product details."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity getAll(@RequestParam Long billerId, @RequestParam int pageNumber, @RequestParam int pageSize) {
        try {
            return ResponseEntity.ok(productService.getAllProducts(billerId, new PageRequest(pageNumber, pageSize)));
        } catch (Exception e) {
            log.error("Could not retrieve products ", e);
            return new ResponseEntity<>(new ErrorDetails("Unable to load products."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getProductById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.getProductById(id));
        } catch (Exception e) {
            log.error("Could not load product with id {} ", id, e);
            return new ResponseEntity<>(new ErrorDetails("Unable to load product details."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/search")
    public ResponseEntity searchProduct(@Valid @RequestBody ProductSearchRequest request, Pageable pageable) {
//        boolean flag = StringUtils.isEmpty(request.getActivated()) ? true : false;
//
//        boolean status = Boolean.parseBoolean(request.getActivated());

//        return ResponseEntity.ok(productService.searchProducts(request.getDescription(),request.getProductName(),request.getBillerName(),flag,status,pageable));
        try {
            return searchRepo.searchProduct(request.getDescription(), request.getProductName(), request.getBillerName(), request.getActivated(), pageable);
        } catch (Exception e) {
            log.error("Search could not be completed", e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> addNewProduct(@Valid @RequestBody ProductRequest productRequest,
                                           @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        try {
            return productService.setup(productRequest, userDetail, null, InitiatorActions.CREATE, false);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to create", Arrays.asList(e.getMessage()), e.getCode());
        }catch(Exception e){
            log.error("Unable to create product", e);
            return new ResponseEntity<>(new ErrorDetails("Unable to create product."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity updateProduct(@Valid @RequestBody ProductRequest productRequest,
                                        @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        try {
            return productService.setup(productRequest, userDetail, null, InitiatorActions.UPDATE, true);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to update", Arrays.asList(e.getMessage()), e.getCode());
        }catch (Exception e){
            log.error("Unable to update product", e);
            return new ResponseEntity<>(new ErrorDetails("Unable to update product."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/toggle")
    public ResponseEntity toggleProduct(@Valid @RequestBody Id id,
                                        @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        try {
            return ResponseEntity.ok(productService.toggleInit(id, userDetail));
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to toggle", Arrays.asList(e.getMessage()), e.getCode());
        }catch (Exception e){
            log.error("Unable to toggle product", e);
            return new ResponseEntity<>(new ErrorDetails("Unable to toggle product."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String generateDetails(Product product, boolean isUpdate) {

        String details = "";

        details += "<strong>Product Name :</strong> " + product.getName() + "<br/>";
        details += "<strong>Biller :</strong> " + product.getBiller().getName() + "<br/>";
        details += "<strong>Product Amount :</strong> " + product.getAmount() + "<br/>";

        return details;
    }

    @GetMapping("/viewAction")
    public ResponseEntity getAllPendingUsere(@RequestParam ViewAction viewAction, @RequestParam int pageNumber, @RequestParam int pageSize,

                                             @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        try {
            return ResponseEntity.ok(productService.selectView(userDetail, viewAction, new PageRequest(pageNumber, pageSize)));
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("View failed", Arrays.asList(e.getMessage()), e.getCode());
        }catch (Exception e){
            log.error("Unable to complete request processing.", e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/previewUpdate")
    public ResponseEntity previewUpdate(@RequestParam Long id,
                                        @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        try {
            return ResponseEntity.status(201).body(productService.previewUpdate(id));
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Unable to preview", Arrays.asList(e.getMessage()), e.getCode());
        }catch (Exception e){
            log.error("Request processing could not be completed.", e);
            return new ResponseEntity<>(new ErrorDetails(Errors.REQUEST_TERMINATED.getValue()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/authorization")
    public ResponseEntity authorizationAction(@Valid @RequestBody AuthorizationRequest request, @RequestParam AuthorizationAction action,
                                              @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        Product product = null;
        try {
            product = (Product) otherAuthorizationService.performAuthorization(request, action, userDetail, EntityType.PRODUCT);
            return ResponseEntity.status(200).body(product);
        } catch (CMMSException e) {
            e.printStackTrace();
            return ErrorDetails.setUpErrors("Authorization failed:", Arrays.asList(e.getMessage()), e.getCode());
        }catch (Exception e){
            log.error("Authorization failed.", e);
            return new ResponseEntity<>(new ErrorDetails("Authorization failed."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
