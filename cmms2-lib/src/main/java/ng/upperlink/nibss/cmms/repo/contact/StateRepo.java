package ng.upperlink.nibss.cmms.repo.contact;

import ng.upperlink.nibss.cmms.model.contact.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;


@Transactional
@Repository
public interface StateRepo extends JpaRepository<State, Long>{

    @Query("select count(s.id) from State s where s.name = :name")
    int getCountByName(@Param("name") String name);

    @Query("select count(s.id) from State s where s.name = :name and s.id <> :id")
    int getCountByNameAndNotId(@Param("name") String name, @Param("id") Long id);


    @Query("select s from State s where s.country.id = :id")
    List<State> getStatesByCountry(@Param("id") Long id);
}
