package ng.upperlink.nibss.cmms.dto;

import lombok.Data;
import ng.upperlink.nibss.cmms.model.auth.Role;
import org.apache.commons.lang.StringUtils;


@Data
public class WebAuditSearch extends BaseSearch {

    private final static String ENTITY_PACKAGE = Role.class.getPackage().getName();


    public void setClassName(String className) {
        if(className != null) {
            if(!className.contains(ENTITY_PACKAGE))
                className = String.format("%s.%s", ENTITY_PACKAGE, StringUtils.capitalize(className) );
        }
        this.className = className;
    }



    private String username;

    private String className;


    public boolean isClassNameEmpty() {
        return StringUtils.isEmpty(className);
    }

    public boolean isUsernameEmpty() {
        return StringUtils.isEmpty(username);
    }


    public boolean isOnlyClassName() {
        return  !isClassNameEmpty() && isUsernameEmpty();
    }

    public boolean isOnlyUsername() {
        return !isUsernameEmpty() && isClassNameEmpty();
    }

    public  boolean isUsernameAndClassName() {
        return !isUsernameEmpty() && !isClassNameEmpty();
    }
    public boolean isAllEmpty() {
        return isUsernameEmpty() && isClassNameEmpty();
    }

}
