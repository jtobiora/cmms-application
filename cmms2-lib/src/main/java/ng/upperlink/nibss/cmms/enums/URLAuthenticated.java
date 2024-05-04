package ng.upperlink.nibss.cmms.enums;


public interface URLAuthenticated {

    String RESET = "/Reset";
    String AGENTS = "/user/agt-mgr/{agt-mgr-code}/agent";
    String AGENT_DETAIL = AGENTS+"/{id}";
    String AGT_MGR_REPORT = "/report/agt-mgr";
    String AGT_BRANCH = "/agent/{agent-id}/branch";
    String AGT_BRANCH_DETAIL = AGT_BRANCH+"/{id}";
    String AGT_BRANCH_ENROLLERS = AGT_BRANCH+"/{id}/enrollers";
    String ENROLLERS = "/agent/{agent-id}/enroller";
    String ENROLLER_DETAIL = ENROLLERS+"/{id}";
    String COUNTRY = "/country";
    String STATE = "/state";
    String LGA = "/state/{state-id}/lga";

}
