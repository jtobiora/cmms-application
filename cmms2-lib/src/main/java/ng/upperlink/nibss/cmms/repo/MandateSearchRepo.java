package ng.upperlink.nibss.cmms.repo;

import ng.upperlink.nibss.cmms.enums.Channel;
import ng.upperlink.nibss.cmms.enums.MandateCategory;
import ng.upperlink.nibss.cmms.enums.MandateRequestType;
import ng.upperlink.nibss.cmms.model.MandateType;
import ng.upperlink.nibss.cmms.model.mandate.Mandate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface MandateSearchRepo extends JpaRepository<Mandate, Long> {

    //mandate STARTDATE and ENDDATE are empty
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName,
                                Pageable pageable,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.channel = :channel " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, @Param("channel") Channel channel,
                                Pageable pageable,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress,@Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateType = :mandateType " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, @Param("mandateType") MandateRequestType mandateType,
                                Pageable pageable,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress,@Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateCategory = :mandateCategory " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, @Param("mandateCategory") MandateCategory mandateCategory,
                                Pageable pageable,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress,@Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateType = :mandateType " +
            "AND m.channel = :channel " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, @Param("mandateType") MandateRequestType mandateType,
                                @Param("channel") Channel channel,Pageable pageable,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress,@Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.channel = :channel " +
            "AND m.mandateCategory = :mandateCategory " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName,  @Param("channel") Channel channel,
                                @Param("mandateCategory") MandateCategory mandateCategory,Pageable pageable,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress,@Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateCategory = :mandateCategory " +
            "AND m.mandateType = :mandateType " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName,  @Param("mandateCategory") MandateCategory mandateCategory,
                                @Param("mandateType") MandateRequestType mandateType,Pageable pageable,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress,@Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateCategory = :mandateCategory " +
            "AND m.mandateType = :mandateType " +
            "AND m.channel = :channel " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName,  @Param("mandateCategory") MandateCategory mandateCategory,
                                @Param("mandateType") MandateRequestType mandateType,
                                @Param("channel") Channel channel,Pageable pageable,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress,@Param("scheduleTime") String scheduleTime);


    //mandate STARTDATE is empty but ENDDATE is not empty
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% AND m.status.name LIKE %:mandateStatus% AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% AND m.accountNumber LIKE %:accountNumber% AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.endDate = :endDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, @Param("endDate") Date endDate, Pageable pageable,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.channel = :channel " +
            "AND m.endDate = :endDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, @Param("channel") Channel channel,
                                @Param("endDate") Date endDate,Pageable pageable,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateType = :mandateType " +
            "AND m.endDate = :endDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, @Param("mandateType") MandateRequestType mandateType,
                                @Param("endDate") Date endDate,Pageable pageable,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateCategory = :mandateCategory " +
            "AND m.endDate = :endDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, @Param("mandateCategory") MandateCategory mandateCategory,
                                @Param("endDate") Date endDate,Pageable pageable,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateType = :mandateType " +
            "AND m.channel = :channel " +
            "AND m.endDate = :endDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, @Param("mandateType") MandateRequestType mandateType,
                                @Param("channel") Channel channel,
                                @Param("endDate") Date endDate,Pageable pageable,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.channel = :channel " +
            "AND m.mandateCategory = :mandateCategory " +
            "AND m.endDate = :endDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName,  @Param("channel") Channel channel,
                                @Param("mandateCategory") MandateCategory mandateCategory,
                                @Param("endDate") Date endDate,Pageable pageable,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateCategory = :mandateCategory " +
            "AND m.mandateType = :mandateType " +
            "AND m.endDate = :endDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName,  @Param("mandateCategory") MandateCategory mandateCategory,
                                @Param("mandateType") MandateRequestType mandateType,
                                @Param("endDate") Date endDate,Pageable pageable,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateCategory = :mandateCategory " +
            "AND m.mandateType = :mandateType " +
            "AND m.channel = :channel " +
            "AND m.endDate = :endDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName,  @Param("mandateCategory") MandateCategory mandateCategory,
                                @Param("mandateType") MandateRequestType mandateType,
                                @Param("channel") Channel channel,
                                @Param("endDate") Date endDate,Pageable pageable,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);


    //mandate ENDDATE is empty but STARTDATE is not empty
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% AND m.status.name LIKE %:mandateStatus% AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% AND m.accountNumber LIKE %:accountNumber% AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.startDate = :startDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, Pageable pageable,@Param("startDate") Date startDate,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.channel = :channel " +
            "AND m.startDate = :startDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, @Param("channel") Channel channel,
                                Pageable pageable,@Param("startDate") Date startDate,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateType = :mandateType " +
            "AND m.startDate = :startDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, @Param("mandateType") MandateRequestType mandateType,
                                Pageable pageable,@Param("startDate") Date startDate,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateCategory = :mandateCategory " +
            "AND m.startDate = :startDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, @Param("mandateCategory") MandateCategory mandateCategory,
                                Pageable pageable,@Param("startDate") Date startDate,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateType = :mandateType " +
            "AND m.channel = :channel " +
            "AND m.startDate = :startDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, @Param("mandateType") MandateRequestType mandateType,
                                @Param("channel") Channel channel,
                                Pageable pageable,@Param("startDate") Date startDate,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.channel = :channel " +
            "AND m.mandateCategory = :mandateCategory " +
            "AND m.startDate = :startDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName,  @Param("channel") Channel channel,
                                @Param("mandateCategory") MandateCategory mandateCategory,
                                Pageable pageable,@Param("startDate") Date startDate,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateCategory = :mandateCategory " +
            "AND m.mandateType = :mandateType " +
            "AND m.startDate = :startDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName,  @Param("mandateCategory") MandateCategory mandateCategory,
                                @Param("mandateType") MandateRequestType mandateType,
                                Pageable pageable,@Param("startDate") Date startDate,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateCategory = :mandateCategory " +
            "AND m.mandateType = :mandateType " +
            "AND m.channel = :channel " +
            "AND m.startDate = :startDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName,  @Param("mandateCategory") MandateCategory mandateCategory,
                                @Param("mandateType") MandateRequestType mandateType,
                                @Param("channel") Channel channel,
                                Pageable pageable,@Param("startDate") Date startDate,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);


    ////
    //mandate ENDDATE and STARTDATE are not empty
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% AND m.status.name LIKE %:mandateStatus% AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% AND m.accountNumber LIKE %:accountNumber% AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.startDate = :startDate " +
            "AND m.endDate = :endDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, Pageable pageable,@Param("startDate") Date startDate,@Param("endDate") Date endDate,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.channel = :channel " +
            "AND m.startDate = :startDate " +
            "AND m.endDate = :endDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, @Param("channel") Channel channel,
                                Pageable pageable,@Param("startDate") Date startDate,@Param("endDate") Date endDate,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateType = :mandateType " +
            "AND m.startDate = :startDate " +
            "AND m.endDate = :endDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, @Param("mandateType") MandateRequestType mandateType,
                                Pageable pageable,@Param("startDate") Date startDate,@Param("endDate") Date endDate,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateCategory = :mandateCategory " +
            "AND m.startDate = :startDate " +
            "AND m.endDate = :endDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, @Param("mandateCategory") MandateCategory mandateCategory,
                                Pageable pageable,@Param("startDate") Date startDate,@Param("endDate") Date endDate,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateType = :mandateType " +
            "AND m.channel = :channel " +
            "AND m.startDate = :startDate " +
            "AND m.endDate = :endDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName, @Param("mandateType") MandateRequestType mandateType,
                                @Param("channel") Channel channel,
                                Pageable pageable,@Param("startDate") Date startDate,@Param("endDate") Date endDate,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.channel = :channel " +
            "AND m.mandateCategory = :mandateCategory " +
            "AND m.startDate = :startDate " +
            "AND m.endDate = :endDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName,  @Param("channel") Channel channel,
                                @Param("mandateCategory") MandateCategory mandateCategory,
                                Pageable pageable,@Param("startDate") Date startDate,@Param("endDate") Date endDate,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateCategory = :mandateCategory " +
            "AND m.mandateType = :mandateType " +
            "AND m.startDate = :startDate " +
            "AND m.endDate = :endDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName,  @Param("mandateCategory") MandateCategory mandateCategory,
                                @Param("mandateType") MandateRequestType mandateType,
                                Pageable pageable,@Param("startDate") Date startDate,@Param("endDate") Date endDate,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

    @Query("select m from Mandate m where m.mandateCode LIKE %:mandateCode% " +
            "AND m.status.name LIKE %:mandateStatus% " +
            "AND m.subscriberCode LIKE %:subscriberCode% " +
            "AND m.accountName LIKE %:accountName% " +
            "AND m.accountNumber LIKE %:accountNumber% " +
            "AND m.bvn LIKE %:bvn% " +
            "AND m.email LIKE %:email% " +
            "AND m.bank.code LIKE %:bankCode% " +
            "AND m.product.name LIKE %:productName% " +
            "AND m.mandateCategory = :mandateCategory " +
            "AND m.mandateType = :mandateType " +
            "AND m.channel = :channel " +
            "AND m.startDate = :startDate " +
            "AND m.endDate = :endDate " +
            "AND m.payerName LIKE %:payerName% " +
            "AND m.payerAddress LIKE %:payerAddress% " +
            "AND m.scheduleTime LIKE %:scheduleTime%")
    Page<Mandate> searchMandate(@Param("mandateCode") String mandateCode, @Param("mandateStatus") String mandateStatus,
                                @Param("subscriberCode") String subscriberCode, @Param("accountName") String accountName,
                                @Param("accountNumber") String accountNumber, @Param("bvn") String bvn,
                                @Param("email") String email, @Param("bankCode") String bankCode,
                                @Param("productName") String productName,  @Param("mandateCategory") MandateCategory mandateCategory,
                                @Param("mandateType") MandateRequestType mandateType,
                                @Param("channel") Channel channel,
                                Pageable pageable,@Param("startDate") Date startDate,@Param("endDate") Date endDate,@Param("payerName") String payerName, @Param("payerAddress") String payerAddress, @Param("scheduleTime") String scheduleTime);

}

