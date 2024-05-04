package ng.upperlink.nibss.cmms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ng.upperlink.nibss.cmms.model.WebAuditAction;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class WebAppAuditEntryDto {



    private long id;

    private String user;

    private String className;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Africa/Lagos")
    private Date dateCreated;

    private String oldObject;

    private String newObject;

    private WebAuditAction action;

    public WebAppAuditEntryDto(long id, String user, String className, Date dateCreated, WebAuditAction action) {
        this.id = id;
        this.user = user;
        this.className = getPrimaryClassName(className);
        this.dateCreated = dateCreated;
        this.action = action;
    }

    public WebAppAuditEntryDto(long id, String user, String className,Date dateCreated, WebAuditAction action,
                               String oldObject, String newObject) {
        this(id,user,className,dateCreated, action);
        this.oldObject = oldObject;
        this.newObject = newObject;
    }

    public WebAppAuditEntryDto(String oldObject, String newObject) {
        this.oldObject = oldObject;
        this.newObject = newObject;
    }

    private String getPrimaryClassName(String className) {
        if( null == className || className.isEmpty())
            return className;
        return className.substring(className.lastIndexOf('.') + 1);
    }
}
