package ng.upperlink.nibss.cmms.repo.contact;

import ng.upperlink.nibss.cmms.model.contact.Lga;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;


@Transactional
@Repository
public interface LgaRepo extends JpaRepository<Lga, Long>{


    @Query("select l from Lga l where l.state.id = :stateId")
    Page<Lga> getLgasByState(@Param("stateId") Long stateId, Pageable pageable);

    @Query("select l from Lga l where l.state.id = :stateId")
    List<Lga> getLgasByState(@Param("stateId") Long stateId);

    @Query("select l from Lga l where l.state.id = :stateId and l.id = :id")
    Lga getLga(@Param("id") Long id, @Param("stateId") Long stateId);

    @Query("select count(l.id) from Lga l where l.name = :name")
    int getCountByName(@Param("name") String name);

    @Query("select count(l.id) from Lga l where l.name = :name and l.id <> :id")
    int getCountByNameAndNotId(@Param("name") String name, @Param("id") Long id);

    @Query("select l.name from Lga l order by l.name asc")
    List<String> getAllNames();

    @Query("select l.code from Lga l order by l.code asc")
    List<String> getAllCodes();

    @Query("select l from Lga l ")
    List<Lga> getAll();

    @Query("select l from Lga l join l.state s where s.name like ?1 ")
    List<Lga> getAnyLgaInStateLike(String param, Pageable pageable);
}
