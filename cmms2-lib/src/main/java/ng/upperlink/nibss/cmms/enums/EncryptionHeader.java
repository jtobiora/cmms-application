package ng.upperlink.nibss.cmms.enums;


public enum EncryptionHeader {

    AUTHORIZATION("AuthorizationTable"),
    SIGNATURE("SIGNATURE"),
    SIGNATURE_METH("SIGNATURE_METH"),
    ORGANISATION_CODE("OrganisationCode");

    String name;

    EncryptionHeader(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
