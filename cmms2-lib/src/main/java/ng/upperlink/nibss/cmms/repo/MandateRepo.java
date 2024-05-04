package ng.upperlink.nibss.cmms.repo;


import ng.upperlink.nibss.cmms.enums.Channel;
import ng.upperlink.nibss.cmms.model.mandate.Mandate;
import ng.upperlink.nibss.cmms.model.mandate.MandateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@Repository
public interface MandateRepo extends JpaRepository<Mandate, Long> {

    //#######################  GENERIC_QUERY  ######################
    @Query("select m from Mandate m where m.requestStatus <> :deleted")
    Page<Mandate> getAllMandates(Pageable pageable, @Param("deleted") int deleted);

    @Query("select m from Mandate m where m.requestStatus <> :deleted")
    List<Mandate> getAllMandates(@Param("deleted") int deleted);

    @Query("select m from Mandate m where m.id = :id and m.requestStatus <> :deleted")
    Mandate getMandateByMandateId(@Param("id") Long id, @Param("deleted") int deleted);

    @Query("SELECT m FROM Mandate m WHERE m.dateCreated BETWEEN ?1 AND ?2 AND m.requestStatus <> ?3")
    List<Mandate> getMandatesYearToDate(Date startDate, Date today,int deleted);


    //############## NIBSS_MANDATE_QUERY ##########################
    @Query("select m from Mandate m where m.status.id= :statusId and (m.requestStatus <> :suspended and m.requestStatus <> :deleted)")
    Page<Mandate> getMandatesByNIBSSAndStatus(@Param("statusId") Long statusId,Pageable pageable, @Param("suspended") int suspended,
                                               @Param("deleted") int deleted);

    @Query("select m from Mandate m where m.requestStatus = :suspended")
    Page<Mandate> getSuspendedMandatesByNIBSS(@Param("suspended") int suspended, Pageable pageable);

    @Query("select m from Mandate m where m.status.id= :statusId and m.requestStatus <> :deleted")
    Page<Mandate> getMandatesByNIBSSAndStatus(@Param("statusId") Long statusId,Pageable pageable, @Param("deleted") int deleted);

    //############## BILLER_MANDATE_QUERY ##########################
    @Query("SELECT m FROM Mandate m WHERE m.dateCreated BETWEEN ?1 AND ?2 AND m.requestStatus <> ?3 AND m.mandateOwnerKey = ?4")
    List<Mandate> getBillerMandatesYearToDate(Date startDate, Date today,int deleted,String ownerKey);

    @Query("select m from Mandate m where m.mandateOwnerKey = :ownerKey and m.requestStatus <> :deleted and m.status.id IN :statusList")
    Page<Mandate> getMandatesByBillers(@Param("deleted") int deleted, @Param("ownerKey") String ownerKey, Pageable pageable, @Param("statusList") List<Long> statusList);

    @Query("select m from Mandate m where m.status.id= :statusId and m.mandateOwnerKey = :ownerKey and (m.requestStatus <> :suspended and m.requestStatus <> :deleted)")
    Page<Mandate> getMandatesByBillerAndStatus(@Param("statusId") Long statusId, @Param("ownerKey") String ownerKey, Pageable pageable, @Param("suspended") int suspended,
                                               @Param("deleted") int deleted);

    @Query("select m from Mandate m where m.status.id= :statusId and m.mandateOwnerKey = :ownerKey and m.requestStatus <> :deleted")
    Page<Mandate> getMandatesByBillerAndStatus(@Param("statusId") Long statusId, @Param("ownerKey") String ownerKey, Pageable pageable, @Param("deleted") int deleted);

    @Query("select m from Mandate m where m.mandateOwnerKey = :ownerKey and m.requestStatus = :suspended")
    Page<Mandate> getSuspendedMandatesByBiller(@Param("ownerKey") String ownerKey, @Param("suspended") int suspended, Pageable pageable);

    @Query("select m from Mandate m where m.id = :id and m.mandateOwnerKey = :ownerKey and m.requestStatus <> :deleted")
    Mandate getMandateByBillerAndMandateId(@Param("id") Long id, @Param("ownerKey") String ownerKey, @Param("deleted") int deleted);


    //############## PSSP_MANDATE_QUERY ##########################
    @Query("select m from Mandate m where m.biller.billerOwner = :ownerKey and m.requestStatus <> :deleted and m.status.id IN :statusList ")
    Page<Mandate> getMandatesByPSSP(@Param("deleted") int deleted, @Param("ownerKey") String ownerKey, Pageable pageable, @Param("statusList") List<Long> statusList);

