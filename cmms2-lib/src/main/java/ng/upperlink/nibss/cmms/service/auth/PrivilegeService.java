package ng.upperlink.nibss.cmms.service.auth;

import ng.upperlink.nibss.cmms.dto.auth.PrivilegeRequest;
import ng.upperlink.nibss.cmms.dto.TaskResponse;
import ng.upperlink.nibss.cmms.enums.Module;
import ng.upperlink.nibss.cmms.model.auth.Privilege;
import ng.upperlink.nibss.cmms.repo.auth.PrivilegeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by stanlee on 08/04/2018.
 */
@Service
public class PrivilegeService {

    private static Logger LOG = LoggerFactory.getLogger(PrivilegeService.class);

    private PrivilegeRepo tasksRepo;

    @Autowired
    public void setPrivilegeRepo(PrivilegeRepo tasksRepo) {
        this.tasksRepo = tasksRepo;
    }

    public Page<Privilege> get(Pageable pageable){
        return tasksRepo.findAll(pageable);
    }

    public List<Privilege> get(){
        return tasksRepo.findAll();
    }

    public Page<Privilege> getAllActivated(Pageable pageable){
        return tasksRepo.getAllByActivated(pageable);
    }

    public List<Privilege> getAllActivated(){
        return tasksRepo.getAllByActivated();
    }

    public Privilege get(Long id){
        return tasksRepo.findOne(id);
    }

    public Set<Privilege> get(List<Long> ids){
        return tasksRepo.getAllByIdAndActivated(ids);
    }

    public Privilege save(Privilege task){
        return tasksRepo.save(task);
    }
    public List<Privilege> save(List<Privilege> task){
        return tasksRepo.save(task);
    }

    public void clearAll(){
        tasksRepo.deleteAll();
    }

    public Privilege getByNameAndModule(String name, Module module){
        return tasksRepo.getByNameAmdModule(name, module);
    }

    public Privilege getByNameAndModuleAndNotId(String name, Module module, Long id){
        return tasksRepo.getByNameAndModuleAndNotId(name, id, module);
    }

    public Privilege toggle(Long id){

        if (id == null){
            return new Privilege();
        }

        tasksRepo.toggle(id);
        return get(id);
    }

    public String validate(PrivilegeRequest taskRequest, boolean isUpdate, Long id){

        Privilege task = null;
        if (isUpdate){
            if (id == null){
                return "Privilege id is not provided";
            }

            task = getByNameAndModuleAndNotId(taskRequest.getName(), Module.valueOf(taskRequest.getModule()), id);
        }else {
            task = getByNameAndModule(taskRequest.getName(), Module.valueOf(taskRequest.getModule()));
        }

        if (task != null){
            return "Role name '"+taskRequest.getName()+"' already exist";
        }

        return null;
    }

    public Set<TaskResponse> generatePrivilegeReponse(Set<Privilege> tasks){

        Set<TaskResponse> responseSet = new HashSet<>();

        tasks.forEach(task -> {
            responseSet.add(new TaskResponse(task.getId(),task.getName(),task.getUrl(),task.getModule(),task.getDescription(),task.isActivated()));
        });

        return responseSet;
    }
}
