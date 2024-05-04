package ng.upperlink.nibss.cmms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import java.io.Serializable;

@NoRepositoryBean
public interface BaseRepo<T, ID extends Serializable> extends JpaRepository<T, ID> {


    EntityManager getEntityManager();

    T create(T entity);
    T update(T entity);
}