    @Query("select m from Mandate m where m.id = :id and m.biller.billerOwner = :ownerKey and m.requestStatus <> :deleted")
    Mandate getMandateByPSSPAndMandateId(@Param("id") Long id, @Param("ownerKey") String ownerKey, @Param("deleted") int deleted);

    @Query("select m from Mandate m where m.status.id= :statusId and m.biller.billerOwner = :ownerKey and (m.requestStatus <> :suspended and m.requestStatus <> :deleted)")
    Page<Mandate> getMandatesByPSSPAndStatus(@Param("statusId") Long statusId, @Param("ownerKey") String ownerKey, Pageable pageable, @Param("suspended") int suspended,
                                               @Param("deleted") int deleted);

    @Query("select m from Mandate m where m.biller.billerOwner = :ownerKey and m.requestStatus = :suspended")
    Page<Mandate> getSuspendedMandatesByPSSP(@Param("ownerKey") String ownerKey, @Param("suspended") int suspended, Pageable pageable);

    @Query("select m from Mandate m where m.status.id= :statusId and m.biller.billerOwner = :ownerKey and m.requestStatus <> :deleted")
    Page<Mandate> getMandatesByPSSPAndStatus(@Param("statusId") Long statusId, @Param("ownerKey") String ownerKey, Pageable pageable, @Param("deleted") int deleted);



    //##############  BANK_MANDATE_QUERY #######################
    @Query("SELECT m FROM Mandate m WHERE m.dateCreated BETWEEN ?1 AND ?2 AND m.requestStatus <> ?3 AND m.mandateOwnerKey = ?4")
    List<Mandate> getBankMandatesYearToDate(Date startDate, Date today,int deleted,String ownerKey);

    @Query("select m from Mandate m where  m.mandateOwnerKey = :ownerKey AND m.requestStatus <> :deleted and m.status.id IN :statusIdList")
    Page<Mandate> getAllMandates(@Param("ownerKey") String ownerKey,Pageable pageable, @Param("deleted") int deleted,@Param("statusIdList") List<Long> statusIdList);

    @Query("select m from Mandate m where m.id = :id and m.mandateOwnerKey = :ownerKey and m.requestStatus <> :deleted")
    Mandate getMandateByBankAndMandateId(@Param("id") Long id, @Param("ownerKey") String ownerKey, @Param("deleted") int deleted);

    @Query("select m from Mandate m where (m.status.id= :statusId or m.status.id = :statId) and m.mandateOwnerKey = :ownerKey and (m.requestStatus <> :deleted and m.requestStatus <> :suspended)")
    Page<Mandate> getPendingMandatesByBanksAndStatus(@Param("statusId") Long statusId, @Param("statId") Long statId, @Param("ownerKey") String ownerKey,  Pageable pageable, @Param("deleted") int deleted,
                                                     @Param("suspended") int suspended);

    @Query("select m from Mandate m where m.status.id= :statusId and  m.mandateOwnerKey = :ownerKey and (m.requestStatus <> :deleted and m.requestStatus <> :suspended)")
    Page<Mandate> getMandatesByBanksAndStatus(@Param("statusId") Long statusId, @Param("ownerKey") String ownerKey, Pageable pageable, @Param("deleted") int deleted,
                                              @Param("suspended") int suspended);

    @Query("select m from Mandate m where m.mandateOwnerKey = :ownerKey and m.requestStatus = :suspended")
    Page<Mandate> getSuspendedMandatesByBank(@Param("ownerKey") String ownerKey, @Param("suspended") int suspended, Pageable pageable);

    @Query("select m from Mandate m where m.status.id= :statusId and m.mandateOwnerKey = :ownerKey and m.requestStatus <> :deleted")
    Page<Mandate> getMandatesAuthorizedByBanks(@Param("statusId") Long statusId, @Param("ownerKey") String ownerKey, Pageable pageable, @Param("deleted") int deleted);

    @Query("select max(m.id) from Mandate m")
    Long getMandateMaxId();
    
    // Get Daily Due Mandates
    @Query("SELECT m FROM Mandate m WHERE m.mandateAdviceSent = ?1 "
            + "AND m.nextDebitDate = ?2 AND m.requestStatus = ?3 AND m.channel = ?4 AND m.nextDebitDate <= m.endDate AND m.status in ?5")
    List<Mandate> getDueMandates(boolean mandateAdviceSent, Date nextDebitDate, int requestStatus, Channel channel, List<MandateStatus> mandateStatus);

    @Query("SELECT m from Mandate m where m.mandateCode=:mandateCode")
    Mandate findByMandateCode(@Param("mandateCode") String mandateCode);



}
