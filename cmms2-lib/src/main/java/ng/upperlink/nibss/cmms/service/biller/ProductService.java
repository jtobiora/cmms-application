package ng.upperlink.nibss.cmms.service.biller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ng.upperlink.nibss.cmms.dto.Id;
import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.biller.ProductRequest;
import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;
import ng.upperlink.nibss.cmms.enums.makerchecker.*;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.auth.Role;
import ng.upperlink.nibss.cmms.model.bank.BankUser;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.biller.BillerUser;
import ng.upperlink.nibss.cmms.model.biller.Product;
import ng.upperlink.nibss.cmms.repo.biller.ProductRepo;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.bank.BankUserService;
import ng.upperlink.nibss.cmms.service.makerchecker.OtherAuthorizationService;
import ng.upperlink.nibss.cmms.service.makerchecker.UserAuthorizationService;
import ng.upperlink.nibss.cmms.service.nibss.NibssUserService;
import ng.upperlink.nibss.cmms.util.emandate.JsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;


@Service
public class ProductService {
    private static Logger logger = LoggerFactory.getLogger(ProductService.class);

    private ProductRepo productRepo;
    private BankUserService bankUserService;
    private BillerUserService billerUserService;
    private BillerService billerService;
    private NibssUserService nibssUserService;
    private UserService userService;

    private OtherAuthorizationService otherAuthorizationService;

