package ng.upperlink.nibss.cmms.service.auth;

import ng.upperlink.nibss.cmms.model.Settings;
import ng.upperlink.nibss.cmms.repo.auth.SettingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by stanlee on 21/05/2018.
 */
@Service
public class SettingService {

    private SettingRepo settingRepo;

    @Autowired
    public void setSettingRepo(SettingRepo settingRepo) {
        this.settingRepo = settingRepo;
    }

    public Settings getSettingsByName(String name){
        return settingRepo.getSettingsByName(name);
    }
    public Settings get(Long id){
        return settingRepo.findOne(id);
    }

    public List<Settings> getAllByNames(List<String> names){
        return settingRepo.getAllByNames(names);
    }
    public List<Settings> getAll(){
        return settingRepo.findAll();
    }

    public Settings save(Settings settings){
        return settingRepo.save(settings);
    }
    public List<Settings> save(Iterable<Settings> settings){
        return settingRepo.save(settings);
    }
}
