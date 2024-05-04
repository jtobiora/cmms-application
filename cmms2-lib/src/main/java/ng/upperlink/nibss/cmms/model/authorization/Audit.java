//package ng.upperlink.nibss.cmms.model.authorization;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import com.fasterxml.jackson.annotation.JsonFilter;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.ToString;
//
//import javax.persistence.*;
//import javax.validation.constraints.NotNull;
//
//@Entity
//@Table(name = "audits")
//@ToString
//@JsonFilter( "genericModelFilter")
//public class Audit {
//
////    @Transient
////    public static String searchables = "ip,rid,dependency,tableName,taskRoute,action,userAction,userName,trailType,trailId";
////
////    @Transient
////    public static String table = "audits";
////
////    @Transient
////    public static String references = "login:logins,authorization:authorizations";  //attribute:tablename
////
//////    @OneToOne
//////    @JoinColumn
//////    @JsonBackReference
//////    private Login login;
////
////    @JsonProperty("trail_type")
////    private String trailType;
////
////    @NotNull
////    @JsonProperty("trail_id")
////    private Long trailId;
////
////    @ManyToOne(fetch = FetchType.LAZY)
////    @JoinColumn
////    @JsonBackReference
////    private AuthorizationTable authorization;
////
////    @Column(length = 60)
////    @JsonProperty("user_name")
////    private String userName = "";
////
////    @Column(length = 80)
////    @JsonProperty("task_route")
////    private String taskRoute;
////
////    @Column(length = 200)
////    @JsonProperty("user_action")
////    private String userAction;
////
////    @Column(length = 100)
////    @JsonProperty("table_name")
////    private String tableName;
////
////    @Column(length = 20)
////    private String action;
////
////    @Column(length = 20)
////    private String ip;
////
////    @Column(length = 40)
////    private String rid;
////
////    @NotNull
////    @Enumerated(EnumType.STRING)
////    private AuditStatus status = AuditStatus.ACTIVE;
////
////    @Column
////    private String before;
////
////    @Column
////    private String after;
////
////    @Column
////    private String dependency;
////
////    public void setBefore(String before){
////        this.before = before;
////    }
////
////    public void setAfter(String after){
////        this.after = after;
////    }
////
////
////    /****
////     * Handles event action before an information is updated
////     */
////    @PreRemove
////    void beforeDelete() {
////        System.out.println("I was called..... during delete IAuditEvent");
////        StaticGlobal.setAuditChannel("fromAudit", true);
////        AuditEvent.onBeforeAudit(this, AuditAction.DELETE);
////    }
////
////
////    /****
////     * Handles event action after an information is updated
////     */
////    @PostRemove
////    void afterDelete() {
////        System.out.println("I was called..... after delete IAuditEvent");
////        StaticGlobal.setAuditChannel("fromAudit", true);
////        AuditEvent.onAfterAudit(this, AuditEvent.EventHandlerType.AFTER_DELETE, this);
////    }
////
//
//}
