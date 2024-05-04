package ng.upperlink.nibss.cmms.service.mandateImpl;

import ng.upperlink.nibss.cmms.enums.Channel;
import ng.upperlink.nibss.cmms.enums.MandateCategory;
import ng.upperlink.nibss.cmms.enums.MandateRequestType;
import ng.upperlink.nibss.cmms.model.mandate.Mandate;
import ng.upperlink.nibss.cmms.repo.MandateSearchRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MandateSearchService {

    private MandateSearchRepo mandateSearchRepo;

    @Autowired
    public void setMandateSearchRepo(MandateSearchRepo mandateSearchRepo) {
        this.mandateSearchRepo = mandateSearchRepo;
    }

    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    //*****************//mandate STARTDATE and ENDDATE are empty************
    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, Pageable pageable,String payerName,String payerAddress,String scheduleTime) {
        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, pageable,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, Channel channel, Pageable pageable,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, channel, pageable,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateRequestType mandateType, Pageable pageable,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateType, pageable,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateCategory mandateCategory, Pageable pageable,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateCategory, pageable,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateRequestType mandateType, Channel channel, Pageable pageable,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateType, channel, pageable,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, Channel channel, MandateCategory mandateCategory, Pageable pageable,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, channel, mandateCategory, pageable,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateCategory mandateCategory, MandateRequestType mandateType, Pageable pageable,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateCategory, mandateType, pageable,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateCategory mandateCategory, MandateRequestType mandateType, Channel channel, Pageable pageable,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateCategory, mandateType, channel, pageable,payerName,payerAddress,scheduleTime);
    }


    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    //*****************//mandate STARTDATE is empty but ENDDATE is not empty************
    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, Date endDate, Pageable pageable,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, endDate, pageable,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, Channel channel, Date endDate, Pageable pageable,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, channel, endDate, pageable,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateRequestType mandateType, Date endDate, Pageable pageable,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateType, endDate, pageable,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateCategory mandateCategory, Date endDate, Pageable pageable,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateCategory, endDate, pageable,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateRequestType mandateType, Channel channel, Date endDate, Pageable pageable,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateType, channel, endDate, pageable,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, Channel channel, MandateCategory mandateCategory, Date endDate, Pageable pageable,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, channel, mandateCategory, endDate, pageable,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateCategory mandateCategory, MandateRequestType mandateType, Date endDate, Pageable pageable,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateCategory, mandateType, endDate, pageable,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateCategory mandateCategory, MandateRequestType mandateType, Channel channel, Date endDate, Pageable pageable,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateCategory, mandateType, channel, endDate, pageable,payerName,payerAddress,scheduleTime);
    }


    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    //*****************//mandate ENDDATE is empty but STARTDATE is not empty***********************
    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, Pageable pageable, Date startDate,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, pageable,startDate,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, Channel channel, Pageable pageable,Date startDate,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, channel, pageable,startDate,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateRequestType mandateType, Pageable pageable,Date startDate,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateType, pageable,startDate,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateCategory mandateCategory,Pageable pageable,Date startDate,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateCategory, pageable,startDate,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateRequestType mandateType, Channel channel, Pageable pageable,Date startDate,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateType, channel,pageable,startDate,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, Channel channel, MandateCategory mandateCategory, Pageable pageable,Date startDate,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, channel, mandateCategory, pageable,startDate,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateCategory mandateCategory, MandateRequestType mandateType, Pageable pageable,Date startDate,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateCategory, mandateType, pageable,startDate,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateCategory mandateCategory, MandateRequestType mandateType, Channel channel,Pageable pageable, Date startDate,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateCategory, mandateType, channel,pageable,startDate,payerName,payerAddress,scheduleTime);
    }


    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    //*****************//mandate ENDDATE and STARTDATE are not empty***********************
    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, Pageable pageable, Date startDate,Date endDate,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, pageable,startDate,endDate,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, Channel channel, Pageable pageable,Date startDate,Date endDate,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, channel, pageable,startDate,endDate,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateRequestType mandateType, Pageable pageable,Date startDate,Date endDate,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateType, pageable,startDate,endDate,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateCategory mandateCategory,Pageable pageable,Date startDate,Date endDate,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateCategory, pageable,startDate,endDate,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateRequestType mandateType, Channel channel, Pageable pageable,Date startDate,Date endDate,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateType, channel,pageable,startDate,endDate,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, Channel channel, MandateCategory mandateCategory, Pageable pageable,Date startDate,Date endDate,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, channel, mandateCategory, pageable,startDate,endDate,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateCategory mandateCategory, MandateRequestType mandateType, Pageable pageable,Date startDate,Date endDate,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateCategory, mandateType, pageable,startDate,endDate,payerName,payerAddress,scheduleTime);
    }

    public Page<Mandate> searchMandates(String mandateCode, String mandateStatus, String subscriberCode, String accName, String accNumber, String bvn, String email, String bankCode, String productName, MandateCategory mandateCategory, MandateRequestType mandateType, Channel channel,Pageable pageable, Date startDate,Date endDate,String payerName,String payerAddress,String scheduleTime) {

        return mandateSearchRepo.searchMandate(mandateCode, mandateStatus, subscriberCode, accName, accNumber, bvn, email, bankCode, productName, mandateCategory, mandateType, channel,pageable,startDate,endDate,payerName,payerAddress,scheduleTime);
    }

}
