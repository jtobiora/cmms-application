package ng.upperlink.nibss.cmms.dto.mandates;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.*;
import ng.upperlink.nibss.cmms.model.Rejection;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.biller.Product;
import ng.upperlink.nibss.cmms.model.mandate.MandateStatus;
import ng.upperlink.nibss.cmms.util.JsonDateSerializer;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mandate implements Serializable{

    private Long id;

    private String mandateCode;

    private String subscriberCode;

    private Channel channel;

    private BigDecimal amount;

    private Bank bank;

    private Biller biller;

    private String email;

    private String payerName;

    private String payerAddress;

    private String accountName;

    private String accountNumber;

    private String narration;

    private String mandateImage;

    private Date startDate;

    private Date endDate;

    private Date dateCreated;

    private Product product;

    private int frequency;

    private MandateStatus status;

    private User createdBy;

    private User approvedBy;

    private User acceptedBy;

    private User authorizedBy;

    private Date dateApproved;

    private Date dateAccepted;

    private Date dateAuthorized;

    private String phoneNumber;

    private Rejection rejection;

    private String workflowStatus;

    private User lastActionBy;

    private Date dateSuspended;

    private boolean mandateAdviceSent;

    private Date nextDebitDate;

    private int kycLevel;

    private String bvn;

    private int requestStatus;

    private MandateRequestType mandateType; //(1) -Fixed mandate (2)Variable Mandate

    private MandateCategory mandateCategory; //(1) -Fixed mandate (2)Variable Mandate

    private boolean fixedAmountMandate;

    private BigDecimal variableAmount;

    private Date dateModified;

    private MandateFrequency mandateFrequency;
}
