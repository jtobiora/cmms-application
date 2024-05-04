package ng.upperlink.nibss.cmms.service.auth;

import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.enums.RoleType;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.exceptions.CMMSException;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.auth.Role;
import ng.upperlink.nibss.cmms.repo.auth.RolesRepo;
import ng.upperlink.nibss.cmms.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Created by stanlee on 08/04/2018.
 */
@Service
public class RoleService {

    private static Logger LOG = LoggerFactory.getLogger(RoleService.class);
    private UserService userService;
    private RolesRepo rolesRepo;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRolesRepo(RolesRepo rolesRepo) {
        this.rolesRepo = rolesRepo;
    }

    public Page<Role> get(Pageable pageable){
        return rolesRepo.getAll(pageable);
    }

    public List<Role> getActivated(UserType userType){
        return rolesRepo.getAllActivated(userType);
    }
    public List<Role> getActivatedByRoleType(UserType userType,UserDetail userDetail) throws CMMSException {
        User operator = userService.get(userDetail.getUserId());
        if (operator == null)
        {
            throw new CMMSException("Please login and try again","401","401");
        }
        UserType loggedInUserType = operator.getUserType();
        Role operatorRole = operator.getRoles().stream().findAny().get();

        switch (loggedInUserType)
        {
            case SYSTEM:return rolesRepo.getAllActivatedByRoleType(UserType.NIBSS,RoleType.ADMIN);
            case NIBSS:
                if (operatorRole.getName().equals(RoleName.NIBSS_SUPER_ADMIN_INITIATOR)||
                        operatorRole.getName().equals(RoleName.NIBSS_SUPER_ADMIN_AUTHORIZER))
                    return rolesRepo.getAllActivatedByRoleType(UserType.NIBSS,RoleType.ADMIN);
                else if (userType.equals(UserType.NIBSS))
                    return rolesRepo.getAllActivatedByRoleType(userType,RoleType.USER);
                else
                    return rolesRepo.getAllActivatedByRoleType(userType,RoleType.ADMIN);
            case BANK:
                switch (userType)
                {
                    case BANK: return rolesRepo.getAllActivatedByRoleType(userType,RoleType.USER);
                    case PSSP:return rolesRepo.getAllActivatedByRoleType(userType,RoleType.ADMIN);
                    case BILLER: return rolesRepo.getAllActivatedByRoleType(userType,RoleType.ADMIN);
                    default:return rolesRepo.getAllActivatedByRoleType(UserType.BANK,RoleType.USER);
                }

            case PSSP:
                switch (userType)
                {
                    case PSSP:return rolesRepo.getAllActivatedByRoleType(userType,RoleType.USER);
                    case BILLER: return rolesRepo.getAllActivatedByRoleType(userType,RoleType.ADMIN);
                    default:return rolesRepo.getAllActivatedByRoleType(UserType.PSSP,RoleType.USER);
                }

            case BILLER:
                return rolesRepo.getAllActivatedByRoleType(userType,RoleType.USER);
            default:return null;

        }
    }
    public List<Role> getRolesByUserType(UserType userType){
        return rolesRepo.getAllRolesByUserType(userType);
    }

    public List<Role> getActivated(Collection<Long> ids){
        return rolesRepo.getAllActivated(ids);
    }

    public List<Role> getActivated(Long ids){
        return rolesRepo.getAllActivated(ids);
    }

    public Role get(Long id){
        return rolesRepo.findOne(id);
    }

    public List<String> getAllNamesByUserType(UserType userType){
        return rolesRepo.getAllNamesByUserType(userType);
    }

    public Role save(Role role){
        return rolesRepo.save(role);
    }

    public Role getByName(RoleName name){
        return rolesRepo.getByName(name);
    }

    public Role getById(Long id){
        return rolesRepo.getById(id);
    }

    public Role getByNameAndNotId(RoleName name, Long id){
        return rolesRepo.getByNameAndNotId(name, id);
    }

    public Role toggle(Long id){

        if (id == null){
            return new Role();
        }

        rolesRepo.toggle(id);
        return get(id);
    }

    public String validate(ng.upperlink.nibss.cmms.dto.auth.Role role, boolean isUpdate, Long id){

        Role existingRole = null;
        if (isUpdate){
            if (id == null){
                return "Role id is not provided";
            }

            existingRole = getByNameAndNotId(role.getName(), id);
        }else {
            existingRole = getByName(role.getName());
        }

        if (existingRole != null){
            return "Role name '"+role.getName()+"' already exist";
        }

        return null;
    }

    public Page<Role> getAll(Pageable pageable){
        return rolesRepo.getAll(pageable);
    }

}