    @Autowired
    public void setOtherAuthorizationService(OtherAuthorizationService otherAuthorizationService) {
        this.otherAuthorizationService = otherAuthorizationService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setNibssUserService(NibssUserService nibssUserService) {
        this.nibssUserService = nibssUserService;
    }

    @Autowired
    public void setProductRepo(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Autowired
    public void setBankUserService(BankUserService bankUserService) {
        this.bankUserService = bankUserService;
    }

    @Autowired
    public void setBillerUserService(BillerUserService billerUserService) {
        this.billerUserService = billerUserService;
    }

    @Autowired
    public void setBillerService(BillerService billerService) {
        this.billerService = billerService;
    }

    public Page<Product> getAllActiveProductsByBillerId(Pageable pageable, Long billerId, boolean status) {
        return productRepo.getAllActiveProductsByBillerId(pageable, billerId, status, AuthorizationStatus.CREATION_REJECTED);

    }

    public List<Product> getAllActiveProductsByBiller(Long billerId, boolean status) {
        return productRepo.getAllActiveProductsByBillerId(billerId, status, AuthorizationStatus.CREATION_REJECTED);
    }

    public Page<Product> getAllProducts(Long billerId, Pageable pageable) {
        return productRepo.getAllProductsByBiller(billerId, pageable, AuthorizationStatus.CREATION_REJECTED);
    }

    public List<Product> getAllActiveProductsByBiller(boolean status, Long id) {

        return productRepo.getAllActiveProductsByBillerId(id, status, AuthorizationStatus.CREATION_REJECTED);
    }

    public Page<Product> getAllProductsByBiller(Long id, Pageable pageable) {
        return productRepo.getAllProductsByBiller(id, pageable, AuthorizationStatus.CREATION_REJECTED);
    }

    public Product getProductById(Long id) {
        return productRepo.getProductById(id);
    }

    public Product getProductsByName(String name) {
        return productRepo.getProductsByName(name);
    }

    public Product getByProductNameAndNotId(String name, Long id) {
        return productRepo.getByProductNameAndNotId(name, id);
    }

    public Product getProductsByBillerIdAndProductId(Long billerId, Long productId) {
        return productRepo.getProductsByBillerIdAndProductId(billerId, productId);
    }

    public Product updateProducts(Product product) {
        return save(product);
    }

    public Product toggle(Long id, BillerUser billerUser) {
        Product productToBeToggled = getProductById(id);
        if (productToBeToggled == null) {
            return new Product();
        }
        boolean activated = productToBeToggled.isActivated();
        productToBeToggled.setActivated(!activated);
        productToBeToggled.setUpdatedBy(userService.setUser(billerUser));
        return productRepo.save(productToBeToggled);

    }

    public Product save(Product product) {
        return productRepo.save(product);
    }

    public Product generate(Product newProduct, Product existingProduct, ProductRequest productRequest, User operator, boolean isUpdate) throws CMMSException {
        newProduct = generateUpdate(newProduct, productRequest, operator);
        if (isUpdate) {
//            newProduct.setUpdatedAt(new Date());
            newProduct.setUpdatedBy(userService.setUser(operator));
            String jsonData = null;
            try {
                jsonData = JsonBuilder.generateJson(newProduct);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                logger.error("Error track ----- ",e);
                throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(), "500", "500");
            }
            return (Product) otherAuthorizationService.actions(jsonData, existingProduct, operator, null,
                    InitiatorActions.UPDATE, null, EntityType.PRODUCT);
        } else {
            newProduct.setCreatedBy(new User(operator.getId(), operator.getName(), operator.getEmailAddress()));
//            newProduct.setCreatedAt(new Date());
            newProduct.setActivated(false);
            return (Product) otherAuthorizationService.actions(null, newProduct, operator, null,
                    InitiatorActions.CREATE, null, EntityType.PRODUCT);
        }
    }

    public Product generateUpdate(Product product, ProductRequest productRequest, User operator) {
        product.setName(productRequest.getName());
        product.setAmount(productRequest.getAmount());
        product.setDescription(productRequest.getDescription());
        return product;
    }

    public Product generateApproved(Product product, Product fromJson, User operator) {
        product.setName(fromJson.getName());
        product.setAmount(fromJson.getAmount());
        product.setDescription(fromJson.getDescription());
//        product.setUpdatedAt(fromJson.getUpdatedAt());
        product.setUpdatedBy(fromJson.getUpdatedBy());

        return product;
    }

    public ResponseEntity<?> setup(ProductRequest productRequest, UserDetail userDetail, AuthorizationAction action, InitiatorActions initiatorActions, boolean isUpdate) throws CMMSException {

        User operator = userService.get(userDetail.getUserId());

        if (operator == null) {
            throw new CMMSException("Please login and try again", "401", "401");
        }
        Product newProduct = new Product();
        if (isUpdate) {
            authenticate(operator, action, initiatorActions, newProduct, isUpdate);
            Product existingProduct = getProductById(productRequest.getId());

            if (existingProduct == null) {
                throw new CMMSException("No product available for Update", "401", "401");
            }
            existingProduct = generate(newProduct, existingProduct, productRequest, operator, isUpdate);
            return ResponseEntity.ok(save(existingProduct));
        } else {
            authenticate(operator, action, initiatorActions, newProduct, isUpdate);
            newProduct = generate(newProduct, null, productRequest, operator, isUpdate);
            return ResponseEntity.ok(save(newProduct));
        }

    }

    public void authenticate(User operator, AuthorizationAction action, InitiatorActions initiatorActions, Product newProduct, boolean isUpdate) throws CMMSException {
        Collection<Role> roles = operator.getRoles();
        if (roles == null) {
            throw new CMMSException("No role is attached to this user", "404", "404");
        }
        Role operatorRole = roles.stream().findAny().get();
        RoleName[] initiatorRoles = {RoleName.NIBSS_INITIATOR, RoleName.BANK_ADMIN_INITIATOR, RoleName.BILLER_ADMIN_INITIATOR, RoleName.PSSP_ADMIN_INITIATOR};
        RoleName[] authorizerdRoles = {RoleName.NIBSS_AUTHORIZER, RoleName.BANK_ADMIN_AUTHORIZER, RoleName.BILLER_ADMIN_AUTHORIZER, RoleName.PSSP_ADMIN_AUTHORIZER};
        if (!Arrays.asList(initiatorRoles).contains(operatorRole.getName()) && !Arrays.asList(authorizerdRoles).contains(operatorRole.getName())) {
            throw new CMMSException("Not permitted to act on  products", "401", "401");
        }
        if (Arrays.asList(UserAuthorizationService.INITIATE_ACTIONS).contains(initiatorActions)
                && !Arrays.asList(initiatorRoles).contains(operatorRole.getName())) {
            throw new CMMSException("Not permitted to create or toggle products", "401", "401");
        }
        if (Arrays.asList(UserAuthorizationService.APPROVE_ACTIONS).contains(action) ||
                Arrays.asList(UserAuthorizationService.REJECT_ACTIONS).contains(action)) {
            if (!Arrays.asList(authorizerdRoles).contains(operatorRole.getName())) {
                throw new CMMSException("Not permitted to authorize or reject products", "401", "401");
            }
        }
        authorizeUser(newProduct, operator, isUpdate);
    }

    public void authorizeUser(Product newProduct, User operator, boolean isUpdate) throws CMMSException {
        Biller productBiller = null;
        switch (operator.getUserType()) {
            case BILLER:
                BillerUser billerOperator = (BillerUser) operator;
                productBiller = billerService.getBillerById(billerOperator.getBiller().getId());
                if (productBiller == null) {
                    throw new CMMSException("Biller not found for the log in user", "500", "500");
                }
                break;

            case BANK:
                BankUser bankOprator = (BankUser) operator;
                productBiller = billerService.getBillerByBankAsBiller(bankOprator.getUserBank().getId());

                //TODO Handle the null pointer that arises from the line 243
                if (bankOprator.getUserBank().getId() != productBiller.getBankAsBiller().getId()) {
                    throw new CMMSException("You are not authorized to create products for other billers", "401", "401");

                }
                break;
            case NIBSS:
//                newBiller = billerService.getBillerByOwner(BillerOwner.NIBSS.getValue());
//                if (newBiller.getBillerOwner() != BillerOwner.NIBSS.getValue())
//                {
                throw new CMMSException("You are not authorized to create products for other billers", "401", "401");
//                }
//                break;
        }
        if (productBiller == null) {
            throw new CMMSException("No biller is found: Cannot tie this product to any biller ", "404", "404");
        }
        if (!isUpdate) {
            newProduct.setBiller(new Biller(productBiller.getId(), productBiller.getName(), productBiller.getRcNumber(), productBiller.getApiKey(),
                    productBiller.getBillerOwner()));
        }
    }

//    public Product generate (Product product, ProductRequest productRequest, User operator, boolean isUpdate, Biller biller){
//        product.setName(productRequest.getName());
//        product.setAmount(productRequest.getAmount());
//        product.setDescription(productRequest.getDescription());
//        if (isUpdate){
//            product.setUpdatedDate(new Date());
//            product.setUpdateddBy(new User(operator.getId(), operator.getName(), operator.getEmailAddress()));
//        }else {
//
//            product.setBiller(biller);
//            product.setCreatedBy(new User(operator.getId(), operator.getName(), operator.getEmailAddress()));
//            product.setCreatedDate(new Date());
//            product.setStatus(true);
//        }
//        return  product;
//    }

    public void deleteProduct(Product product) {
        if (product != null) {
            productRepo.delete(product);
        }
    }

    public void deleteProductById(Long id) {
        if (id != null) {
            productRepo.delete(id);
        }
    }

    public Page<Product> searchProducts(String description, String productName, String billerName, boolean flag, boolean activated, Pageable pageable) {

        return flag ? this.productRepo.searchProducts(productName, description, billerName, pageable)
                : this.productRepo.searchProductsByStatusInclusive(productName, description, billerName, activated, pageable);
    }

    public Page<Product> selectView(UserDetail userDetail, ViewAction viewAction, Pageable pageable) throws CMMSException {
        User operator = userService.get(userDetail.getUserId());
        Long billerId = null;
        if (operator == null) {
            throw new CMMSException("Please Login and try again", "401", "401");
        }
        switch (operator.getUserType()) {
//            case NIBSS: billerOwner = nibssId;
//                break;
            case BANK:
                BankUser bankUser = (BankUser) operator;
                Long bankId = bankUser.getUserBank().getId();
                Biller billerByBankAsBiller = billerService.getBillerByBankAsBiller(bankId);
                if (billerByBankAsBiller == null) {
                    throw new CMMSException("Sorry you can't view products", "401", "401");
                }
                billerId = billerByBankAsBiller.getId();
                break;
//            case PSSP:
//                PsspUser psspUser = (PsspUser)operator;
//                billerOwner = psspUser.getPssp().getApiKey();
//                break;
            default:
                throw new CMMSException("Unknown user type found", "400", "400");
        }
        if (billerId == null) {
            throw new CMMSException("Biller owner is not found", "404", "404");
        }
        switch (viewAction) {
            case UNAUTHORIZED:
                return this.getAllPending(billerId, pageable);
            case AUTHORIZED:
                return getAllApproved(billerId, AuthorizationStatus.AUTHORIZED, pageable);
            case REJECTED:
                return getAllRejected(billerId, Arrays.asList(UserAuthorizationService.rejectionStatuses), pageable);
            default:
                return getAllApproved(billerId, AuthorizationStatus.AUTHORIZED, pageable);
        }
    }

    private Page<Product> getAllPending(Long id, Pageable pageable) {
        return this.productRepo.getAllPending(id, Arrays.asList(UserAuthorizationService.pendingActions), pageable);
    }

    private Page<Product> getAllApproved(Long id, AuthorizationStatus authStatus, Pageable pageable) {
        return productRepo.getAllApproved(id, authStatus, pageable);
    }

    private Page<Product> getAllRejected(Long id, List<AuthorizationStatus> authStatusList, Pageable pageable) {
        return productRepo.getAllRejected(id, authStatusList, pageable);
    }

    public Product previewUpdate(Long id) throws CMMSException {
        Product fromJson = productRepo.previewUpdate(id, AuthorizationStatus.UNAUTHORIZED_UPDATE);
        if (fromJson == null) {
            throw new CMMSException(Errors.NO_UPDATE_REQUEST.getValue(), "404", "404");
        }
        String jsonData = fromJson.getJsonData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Product jsonUser = mapper.readValue(jsonData, Product.class);
            return jsonUser;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error track ----",e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(), "500", "500");
        }
    }

    public Product toggleInit(Id id, UserDetail userDetail) throws CMMSException {
        User operator = userService.get(userDetail.getUserId());
        if (operator == null) {
            throw new CMMSException("Details not found. Please login and try agin", "404", "404");
        }
        Product productToBeToggled = this.getProductById(id.getId());
        if (productToBeToggled == null) {
            throw new CMMSException("No product found for toggling ", "404", "404");
        }
        authenticate(operator, null, InitiatorActions.TOGGLE, null, true);
        productToBeToggled = (Product) otherAuthorizationService.actions(null, productToBeToggled, operator, null, InitiatorActions.TOGGLE, null, EntityType.PRODUCT);
        return save(productToBeToggled);
    }

    long countAllByBillerId(Long id) {
        return productRepo.countAllByBillerId(id);
    }

    long countAllByBillerIdAndStatus(boolean status, Long id) {
        return productRepo.countAllByBillerIdAndStatus(status, id);
    }

    long countAll(Long id) {
        return productRepo.countAll(id);
    }

    long countAllByStatus(boolean status) {
        return productRepo.countAllByStatus(status);
    }
}

