package ng.upperlink.nibss.cmms.service;


import ng.upperlink.nibss.cmms.embeddables.TransactionAmountPerChannel;
import ng.upperlink.nibss.cmms.enums.ServicesProvided;
import ng.upperlink.nibss.cmms.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by stanlee on 02/05/2018.
 */
@Service
public class TransactionReportService {
/*
    private TransactionReportRepo transactionReportRepo;
    private AgentService agentService;

    @Autowired
    public void setTransactionReportRepo(TransactionReportRepo transactionReportRepo) {
        this.transactionReportRepo = transactionReportRepo;
    }

    @Autowired
    public void setAgentService(AgentService agentService) {
        this.agentService = agentService;
    }

    public List<TransactionReport> saveBulk(List<TransactionReport> transactionReports){
        return transactionReportRepo.save(transactionReports);
    }

    public Page<TransactionReport> getAll(String code, Date from, Date to, Pageable pageable){


        if (from == null){
            from = new Date();
        }if (to == null){
            to = new Date();
        }
        to = CommonUtils.addDays(to, 1);

//        DateFormat df = new SimpleDateFormat(Format.date);

        return transactionReportRepo.findAll(from, to, code, pageable);
    }

    public List<TransactionReport> getAll(String code, Date from, Date to) {

        if (from == null){
            from = new Date();
        }if (to == null){
            to = new Date();
        }
        to = CommonUtils.addDays(to, 1);

//        DateFormat df = new SimpleDateFormat(Format.date);

        return transactionReportRepo.findAll(from, to, code);
    }

    public Page<TransactionReport> getAllByAgentCode(String code, String agentCode, Date from, Date to, Pageable pageable){


        if (from == null){
            from = new Date();
        }if (to == null){
            to = new Date();
        }
        to = CommonUtils.addDays(to, 1);

//        DateFormat df = new SimpleDateFormat(Format.date);

        return transactionReportRepo.findAllByAgentCode(from, to, code, agentCode, pageable);
    }

    public TransactionReport save(TransactionReport report){
        return transactionReportRepo.save(report);
    }

    public String validate(AgtMgrReportRequest request){
        Agent agentByCode = agentService.getByCode(request.getAgentCode());
        if (agentByCode == null){
            return "Invalid Agent code provided.";
        }

        if (!Objects.equals(agentByCode.getAgentManager().getCode(), request.getCode())){
            return "Invalid agent manager code provided. Ensure that Agent Code is own by the agent manager code provided";
        }

        return null;
    }

    public TransactionReport generate(AgtMgrReportRequest request){

        TransactionReport report = new TransactionReport();
        Agent agent = agentService.getByCode(request.getAgentCode());
        report.setAgent(agent);
        report.setAgentCode(agent.getCode());
        report.setAgentManagerInstitution(agent.getAgentManager().getAgentManagerInstitution());
        report.setCode(agent.getAgentManager().getCode());
        report.setTransactionDate(request.getTransactionDate());
        report.getTransactionAmountPerChannels().clear();
        report.getTransactionAmountPerChannels().add(new TransactionAmountPerChannel(ServicesProvided.BVN_ENROLMENT,request.getAmount()));
        report.setAgentLocation(new GPS("0","0"));
        return report;
    }

    public Page<TransactionReport> getTransactionReport(Date startDate, Date endDate, String param ,Pageable pageable){

        if ((startDate == null || endDate == null) && (param == null || param.trim().isEmpty()) ){
            return transactionReportRepo.getAll(pageable);
        }else {
            if ((startDate == null || endDate == null) && !param.trim().isEmpty()){
                return transactionReportRepo.getByParam("%"+param+"%",pageable);
            }else if (startDate != null && endDate != null && (param == null || param.trim().isEmpty())){
                return transactionReportRepo.getByDate(startDate, endDate, pageable);
            }else {
                return transactionReportRepo.getByDateAndParam(startDate, endDate, "%"+param+"%", pageable);
            }
        }
    }*/
}
