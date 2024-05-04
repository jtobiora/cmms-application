package ng.upperlink.nibss.cmms.service.contact;

import ng.upperlink.nibss.cmms.dto.contact.StateRequest;
import ng.upperlink.nibss.cmms.model.contact.State;
import ng.upperlink.nibss.cmms.repo.contact.StateRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by stanlee on 08/04/2018.
 */
@Service
public class StateService {

    private static Logger LOG = LoggerFactory.getLogger(StateService.class);

    private StateRepo stateRepo;

    private CountryService countryService;

    @Autowired
    public void setStateRepo(StateRepo stateRepo) {
        this.stateRepo = stateRepo;
    }

    @Autowired
    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    public Page<State> get(Pageable pageable){
        return stateRepo.findAll(pageable);
    }

    public List<State> get(){
        return stateRepo.findAll();
    }

    public State get(Long id){
        return stateRepo.findOne(id);
    }

    public State save(State state){
        return stateRepo.save(state);
    }

    public int getByName(String name){
        return stateRepo.getCountByName(name);
    }

    public int getByNameAndNotId(String name, Long id){
        return stateRepo.getCountByNameAndNotId(name, id);
    }

    public String validate(StateRequest request, boolean isUpdate, Long id){

        int count = 0;
        if (isUpdate){
            if (id == null){
                return "State id is not provided";
            }

            count = getByNameAndNotId(request.getName(), id);
        }else {
            count = getByName(request.getName());
        }

        if (count > 0){
            return "State name '"+request.getName()+"' already exist";
        }

        return null;
    }

    public State generate(State state, StateRequest request){
        state.setName(request.getName());
        state.setCountry(countryService.get(request.getId()));
        return state;
    }

    public List<State> getStatesByCountry(Long id){
        return stateRepo.getStatesByCountry(id);
    }
}
