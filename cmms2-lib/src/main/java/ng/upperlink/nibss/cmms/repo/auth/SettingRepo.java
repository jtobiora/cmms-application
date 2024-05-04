package ng.upperlink.nibss.cmms.repo.auth;

import ng.upperlink.nibss.cmms.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingRepo extends JpaRepository<Settings, Long> {

    Settings getSettingsByName(String name);

    @Query(" select e from Settings e where e.name in ?1 ")
    List<Settings> getAllByNames(List<String> names);

}
