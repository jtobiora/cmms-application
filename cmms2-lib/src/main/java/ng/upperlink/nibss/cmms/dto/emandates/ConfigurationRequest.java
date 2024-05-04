package ng.upperlink.nibss.cmms.dto.emandates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.makerchecker.EntityType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationRequest {
    private Long id;
    private Long ownerId;
    private String username;
    private String domainName;
    private String notificationUlr;
    private EntityType entityType;
}
