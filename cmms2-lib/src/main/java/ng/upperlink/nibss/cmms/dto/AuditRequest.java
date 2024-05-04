package ng.upperlink.nibss.cmms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditRequest {

    private Long id;
    private String macAddress;
    private String ipAddress;
    private String userName;
    private String createdAt;
    private String action;

    private String agentCode;


    public AuditRequest(long id, String macAddress, String ipAddress, String userName,
                        Date createdAt, String action, String agentCode) {

        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        this.id = id;
        this.macAddress = macAddress;
        this.ipAddress = ipAddress;
        this.userName = userName;
        if (null != createdAt)
            this.createdAt = fmt.format(createdAt);
        this.action = action;
        this.agentCode = agentCode;
    }

}
