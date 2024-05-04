package ng.upperlink.nibss.cmms.repo;

import ng.upperlink.nibss.cmms.dto.SubscriberDto;
import ng.upperlink.nibss.cmms.dto.SubscriberResponse;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.model.Subscriber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;


@Transactional
@Repository
public interface SubscriberRepo extends JpaRepository<Subscriber, Long>{

    String BASE_DTO_QUERY = "select new ng.upperlink.nibss.cmms.dto.SubscriberDto(a.id,u.name.firstName,u.name.lastName,u.contactDetails.lga.state.name," +
            "u.contactDetails.lga.name,u.emailAddress,u.bvn,a.code, a.createdAt) from Subscriber a join a.user u";
    //MAKER CHECKER
    String BASE_ALL_JSON_QUERY = "select new ng.upperlink.nibss.cmms.model.Subscriber(a.id ,a.makerChecker.unapprovedData) from Subscriber a where a.makerChecker.unapprovedData is not null ";
    String BASE_UNAPPROVED_QUERY = BASE_ALL_JSON_QUERY +" and a.makerChecker.approvalStatus is null ";
    String BASE_APPROVED_OR_DISAPPROVED_QUERY = BASE_ALL_JSON_QUERY +" and a.makerChecker.approvalStatus = ?1 ";

    @Modifying
    @Query("UPDATE Subscriber a SET a.user.activated = CASE a.user.activated WHEN true THEN false ELSE true END WHERE a.id = :id")
    int toggle(@Param("id") Long id);

    @Query("select a from Subscriber a where a.user.userType = :userType")
    Page<Subscriber> getAll(@Param("userType") UserType userType, Pageable pageable);


    @Query("select n from Subscriber n where n.code = :anyKey or n.user.name.firstName like :anyKey or n.user.name.lastName like :anyKey or n.user.emailAddress like :anyKey or n.user.emailAddress like :anyKey")
    Page<Subscriber> getAllByAnyKey(@Param("anyKey") String anyKey, Pageable pageable);

    @Query("select a from Subscriber a where a.user.activated = true and a.user.userType = :userType")
    List<Subscriber> getAllActivated(@Param("userType") UserType userType);

    @Query("select a from Subscriber a where a.id = :id and a.user.userType = :userType")
    Subscriber get(@Param("id") Long id, @Param("userType") UserType userType);

    @Query("select a from Subscriber a where a.user.id = :userId and a.user.activated = true")
    Subscriber getByUser(@Param("userId") Long userId);

    @Query("select a.code from Subscriber a")
    List<String> getAllSubscriberCode();

    @Query("select a from Subscriber a where a.code = :code")
    Subscriber getByCode(@Param("code") String code);


    @Query("select a.id from Subscriber a where a.user.id = ?1")
    Long findIdByUserId(long userId);

    @Query("select a.code from Subscriber a where a.user.id = ?1")
    String findInstitutionCodeByUserId(long userId);

    @Query("select a.id from Subscriber a where a.user.id = ?1")
    Long findInstitutionIdByUserId(long userId);

    /*@Query(value = BASE_DTO_QUERY + " order by a.id desc")//, countQuery = "select count(a.id) from Subscriber a")
    Page<SubscriberDto> findAllStrippedDown(Pageable pageable);*/

    /*@Query(value = BASE_DTO_QUERY + " where a.createdAt between ?1 and ?2 order by a.id desc",
    countQuery = "select count(a.id) from Subscriber a where a.createdAt between ?1 and ?2")
    Page<SubscriberDto> findByDateRange(Date startDate, Date endDate, Pageable pageable);*/

    /*@Query(BASE_DTO_QUERY + " where a.createdAt between ?1 and ?2 order by a.id desc")
    List<SubscriberDto> findByDateRange(Date startDate, Date endDate);*/

    @Query(BASE_ALL_JSON_QUERY)


    Page<Subscriber> getAllJsonData(Pageable pageable);

    @Query(BASE_UNAPPROVED_QUERY)
    Page<Subscriber> getUnapprovedData(Pageable pageable);

    @Query(BASE_UNAPPROVED_QUERY + " and (a.code like ?1 or a.user.name.firstName like ?1 or a.user.name.lastName like ?1 or a.user.emailAddress like ?1 or a.user.emailAddress like ?1) ")
    Page<Subscriber> getUnapprovedData(String param, Pageable pageable);

    @Query(BASE_APPROVED_OR_DISAPPROVED_QUERY)
    Page<Subscriber> getApprovedOrDisapprovedData(boolean approvalStatus, Pageable pageable);

    @Query(BASE_APPROVED_OR_DISAPPROVED_QUERY + " and (a.code like ?2 or a.user.name.firstName like ?2 or a.user.name.lastName like ?2 or a.user.emailAddress like ?2 or a.user.emailAddress like ?2) ")
    Page<Subscriber> getApprovedOrDisapprovedData(boolean approvalStatus, String param, Pageable pageable);

    @Query("select n from Subscriber n where n.id = ?1 and n.makerChecker.approvalStatus is null")
    Subscriber getUnapprovedById(Long id);

    @Query("select a.code from Subscriber a where a.autoGeneratedCode = true ")
    List<String> getAutoGeneratedCodes();

    @Query("select a.code from Subscriber a ")
    List<String> getAllCodes();

    @Query("select count(a.id) from Subscriber a where a.code = ?1 ")
    long countOfSameCode(String code);

    @Query("select count(a.id) from Subscriber a where a.code = ?1 and a.id <> ?2")
    long countOfSameCode(String code, Long id);

    @Query("select a.user.emailAddress from Subscriber a where a.user.activated = true and a.makerCheckerType = 'AUTHORIZER'")
    List<String> getAllActiveAuthorizerEmailAddress();

    @Query("select a.user.emailAddress from Subscriber a where a.user.activated = true and a.makerCheckerType = 'AUTHORIZER' and a.code like ?1")
    List<String> getAllActiveAuthorizerEmailAddressByInstitutionCode(String institutionCode);

    @Query("select new ng.upperlink.nibss.cmms.dto.SubscriberResponse(a.id, a.user.name, a.code) from Subscriber a where a.id = ?1 ")
    List<SubscriberResponse> getAllBySubscriberInstitution(Long institutionId);

    @Query("select a from Subscriber a")
    List<Subscriber> getAllSubscriber();

   /* @Query("select a from Subscriber a join a.subscriberInstitution ag where ag.name = ?1 ")
    List<Subscriber> getSubscriberBySubscriberInstitutionName(String institutionName, Pageable pageable);*/

}