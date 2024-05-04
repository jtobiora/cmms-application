package ng.upperlink.nibss.cmms.enums;

public enum MandateStatusType {
    NONE(0L,"none"),
    BILLER_INITIATE_MANDATE(1L,"Biller Initiated"),
    BILLER_AUTHORIZE_MANDATE(2L,"Biller Authorized"),
    BILLER_REJECT_MANDATE(3L,"Biller Rejected"),
    BILLER_APPROVE_MANDATE(4L,"Biller Approved"),
    BILLER_DISAPPROVE_MANDATE(5L,"Biller Disapproved"),
    BANK_AUTHORIZE_MANDATE(6L,"Bank Authorized"),
    BANK_REJECT_MANDATE(7L,"Bank Rejected"),
    BANK_APPROVE_MANDATE(8L,"Bank Approved"),
    BANK_DISAPPROVE_MANDATE(9L,"Bank Disapproved"),
    BANK_INITIATE_MANDATE(10L,"Bank Initiated"),
    BANK_BILLER_INITIATE_MANDATE(11L,"Bank Biller Initiated"),
    BANK_BILLER_AUTHORIZE_MANDATE(12L,"Bank Biller Authorized"),
    NIBSS_BILLER_INITIATE_MANDATE(13L,"NIBSS Initiated"),
    NIBSS_BILLER_AUTHORIZE_MANDATE(14L,"NIBSS Authorized"),
    NIBSS_REJECT_MANDATE(15L,"NIBSS Rejected"),
    PSSP_REJECT_MANDATE(16L,"PSSP Rejected"),
    PSSP_INITIATE_MANDATE(17L,"PSSP Initiated"),
    PSSP_AUTHORIZE_MANDATE(18L,"PSSP Authorized"),
    PSSP_APPROVE_MANDATE(19L,"PSSP Approved");
    ;

    Long id;
    String description;

    MandateStatusType(Long id,String description) {
        this.id = id;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getDescription(){
        return description;
    }

    public static String getMandateStatusDescriptionById(Long id) {
        for(MandateStatusType mStatus : MandateStatusType.values()) {
            if(mStatus.getId() == id)
                return mStatus.getDescription();
        }
        return null;
    }



}
