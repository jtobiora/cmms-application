package ng.upperlink.nibss.cmms.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import ng.upperlink.nibss.cmms.dto.WebAppAuditEntryDto;
import ng.upperlink.nibss.cmms.model.WebAppAuditEntry;
import ng.upperlink.nibss.cmms.repo.WebAppAuditEntryRepo;

@Service
public class WebAppAuditEntryService1 {

    private WebAppAuditEntryRepo repo;

    public WebAppAuditEntryService1(final WebAppAuditEntryRepo repo) {

        this.repo = repo;
    }

    public Page<WebAppAuditEntryDto> findAllStrippedDown(Pageable pageable) {
        synchronized (new Object()) {
            return repo.findAllStrippedDown(pageable);
        }
    }

    public Page<WebAppAuditEntryDto> findByDateRange(Date startDate, Date endDate, Pageable pageable) {
        synchronized (new Object()) {
            return repo.findByDateRange(startDate, endDate,pageable);
        }
    }

    public Page<WebAppAuditEntryDto> findByUser(String username, Pageable pageable) {
        synchronized (new Object()) {
            return repo.findByUser(username, pageable);
        }
    }

    public Page<WebAppAuditEntryDto> findByUserAndDateRange(String username, Date startDate, Date endDate, Pageable pageable) {
        synchronized (new Object()) {
            return  repo.findByUserAndDateRange(username, startDate, endDate, pageable);
        }
    }

    public Page<WebAppAuditEntryDto> findByClassName(String className, Pageable pageable) {
        synchronized (new Object()) {
            return repo.findByClassName(className, pageable);
        }
    }

    public Page<WebAppAuditEntryDto> findByClassNameAndDateRange(String className, Date startDate, Date endDate, Pageable pageable) {
        synchronized (new Object()) {
            return repo.findByClassNameAndDateRange(className, startDate, endDate, pageable);
        }
    }


    public Page<WebAppAuditEntryDto> findByUserAndClassName(String username, String className, Pageable pageable) {
        synchronized (new Object()) {
            return repo.findByUserAndClassName(username, className, pageable);
        }
    }

    public Page<WebAppAuditEntryDto> findByUserAndClassNameAndDateRange(String username, String className, Date startDate,
                                                                         Date endDate, Pageable pageable) {
        synchronized ( new Object()) {
            return repo.findByUserAndClassNameAndDateRange(username, className, startDate ,endDate, pageable);
        }
    }

    public WebAppAuditEntryDto findByIdStrippedDown(long id) {
        synchronized (new Object()) {
            return repo.findByIdStrippedDown(id);
        }
    }

    public WebAppAuditEntry save(final WebAppAuditEntry entry) {
        synchronized (new Object()) {
            return repo.save(entry);
        }
    }
}
