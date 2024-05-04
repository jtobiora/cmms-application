package ng.upperlink.nibss.cmms.repo;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.enums.Channel;
import ng.upperlink.nibss.cmms.enums.FeeBearer;
import ng.upperlink.nibss.cmms.enums.MandateCategory;
import ng.upperlink.nibss.cmms.enums.MandateRequestType;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.model.NibssUser;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.bank.BankUser;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.biller.BillerUser;
import ng.upperlink.nibss.cmms.model.biller.Product;
import ng.upperlink.nibss.cmms.model.mandate.Beneficiary;
import ng.upperlink.nibss.cmms.model.mandate.Fee;
import ng.upperlink.nibss.cmms.model.mandate.Mandate;
import ng.upperlink.nibss.cmms.model.pssp.Pssp;
import ng.upperlink.nibss.cmms.model.pssp.PsspUser;
import ng.upperlink.nibss.cmms.util.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class SearchRepo {
    @PersistenceContext
    private EntityManager entityManager;


    //######### BENEFICIARY SEARCH ############################
    public ResponseEntity searchBeneficiary(String beneficiaryName,String accNumber,String accName,
                                            String activated,Pageable pageable){

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Beneficiary> cq = cb.createQuery(Beneficiary.class);
        Root<Beneficiary> root = cq.from(Beneficiary.class);

        //create a base predicate to hold all the queries
        Predicate predicate = cb.like(root.get("beneficiaryName"), "%%");

        if(!StringUtils.isEmpty(beneficiaryName)){
            predicate = cb.and(predicate,cb.like(root.get("beneficiaryName"), "%" + beneficiaryName + "%"));
        }

        if(!StringUtils.isEmpty(accNumber)){
            predicate = cb.and(predicate,cb.like(root.get("accountNumber"), "%" + accNumber + "%"));
        }

        if(!StringUtils.isEmpty(accName)){
            predicate = cb.and(predicate,cb.like(root.get("accountName"), "%" + accName + "%"));
        }

        if(!StringUtils.isEmpty(activated)){
            boolean status = Boolean.parseBoolean(activated);
            predicate = cb.and(predicate,cb.equal(root.get("activated"), status));
        }

        cq.where(predicate);

        TypedQuery<Beneficiary> query = entityManager.createQuery(cq);

        List<Beneficiary> beneficiaries = query.getResultList();

        Page<Beneficiary> response = new PageImpl<>(beneficiaries, pageable, beneficiaries.size());

        return ResponseEntity.ok().body(response);

    }


    //######### PSSP_USER SEARCH ############################
    public ResponseEntity searchPSSPUser(String roleDesc,String email,String firstName,String middleName,
                                             String lastName,String phoneNumber,String city,String staffNumber,
                                             String activated,String createdAt,Pageable pageable){


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<PsspUser> cq = cb.createQuery(PsspUser.class);
        Root<PsspUser> root = cq.from(PsspUser.class);

        //create a base predicate to hold all the queries
        Predicate predicate = cb.like(root.get("staffNumber"), "%%");

        Join name = root.join("name");
        if(!StringUtils.isEmpty(firstName)){
            predicate = cb.and(predicate,cb.like(name.get("firstName"), "%" + firstName + "%"));
        }

        Join contactdetails = root.join("contactDetails");
        if(!StringUtils.isEmpty(city)){
            predicate = cb.and(predicate,cb.like(contactdetails.get("city"), "%" + city + "%"));
        }

        if(!StringUtils.isEmpty(middleName)){
            predicate = cb.and(predicate,cb.like(name.get("middleName"), "%" + middleName + "%"));
        }

        //role
        Join role = root.join("roles");
        if(!StringUtils.isEmpty(roleDesc)){
            predicate = cb.and(predicate,cb.like(role.get("description"), "%" + roleDesc + "%"));
        }

        if(!StringUtils.isEmpty(email)){
            predicate = cb.and(predicate,cb.like(root.get("emailAddress"), "%" + email + "%"));
        }


        if(!StringUtils.isEmpty(lastName)){
            predicate = cb.and(predicate,cb.like(name.get("lastName"), "%" + lastName + "%"));
        }

        if(!StringUtils.isEmpty(phoneNumber)){
            predicate = cb.and(predicate,cb.like(root.get("phoneNumber"), "%" + phoneNumber + "%"));
        }

        if(!StringUtils.isEmpty(staffNumber)){
            predicate = cb.and(predicate,cb.like(root.get("staffNumber"), "%" + staffNumber + "%"));
        }

        if(!StringUtils.isEmpty(createdAt)){
            try{

                SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
                Date from  = CommonUtils.startOfDay(dateFormat.parse(createdAt));
                Date to = CommonUtils.endOfDay(dateFormat.parse(createdAt));
                predicate = cb.and(predicate,cb.between(root.get("createdAt"), from,to));
            }catch(ParseException ex){
                log.error("Error thrown while searching using date created ",ex.getMessage());
            }
        }

//        if(!StringUtils.isEmpty(createdAt)){
//            try{
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                Date dateCreated = dateFormat.parse(createdAt);
//                predicate = cb.and(predicate,cb.equal(root.get("createdAt"), dateCreated));
//            }catch(ParseException ex){
//                log.error("Error thrown while searching {}",ex.getMessage());
//            }
//        }

        if(!StringUtils.isEmpty(activated)){
            boolean status = Boolean.parseBoolean(activated);
            predicate = cb.and(predicate,cb.equal(root.get("activated"), status));
        }


        cq.where(predicate);

        TypedQuery<PsspUser> query = entityManager.createQuery(cq);

        List<PsspUser> psspUserList = query.getResultList();

        Page<PsspUser> response = new PageImpl<>(psspUserList, pageable, psspUserList.size());

        return ResponseEntity.ok().body(response);

    }

    //######### PSSP SEARCH ############################
    public ResponseEntity searchPSSP(String accountNumber, String accountName, String psspCode,
                                     String psspName, String activated, String bvn,String bankCode,Pageable pageable){

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Pssp> cq = cb.createQuery(Pssp.class);
        Root<Pssp> root = cq.from(Pssp.class);

        //create a base predicate to hold all the queries
        Predicate predicate = cb.like(root.get("psspName"), "%%");

        if(!StringUtils.isEmpty(psspName)){
            predicate = cb.and(predicate,cb.like(root.get("psspName"), "%" + psspName + "%"));
        }

        if(!StringUtils.isEmpty(accountNumber)){
            predicate = cb.and(predicate,cb.like(root.get("accountNumber"), "%" + accountNumber + "%"));
        }

        if(!StringUtils.isEmpty(accountName)){
            predicate = cb.and(predicate,cb.like(root.get("accountName"), "%" + accountName + "%"));
        }

        if(!StringUtils.isEmpty(psspCode)){
            predicate = cb.and(predicate,cb.like(root.get("psspCode"), "%" + psspCode + "%"));
        }

        if(!StringUtils.isEmpty(bvn)){
            predicate = cb.and(predicate,cb.like(root.get("bvn"), "%" + bvn + "%"));
        }

        if(!StringUtils.isEmpty(activated)){
            boolean status = Boolean.parseBoolean(activated);
            predicate = cb.and(predicate,cb.equal(root.get("activated"), status));
        }

        //bank code
        Join code = root.join("bank");
        if(!StringUtils.isEmpty(bankCode)){
            predicate = cb.and(predicate,cb.like(code.get("code"), "%" + bankCode + "%"));
        }

        cq.where(predicate);

        TypedQuery<Pssp> query = entityManager.createQuery(cq);

        List<Pssp> psspList = query.getResultList();

        Page<Pssp> response = new PageImpl<>(psspList, pageable, psspList.size());

        return ResponseEntity.ok().body(response);

    }

    //######### FEE SEARCH ############################
    public ResponseEntity searchFees(String markUpFee,String feeBearer,
                                String billerAccNumber,String billerName,String debitAtTrans,
                                     String markUpFeeSelected,String beneficiaryBankCode,Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Fee> cq = cb.createQuery(Fee.class);
        Root<Fee> root = cq.from(Fee.class);

        Predicate predicate = cb.like(root.get("billerDebitAccountNumber"), "%%");

        if(!StringUtils.isEmpty(billerAccNumber)){
            predicate = cb.and(predicate,cb.like(root.get("billerDebitAccountNumber"), "%" + billerAccNumber + "%"));
        }

        if(!StringUtils.isEmpty(markUpFee)){
            BigDecimal fee = null;
            try {
                fee = new BigDecimal(markUpFee);
                predicate = cb.and(predicate,cb.equal(root.get("markUpFee"), fee));
            } catch (NumberFormatException ex) {
                return ResponseEntity.badRequest().body(new ErrorDetails("MarkUp Fee must not contain characters!"));
            }
        }

        if (!StringUtils.isEmpty(feeBearer)) {
            FeeBearer fb = FeeBearer.find(feeBearer);
            predicate = cb.and(predicate,cb.equal(root.get("feeBearer"), fb));
        }

        Join biller = root.join("biller");
        if(!StringUtils.isEmpty(billerName)) {
            predicate = cb.and(predicate,cb.like(biller.get("name"), "%" + billerName+ "%"));
        }

        if(!StringUtils.isEmpty(debitAtTrans)){
            boolean debitTrans = Boolean.parseBoolean(debitAtTrans);
            predicate = cb.and(predicate,cb.equal(root.get("isDebitAtTransactionTime"), debitTrans));
        }

        if(!StringUtils.isEmpty(markUpFeeSelected)){
            boolean feeSelected = Boolean.parseBoolean(markUpFeeSelected);
            predicate = cb.and(predicate,cb.equal(root.get("markedUp"), feeSelected));
        }

        //bank code
        Join code = root.join("bank");
        if(!StringUtils.isEmpty(beneficiaryBankCode)){
            predicate = cb.and(predicate,cb.like(code.get("code"), "%" + beneficiaryBankCode + "%"));
        }

        cq.where(predicate);

        TypedQuery<Fee> query = entityManager.createQuery(cq);

        List<Fee> fees = query.getResultList();

        Page<Fee> response = new PageImpl<>(fees, pageable, fees.size());

        return ResponseEntity.ok().body(response);
    }


    //############# MANDATE SEARCH #########################
    public ResponseEntity searchMandates(String mandateCode,String mandateStartDate,String mandateEndDate,String mandateStatus,
                                         String subscriberCode,String accName,String accNumber,String bvn,String email,String bankCode,
                                         String productName,String mandateType,String mandateCategory,String channel,String payerAddress,
                                         String payerName,String amount,String frequency,Pageable pageable) throws ParseException {

        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Mandate> cq = cb.createQuery(Mandate.class);
        Root<Mandate> root = cq.from(Mandate.class);

        Predicate predicate = cb.like(root.get("payerAddress"), "%%");

        //mandateCode
        if(!StringUtils.isEmpty(mandateCode)){
            predicate = cb.and(predicate,cb.like(root.get("mandateCode"), "%" + mandateCode + "%"));
        }

        //mandate start date
        if(!StringUtils.isEmpty(mandateStartDate)){
            Date startDate = sdf.parse(mandateStartDate);
            predicate = cb.and(predicate,cb.equal(root.get("startDate"), startDate));
        }

        //mandate end date
        if(!StringUtils.isEmpty(mandateEndDate)){
            Date endDate = sdf.parse(mandateEndDate);
            predicate = cb.and(predicate,cb.equal(root.get("endDate"), endDate));
        }

        //mandate status
        Join status = root.join("status");
        if(!StringUtils.isEmpty(mandateStatus)){
            predicate = cb.and(predicate,cb.like(status.get("name"), "%" + mandateStatus+ "%"));
        }

        //subscriber code
        if(!StringUtils.isEmpty(subscriberCode)){
            predicate = cb.and(predicate,cb.like(root.get("subscriberCode"), "%" + subscriberCode + "%"));
        }

        //account Number
        if(!StringUtils.isEmpty(accNumber)){
            predicate = cb.and(predicate,cb.like(root.get("accountNumber"), "%" + accNumber + "%"));
        }

        //account Name
        if(!StringUtils.isEmpty(accName)){
            predicate = cb.and(predicate,cb.like(root.get("accountName"), "%" + accName + "%"));
        }

        //bvn
        if(!StringUtils.isEmpty(bvn)){
            predicate = cb.and(predicate,cb.like(root.get("bvn"), "%" + bvn + "%"));
        }

        //email
        if(!StringUtils.isEmpty(email)){
            predicate = cb.and(predicate,cb.like(root.get("email"), "%" + email + "%"));
        }

        //bank code
        Join code = root.join("bank");
        if(!StringUtils.isEmpty(bankCode)){
            predicate = cb.and(predicate,cb.like(code.get("code"), "%" + bankCode + "%"));
        }

        //product name
        Join product = root.join("product");
        if(!StringUtils.isEmpty(productName)){
            predicate = cb.and(predicate,cb.like(product.get("name"), "%" + productName + "%"));
        }

        //mandate type
        if(!StringUtils.isEmpty(mandateType)){
            MandateRequestType mType = MandateRequestType.find(mandateType);
            predicate = cb.and(predicate,cb.equal(root.get("mandateType"), mType ));
        }

        //mandate category
        if(!StringUtils.isEmpty(mandateCategory)){
            MandateCategory mCategory = MandateCategory.find(mandateCategory);
            predicate =cb.and(predicate,cb.equal(root.get("mandateCategory"),  mCategory));
        }

        //channel
        if(!StringUtils.isEmpty(channel)){
            Channel ch = Channel.find(channel);
            predicate =cb.and(predicate,cb.equal(root.get("channel"),  ch));
        }

        //payer address
        if(!StringUtils.isEmpty(payerAddress)){
            predicate =cb.and(predicate,cb.like(root.get("payerAddress"), "%" + payerAddress + "%"));
        }

        //payer name
        if(!StringUtils.isEmpty(payerName)){
            predicate =cb.and(predicate,cb.like(root.get("payerName"), "%" + payerName + "%"));
        }

        //amount
        if(!StringUtils.isEmpty(amount)){
            BigDecimal amt = null;
            try {
                amt = new BigDecimal(amount);
                predicate = cb.and(predicate,cb.equal(root.get("amount"), amt));
            } catch (NumberFormatException ex) {
                return ResponseEntity.badRequest().body(new ErrorDetails("Amount must not contain characters!"));
            }
        }

        //frequency
        if(!StringUtils.isEmpty(frequency)){
            predicate =cb.and(predicate,cb.like(root.get("scheduleTime"), "%" + frequency + "%"));
        }

        cq.where(predicate);

        TypedQuery<Mandate> query = entityManager.createQuery(cq);

        List<Mandate> mandatesList = query.getResultList();

        Page<Mandate> response = new PageImpl<>(mandatesList, pageable, mandatesList.size());

        return ResponseEntity.ok().body(response);
    }

    //######### BILLER_USER SEARCH ############################
    public ResponseEntity searchBillerUser(String roleDesc,String email,String firstName,String middleName,
                                         String lastName,String phoneNumber,String city,String staffNumber,
                                         String activated,String createdAt,Pageable pageable){


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<BillerUser> cq = cb.createQuery(BillerUser.class);
        Root<BillerUser> root = cq.from(BillerUser.class);

        //create a base predicate to hold all the queries
        Predicate predicate = cb.like(root.get("staffNumber"), "%%");

        Join name = root.join("name");
        if(!StringUtils.isEmpty(firstName)){
            predicate = cb.and(predicate,cb.like(name.get("firstName"), "%" + firstName + "%"));
        }

        Join contactdetails = root.join("contactDetails");
        if(!StringUtils.isEmpty(city)){
            predicate = cb.and(predicate,cb.like(contactdetails.get("city"), "%" + city + "%"));
        }

        if(!StringUtils.isEmpty(middleName)){
            predicate = cb.and(predicate,cb.like(name.get("middleName"), "%" + middleName + "%"));
        }

        //role
        Join role = root.join("roles");
        if(!StringUtils.isEmpty(roleDesc)){
            predicate = cb.and(predicate,cb.like(role.get("description"), "%" + roleDesc + "%"));
        }

        if(!StringUtils.isEmpty(email)){
            predicate = cb.and(predicate,cb.like(root.get("emailAddress"), "%" + email + "%"));
        }


        if(!StringUtils.isEmpty(lastName)){
            predicate = cb.and(predicate,cb.like(name.get("lastName"), "%" + lastName + "%"));
        }

        if(!StringUtils.isEmpty(phoneNumber)){
            predicate = cb.and(predicate,cb.like(root.get("phoneNumber"), "%" + phoneNumber + "%"));
        }

        if(!StringUtils.isEmpty(staffNumber)){
            predicate = cb.and(predicate,cb.like(root.get("staffNumber"), "%" + staffNumber + "%"));
        }

        if(!StringUtils.isEmpty(createdAt)){
            try{
                SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
                Date from  = CommonUtils.startOfDay(dateFormat.parse(createdAt));
                Date to = CommonUtils.endOfDay(dateFormat.parse(createdAt));
                predicate = cb.and(predicate,cb.between(root.get("createdAt"), from,to));
            }catch(ParseException ex){
                log.error("Error thrown while searching using date created ",ex.getMessage());
            }
        }

        if(!StringUtils.isEmpty(activated)){
            boolean status = Boolean.parseBoolean(activated);
            predicate = cb.and(predicate,cb.equal(root.get("activated"), status));
        }


        cq.where(predicate);

        TypedQuery<BillerUser> query = entityManager.createQuery(cq);

        List<BillerUser> billerUserList = query.getResultList();

        Page<BillerUser> response = new PageImpl<>(billerUserList, pageable, billerUserList.size());

        return ResponseEntity.ok().body(response);

    }

    //######### BANK_USER SEARCH ############################
    public ResponseEntity searchBankUser(String roleDesc,String email,String firstName,String middleName,
                                         String lastName,String phoneNumber,String city,String staffNumber,
                                         String activated,String createdAt,Pageable pageable){


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<BankUser> cq = cb.createQuery(BankUser.class);
        Root<BankUser> root = cq.from(BankUser.class);

        //create a base predicate to hold all the queries
        Predicate predicate = cb.like(root.get("staffNumber"), "%%");

        Join name = root.join("name");
        if(!StringUtils.isEmpty(firstName)){
            predicate = cb.and(predicate,cb.like(name.get("firstName"), "%" + firstName + "%"));
        }

        Join contactdetails = root.join("contactDetails");
        if(!StringUtils.isEmpty(city)){
            predicate = cb.and(predicate,cb.like(contactdetails.get("city"), "%" + city + "%"));
        }

        if(!StringUtils.isEmpty(middleName)){
            predicate = cb.and(predicate,cb.like(name.get("middleName"), "%" + middleName + "%"));
        }

        //role
        Join role = root.join("roles");
        if(!StringUtils.isEmpty(roleDesc)){
            predicate = cb.and(predicate,cb.like(role.get("description"), "%" + roleDesc + "%"));
        }

        if(!StringUtils.isEmpty(email)){
            predicate = cb.and(predicate,cb.like(root.get("emailAddress"), "%" + email + "%"));
        }


        if(!StringUtils.isEmpty(lastName)){
            predicate = cb.and(predicate,cb.like(name.get("lastName"), "%" + lastName + "%"));
        }

        if(!StringUtils.isEmpty(phoneNumber)){
            predicate = cb.and(predicate,cb.like(root.get("phoneNumber"), "%" + phoneNumber + "%"));
        }

        if(!StringUtils.isEmpty(staffNumber)){
            predicate = cb.and(predicate,cb.like(root.get("staffNumber"), "%" + staffNumber + "%"));
        }

//        if(!StringUtils.isEmpty(createdAt)){
//            try{
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                Date dateCreated = dateFormat.parse(createdAt);
//                predicate = cb.and(predicate,cb.equal(root.get("createdAt"), dateCreated));
//            }catch(ParseException ex){
//                log.error("Error thrown while searching using date created ",ex.getMessage());
//            }
//        }

        if(!StringUtils.isEmpty(createdAt)){
            try{
                SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
                Date from  = CommonUtils.startOfDay(dateFormat.parse(createdAt));
                Date to = CommonUtils.endOfDay(dateFormat.parse(createdAt));
                predicate = cb.and(predicate,cb.between(root.get("createdAt"), from,to));
            }catch(ParseException ex){
                log.error("Error thrown while searching using date created ",ex.getMessage());
            }
        }

        if(!StringUtils.isEmpty(activated)){
            boolean status = Boolean.parseBoolean(activated);
            predicate = cb.and(predicate,cb.equal(root.get("activated"), status));
        }


        cq.where(predicate);

        TypedQuery<BankUser> query = entityManager.createQuery(cq);

        List<BankUser> bankUserList = query.getResultList();

        Page<BankUser> response = new PageImpl<>(bankUserList, pageable, bankUserList.size());

        return ResponseEntity.ok().body(response);

    }

    //######### NIBSS_USER SEARCH ############################
    public ResponseEntity searchNIBBSUser(String roleDesc,String email,String firstName,String middleName,
                                          String lastName,String phoneNumber,String city,String staffNumber,
                                          String activated,String createdAt,Pageable pageable){

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<NibssUser> cq = cb.createQuery(NibssUser.class);
        Root<NibssUser> root = cq.from(NibssUser.class);

        //create a base predicate to hold all the queries
        Predicate predicate = cb.like(root.get("staffNumber"), "%%");

        Join name = root.join("name");
        if(!StringUtils.isEmpty(firstName)){
            predicate = cb.and(predicate,cb.like(name.get("firstName"), "%" + firstName + "%"));
        }

        Join contactdetails = root.join("contactDetails");
        if(!StringUtils.isEmpty(city)){
            predicate = cb.and(predicate,cb.like(contactdetails.get("city"), "%" + city + "%"));
        }

        if(!StringUtils.isEmpty(middleName)){
            predicate = cb.and(predicate,cb.like(name.get("middleName"), "%" + middleName + "%"));
        }

        //role
        Join role = root.join("roles");
        if(!StringUtils.isEmpty(roleDesc)){
            predicate = cb.and(predicate,cb.like(role.get("description"), "%" + roleDesc + "%"));
        }

        if(!StringUtils.isEmpty(email)){
            predicate = cb.and(predicate,cb.like(root.get("emailAddress"), "%" + email + "%"));
        }


        if(!StringUtils.isEmpty(lastName)){
            predicate = cb.and(predicate,cb.like(name.get("lastName"), "%" + lastName + "%"));
        }

        if(!StringUtils.isEmpty(phoneNumber)){
            predicate = cb.and(predicate,cb.like(root.get("phoneNumber"), "%" + phoneNumber + "%"));
        }

        if(!StringUtils.isEmpty(staffNumber)){
            predicate = cb.and(predicate,cb.like(root.get("staffNumber"), "%" + staffNumber + "%"));
        }

        if(!StringUtils.isEmpty(createdAt)){
            try{
                SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
                Date from  = CommonUtils.startOfDay(dateFormat.parse(createdAt));
                Date to = CommonUtils.endOfDay(dateFormat.parse(createdAt));
                predicate = cb.and(predicate,cb.between(root.get("createdAt"), from,to));
            }catch(ParseException ex){
                log.error("Error thrown while searching using date created ",ex.getMessage());
            }
        }

        if(!StringUtils.isEmpty(activated)){
            boolean status = Boolean.parseBoolean(activated);
            predicate = cb.and(predicate,cb.equal(root.get("activated"), status));
        }


        cq.where(predicate);

        TypedQuery<NibssUser> query = entityManager.createQuery(cq);

        List<NibssUser> nibssUserList = query.getResultList();
        Page<NibssUser> response = new PageImpl<>(nibssUserList, pageable, nibssUserList.size());

        return ResponseEntity.ok().body(response);

    }

    //######### PRODUCT SEARCH ############################
    public ResponseEntity searchProduct(String desc,String getProductName,String billerName,
                                          String activated,Pageable pageable){


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        //create a base predicate to hold all the queries
        Predicate predicate = cb.like(root.get("description"), "%%");

        Join biller = root.join("biller");
        if(!StringUtils.isEmpty(billerName)){
            predicate = cb.and(predicate,cb.like(biller.get("name"), "%" + billerName + "%"));
        }


        if(!StringUtils.isEmpty(desc)){
            predicate = cb.and(predicate,cb.like(root.get("description"), "%" + desc + "%"));
        }


        if(!StringUtils.isEmpty(getProductName)){
            predicate = cb.and(predicate,cb.like(root.get("name"), "%" + getProductName + "%"));
        }

        if(!StringUtils.isEmpty(activated)){
            boolean status = Boolean.parseBoolean(activated);
            predicate = cb.and(predicate,cb.equal(root.get("activated"), status));
        }


        cq.where(predicate);

        TypedQuery<Product> query = entityManager.createQuery(cq);

        List<Product> productList = query.getResultList();

        Page<Product> response = new PageImpl<>(productList, pageable, productList.size());

        return ResponseEntity.ok().body(response);
    }


    //######### BILLER SEARCH ############################
    public ResponseEntity searchBiller(String accountNumber,String accountName,String bankCode,String companyName,String description,
                                        String rcNumber,String activated,String bvn,Pageable pageable){


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Biller> cq = cb.createQuery(Biller.class);
        Root<Biller> root = cq.from(Biller.class);

        //create a base predicate to hold all the queries
        Predicate predicate = cb.like(root.get("description"), "%%");

        Join code = root.join("bank");
        if(!StringUtils.isEmpty(bankCode)){
            predicate = cb.and(predicate,cb.like(code.get("code"), "%" + bankCode + "%"));
        }

        if(!StringUtils.isEmpty(accountNumber)){
            predicate = cb.and(predicate,cb.like(root.get("accountNumber"), "%" + accountNumber + "%"));
        }


        if(!StringUtils.isEmpty(accountName)){
            predicate = cb.and(predicate,cb.like(root.get("accountName"), "%" + accountName + "%"));
        }

        if(!StringUtils.isEmpty(companyName)){
            predicate = cb.and(predicate,cb.like(root.get("name"), "%" + companyName + "%"));
        }

        if(!StringUtils.isEmpty(description)){
            predicate = cb.and(predicate,cb.like(root.get("description"), "%" + description + "%"));
        }

        if(!StringUtils.isEmpty(rcNumber)){
            predicate = cb.and(predicate,cb.like(root.get("rcNumber"), "%" + rcNumber + "%"));
        }

        if(!StringUtils.isEmpty(bvn)){
            predicate = cb.and(predicate,cb.like(root.get("bvn"), "%" + bvn + "%"));
        }

        if(!StringUtils.isEmpty(activated)){
            boolean status = Boolean.parseBoolean(activated);
            predicate = cb.and(predicate,cb.equal(root.get("activated"), status));
        }

        cq.where(predicate);

        TypedQuery<Biller> query = entityManager.createQuery(cq);

        List<Biller> billerList = query.getResultList();

        Page<Biller> response = new PageImpl<>(billerList, pageable, billerList.size());

        return ResponseEntity.ok().body(response);
    }

    //######### BANK SEARCH ############################
    public ResponseEntity searchBank(String bankCode,String bankName,String nipBankCode,String activated,Pageable pageable){


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Bank> cq = cb.createQuery(Bank.class);
        Root<Bank> root = cq.from(Bank.class);

        //create a base predicate to hold all the queries
        Predicate predicate = cb.like(root.get("name"), "%%");

        if(!StringUtils.isEmpty(bankCode)){
            predicate = cb.and(predicate,cb.like(root.get("code"), "%" + bankCode + "%"));
        }


        if(!StringUtils.isEmpty(bankName)){
            predicate = cb.and(predicate,cb.like(root.get("name"), "%" + bankName + "%"));
        }

        if(!StringUtils.isEmpty(nipBankCode)){
            predicate = cb.and(predicate,cb.like(root.get("nipBankCode"), "%" + nipBankCode + "%"));
        }

        if(!StringUtils.isEmpty(activated)){
            boolean status = Boolean.parseBoolean(activated);
            predicate = cb.and(predicate,cb.equal(root.get("activated"), status));
        }

        cq.where(predicate);

        TypedQuery<Bank> query = entityManager.createQuery(cq);

        List<Bank> bankList = query.getResultList();

        Page<Bank> response = new PageImpl<>(bankList, pageable, bankList.size());

        return ResponseEntity.ok().body(response);
    }

}

