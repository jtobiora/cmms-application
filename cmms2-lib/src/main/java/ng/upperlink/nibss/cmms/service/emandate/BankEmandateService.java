package ng.upperlink.nibss.cmms.service.emandate;

import ng.upperlink.nibss.cmms.dto.emandates.EmandateRequest;
import ng.upperlink.nibss.cmms.dto.emandates.response.EmandateResponse;
import ng.upperlink.nibss.cmms.enums.*;
import ng.upperlink.nibss.cmms.enums.emandate.EmandateResponseCode;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.biller.Product;
import ng.upperlink.nibss.cmms.model.mandate.Mandate;
import ng.upperlink.nibss.cmms.model.mandate.MandateStatus;
import ng.upperlink.nibss.cmms.repo.MandateRepo;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.biller.ProductService;
import ng.upperlink.nibss.cmms.service.mandateImpl.MandateStatusService;
import ng.upperlink.nibss.cmms.util.DateUtils;
import ng.upperlink.nibss.cmms.util.MandateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class BankEmandateService
{

    private static Logger logger = LoggerFactory.getLogger(BankEmandateService.class);
    private ProductService productService;
    private BankService bankService;
    private MandateRepo mandateRepo ;
    private EmandateBaseService emandateBaseService;

    @Autowired
    public void setEmandateBaseService(EmandateBaseService emandateBaseService) {
        this.emandateBaseService = emandateBaseService;
    }

    @Autowired
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    @Autowired
    public void setProductService(ProductService productService)
    {
        this.productService = productService;
    }
    @Autowired
    public void setMandateRepo(MandateRepo mandateRepo)
    {
        this.mandateRepo = mandateRepo;
    }
    public EmandateResponse processSaveUpdate(EmandateRequest requestObject) throws CMMSException {

        Mandate mandate = new Mandate();
        Product product =null;
        Biller biller =null;
        String rcNumber =null;
        Bank subcriberBank =null;
        String mandateCode =null;
        Channel channel = null;

        try {
            product = productService.getProductById(requestObject.getProductId());
            if (product==null)
            {
                return EmandateBaseService.generateEmandateResponse(EmandateResponseCode.PRODUCT_NOT_FOUND, null,String.valueOf(requestObject.getProductId()));
            }
            biller = product.getBiller();
            if (biller==null){
                return EmandateBaseService.generateEmandateResponse(EmandateResponseCode.PRODUCT_BILLER_NOT_FOUND,null,String.valueOf(requestObject.getProductId()));
            }
            rcNumber = biller.getRcNumber();

            //get the subscriber's bank
            subcriberBank = bankService.getBankByCode(requestObject.getBankCode());

            if (subcriberBank == null){
                return EmandateBaseService.generateEmandateResponse(EmandateResponseCode.SUBSCRIBER_BANK_NOT_FOUND,null,requestObject.getBankCode());
            }
            mandateCode = MandateUtils.getMandateCode(String.valueOf(System.currentTimeMillis()), rcNumber, String.valueOf(product.getId()));
            if (mandateCode ==null)
                return EmandateBaseService.generateEmandateResponse(EmandateResponseCode.MANDATE_NOT_GENERATEED,null,"");
            logger.info("Generated mandate code "+mandateCode );

                channel = Channel.findById(requestObject.getChannelCode());
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new CMMSException(EmandateResponseCode.CHANNEL_NOT_FOUND.getValue().replace("{}",requestObject.getChannelCode()),"400",EmandateResponseCode.CHANNEL_NOT_FOUND.getCode());
            }
            if (channel ==null || channel.equals(Channel.UNKNOWN))
                return EmandateBaseService.generateEmandateResponse(EmandateResponseCode.CHANNEL_NOT_FOUND,null,requestObject.getChannelCode());
            logger.info("The Request is Coming through channel : "+channel);
            //generateAuthRequest mandate
            mandate = emandateBaseService.generateMandate(mandate, requestObject, mandateCode, product, subcriberBank,biller,channel);
            logger.info("Mandate to be saved "+mandate);

            if (mandate ==null)
            {
                return EmandateBaseService.generateEmandateResponse(EmandateResponseCode.MANDATE_NOT_GENERATEED,mandate.getMandateCode(),"");

            }else
            {
                mandate = saveMandate(mandate);
                if (mandate !=null)
                    emandateBaseService.sendMandateAdvice(mandate);
                return EmandateBaseService.generateEmandateResponse(EmandateResponseCode.CREATION_SUCCESSFUL,mandate.getMandateCode(),"");
            }
    }
    public Mandate saveMandate(Mandate mandate) throws CMMSException {
        try {
            return mandateRepo.save(mandate);
        }catch (Exception e)
        {
            logger.error("--Exception trace --{} ",e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500",EmandateResponseCode.UNKNOWN.getCode());
        }
    }


}
