package ng.upperlink.nibss.cmms.service;

import ng.upperlink.nibss.cmms.dto.AuditRequest;
import ng.upperlink.nibss.cmms.model.Audit;
import ng.upperlink.nibss.cmms.repo.AuditRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class AuditService {

    private AuditRepo auditRepo;

    private Logger LOG = LoggerFactory.getLogger(AuditService.class);

    @Autowired
    public void setAuditRepo(AuditRepo auditRepo) {
        this.auditRepo = auditRepo;
    }

    public Audit save(Audit audit) {
        return auditRepo.save(audit);
    }

    public List<Audit> save(List<AuditRequest> auditRequests) {

        List<Audit> audits = new ArrayList<>();
        for (AuditRequest auditRequest : auditRequests) {
            Audit audit = generate(auditRequest);
            if (audit == null) {
                continue;
            }
            audits.add(audit);
        }
        return auditRepo.save(audits);
    }

    public Audit generate(AuditRequest request) {

        try {
            return new Audit(request.getMacAddress(), request.getIpAddress(),
                    request.getUserName(), request.getAction(),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(request.getCreatedAt()),
                    request.getAgentCode());
        } catch (ParseException e) {
            LOG.error("ParseException => {}", e);
        }

        return null;
    }


    public Page<AuditRequest> findByDateRange(Date startDate, Date endDate, Pageable pageable) {
        synchronized (new Object()) {
            return auditRepo.findByDateRange(startDate, endDate, pageable);
        }
    }

    public Page<AuditRequest> findByAgentCodeAndDateRange(String agentCode, Date startDate,
                                                          Date endDate, Pageable pageable) {

        synchronized (new Object()) {
            return auditRepo.findByAgentCodeAndDateRange(agentCode, startDate, endDate, pageable);
        }
    }

    public Page<AuditRequest> findByUserNameAndDateRange(String userName, Date startDate,
                                                         Date endDate, Pageable pageable) {
        synchronized (new Object()) {
            return auditRepo.findByUsernameAndDateRange(userName, startDate, endDate, pageable);
        }
    }

    public Page<AuditRequest> findByUsernameAndAgentCodeAndDateRange(String username, String agentCode,
                                                                     Date startDate, Date endDate, Pageable pageable) {
        synchronized (new Object()) {
            return auditRepo.findByUsernameAndAgentCodeAndDateRange(username, agentCode, startDate, endDate, pageable);
        }
    }

    public Page<AuditRequest> findAllTrimmedDown(Pageable pageable) {
        synchronized (new Object()) {
            return auditRepo.findAllTrimmedDown(pageable);
        }
    }

    public Page<AuditRequest> findAllByUsernameTrimmedDown(String userName, Pageable pageable) {
        synchronized (new Object()) {
            return auditRepo.findAllByUsernameTrimmedDown(userName, pageable);
        }
    }

    public Page<AuditRequest> findAllByAgentCodeTrimmedDown(String agentCode, Pageable pageable) {
        synchronized (new Object()) {
            return auditRepo.findAllByAgentCodeTrimmedDown(agentCode, pageable);
        }
    }

    public Page<AuditRequest> findAllByUsernameAndAgentCodeTrimmedDown(String userName, String agentCode, Pageable pageable) {
        synchronized (new Object()) {
            return auditRepo.findAllByUsernameAndAgentCodeTrimmedDown(userName, agentCode, pageable);
        }
    }
}
