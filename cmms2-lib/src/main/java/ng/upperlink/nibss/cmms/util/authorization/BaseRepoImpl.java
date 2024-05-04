//package ng.upperlink.nibss.cmms.util.authorization;
//
//
//import ng.upperlink.nibss.cmms.model.authorization.Audit;
//import ng.upperlink.nibss.cmms.model.authorization.AuthorizationTable;
//import ng.upperlink.nibss.cmms.repo.BaseRepo;
//import org.springframework.data.jpa.repository.support.JpaEntityInformation;
//import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
//import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.EntityManager;
//import java.io.Serializable;
//
////@SuppressWarnings("SpringJavaAutowiringInspection")
////public class BaseRepoImpl<T, ID extends Serializable>
////        extends SimpleJpaRepository<T, ID> implements BaseRepo<T, ID> {
//
////    private EntityManager entityManager;
////    private JpaEntityInformation<T, ID> entityInformation;
////
////    public BaseRepoImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
////        super(JpaEntityInformationSupport.getEntityInformation(entityInformation.getJavaType(), entityManager), entityManager);
////        this.entityManager = entityManager;
////        this.entityInformation = entityInformation;
////    }
////
////    public BaseRepoImpl(Class<T> domainClass, EntityManager entityManager) {
////        super(domainClass, entityManager);
////        this.entityManager = entityManager;
////    }
////
////    @Override
////    public EntityManager getEntityManager() {
////        return entityManager;
////    }
////
////
////    @Transactional
////    public T create(T entity) {
////        return this.save(entity);
////    }
////
////    @Transactional
////    public T update(T entity) {
////
////        System.out.println(" I was called from baseRepo for update");
////
////        System.out.println("AuthInstance= " + (entity instanceof IAuthorizationEvent) + "AuditInstance=" + (entity instanceof IAuditEvent));
////
////        if (entity instanceof AuthorizationTable || entity instanceof Audit) {
////            this.save(entity);
////            return true;
////        }
//////
////        if (entity instanceof IAuthorizationEvent) ((IAuthorizationEvent) entity).handleBeforeSave();
////        else if (entity instanceof IAuditEvent) ((IAuditEvent) entity).beforeSave();
////
////        System.out.println("Failed get instance in Update");
////        return ((IModel) entity).update();
////    }
////
////    @Override
////    @Transactional
////    public <S extends T> S save(S entity) {
////        System.out.println(" I was called from baseRepo for create");
////        System.out.println("AuthInstance= " + (entity instanceof IAuthorizationEvent) + "AuditInstance=" + (entity instanceof IAuditEvent));
////        if (entity instanceof IAuthorizationEvent) ((IAuthorizationEvent) entity).handleBeforeCreate();
////        else if (entity instanceof IAuditEvent) ((IAuditEvent) entity).beforeCreate();
////
////        System.out.println("Failed get instance in Create");
////        return super.save(entity);
////    }
////
////
////    @Transactional
////    public void toggle() {
////
////    }
////}
//
