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
import ng.upperlink.nibss.cmms.repo.MandateRepo;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.biller.ProductService;
import ng.upperlink.nibss.cmms.service.emandate.mcash.McashAutheticationService;
import ng.upperlink.nibss.cmms.service.mandateImpl.MandateStatusService;
import ng.upperlink.nibss.cmms.util.MandateUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillerEmandateService
{
    private static Logger logger = LoggerFactory.getLogger(BillerEmandateService.class);
    private ProductService productService;
    private BankService bankService;
    private MandateRepo mandateRepo ;
    private MandateStatusService mandateStatusService;
    private McashAutheticationService mcashAutheticationService;
    private EmandateBaseService emandateBaseService;

    @Autowired
    public void setEmandateBaseService(EmandateBaseService emandateBaseService) {
        this.emandateBaseService = emandateBaseService;
    }
    @Autowired
    public void setMcashAutheticationService(McashAutheticationService mcashAutheticationService)
    {
        this.mcashAutheticationService = mcashAutheticationService;
    }
    @Autowired
    public void setMandateStatusService(MandateStatusService mandateStatusService){
        this.mandateStatusService = mandateStatusService;
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
    public EmandateResponse processMandate(EmandateRequest requestObject){
        Mandate mandate = null;
        try
        {
            Product product = productService.getProductById(requestObject.getProductId());
            if (product==null)
            {
                logger.info(EmandateResponseCode.PRODUCT_NOT_FOUND.getValue().replace("{}",String.valueOf(requestObject.getProductId())));
                return EmandateBaseService.generateEmandateResponse(EmandateResponseCode.PRODUCT_NOT_FOUND,null,String.valueOf(requestObject.getProductId()));
            }
            Biller biller = product.getBiller();
            if (biller==null){
                {
                    logger.info("Could not find the Biller of the product: "+product.getName());
                    return EmandateBaseService.generateEmandateResponse(EmandateResponseCode.PRODUCT_BILLER_NOT_FOUND,null,String.valueOf(requestObject.getProductId()));
                }

            }
            String rcNumber = biller.getRcNumber();
            Bank subscriberBank = bankService.getBankByCode(requestObject.getBankCode());
            if (subscriberBank == null)
            {
                logger.info(EmandateResponseCode.SUBSCRIBER_BANK_NOT_FOUND.getValue()+" CBN code:"+requestObject.getBankCode());
                return EmandateBaseService.generateEmandateResponse(EmandateResponseCode.SUBSCRIBER_BANK_NOT_FOUND,null,requestObject.getBankCode());
            }
            String mandateCode = MandateUtils.getMandateCode(String.valueOf(System.currentTimeMillis()), rcNumber, String.valueOf(product.getId()));
            logger.info("Generated mandate code "+mandateCode );
            Channel channel = null;
            channel = Channel.findById(requestObject.getChannelCode());

            if (channel ==null || channel.equals(Channel.UNKNOWN))
                return EmandateBaseService.generateEmandateResponse(EmandateResponseCode.CHANNEL_NOT_FOUND,null,requestObject.getChannelCode());
            logger.info("The Request is Coming through channel : "+channel);
            mandate = emandateBaseService.generateMandate(new Mandate(), requestObject, mandateCode, product, subscriberBank,biller,channel);
            if (mandate ==null) return EmandateBaseService.generateEmandateResponse(EmandateResponseCode.MANDATE_NOT_GENERATEED,null,"");

            EmandateResponse emandateResponse = mcashAutheticationService.setupMcashRequest(mandate,requestObject,channel.getValue());
            logger.info("Mcash response : "+emandateResponse);
            if (StringUtils.isEmpty(emandateResponse.getResponseCode()))
                return  EmandateBaseService.generateEmandateResponse(EmandateResponseCode.MCASH_NO_RESPONSE,null,subscriberBank.getName());
            if (!emandateResponse.getResponseCode().equalsIgnoreCase("00"))
                {
                    emandateResponse.setMandateCode(null);
                    return emandateResponse;
                }
            logger.info("Mandate to be Saved : "+mandate);
            mandate = save(mandate);
            if (mandate != null)
            {
                emandateBaseService.sendMandateAdvice(mandate);
                return EmandateBaseService.generateEmandateResponse(EmandateResponseCode.CREATION_SUCCESSFUL,mandate.getMandateCode(),"");
            }
        } catch (CMMSException ex) {
            logger.error("---Exception trace --- {} ", ex);
            return new EmandateResponse(ex.getMessage(),null,ex.getEmandateErrorCode());
        }

        return EmandateBaseService.generateEmandateResponse(EmandateResponseCode.UNKNOWN,null,"");

    }

    private Mandate save(Mandate mandate) throws CMMSException {
        try {
            return mandateRepo.save(mandate);
        }catch (Exception e)
        {
            logger.error("--Exception trace --{} ",e);
            throw new CMMSException(EmandateResponseCode.UNKNOWN.getValue(),"500",EmandateResponseCode.UNKNOWN.getCode());
        }
    }



}
