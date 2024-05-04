package ng.upperlink.nibss.cmms.repo;

import ng.upperlink.nibss.cmms.model.Rejection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface RejectionRepo extends JpaRepository<Rejection,Long> {
}
