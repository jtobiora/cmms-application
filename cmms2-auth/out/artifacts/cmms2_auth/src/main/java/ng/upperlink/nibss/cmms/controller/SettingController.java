package ng.upperlink.nibss.cmms.controller;

import com.google.gson.Gson;
import ng.upperlink.nibss.cmms.dto.SettingDto;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.model.Settings;
import ng.upperlink.nibss.cmms.repo.SettingRepo;
import ng.upperlink.nibss.cmms.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by stanlee on 14/05/2018.
 */
@RestController
@RequestMapping("/setting")
public class SettingController {

    private SettingService settingService;

    public final static String AMOUNT = "AMOUNT";
    public final static String ALLOWED_VERSIONS = "ALLOWED_VERSIONS";
    public final static String ALLOWED_SOURCES= "ALLOWED_SOURCES";
    private final static String desktop[] = {"enrolment.settings.fingerprint.threshold.fake.dermalog",
                                             "enrolment.settings.fingerprint.threshold.nfiq.dermalog",ALLOWED_VERSIONS};

    @Autowired
    public void setSettingService(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping("/amount")
    public ResponseEntity getAmountSetting(){
        Settings settings = settingService.getSettingsByName(AMOUNT);
        if (settings == null) {
            settings = new Settings(AMOUNT, "20");
            settings = settingService.save(settings);
            return ResponseEntity.ok(settings);
        }
        return ResponseEntity.ok(settings);
    }

    @PutMapping("/amount")
    public ResponseEntity updateAmountSetting(@RequestParam int amount){
        Settings settings = settingService.getSettingsByName(AMOUNT);
        if (settings == null) {
            settings = new Settings(AMOUNT,"20");
        }
        settings.setValue(String.valueOf(amount));
        settings = settingService.save(settings);
        return ResponseEntity.ok(settings);
    }

    @GetMapping("/desktop")
    public ResponseEntity getSettings(){
        List<Settings> settings = settingService.getAll().stream().filter(s->!AMOUNT.equals(s.getName())).collect(Collectors.toList());
        if(settings.isEmpty())
            settings = settingService.save(Arrays.asList(new Settings[]{new Settings(desktop[0],"55"),new Settings(desktop[1],"3")}));
        return ResponseEntity.ok(settings);
    }

    @PostMapping("/desktop")
    public ResponseEntity postEnrolmentSetting(@RequestBody SettingDto settings){
        if (settings == null || settings.getName() == null || settings.getName().trim().isEmpty() || settings.getValue() == null || settings.getValue().trim().isEmpty()  )
            return ResponseEntity.status(400).body(new ErrorDetails("Setting properties not found."));
        return ResponseEntity.ok(settingService.save(new Settings(settings.getName(),settings.getValue())));
    }

    @PutMapping("/desktop")
    public ResponseEntity updateEnrolmentSetting(@RequestBody Settings settings){
        if (settings == null || settings.getName() == null
                || settings.getName().trim().isEmpty()
                || settings.getValue() == null || settings.getValue().trim().isEmpty())

            return ResponseEntity.status(400).body( new ErrorDetails("Setting properties not provided."));
        Settings localSetting = settingService.get(settings.getId());
        if(localSetting == null)
            return ResponseEntity.status(404).body(new ErrorDetails("Setting not found."));
        localSetting.setValue(settings.getValue());
        localSetting = settingService.save(localSetting);
        return ResponseEntity.ok(localSetting);
    }

    @GetMapping("/desktop/enrolment-settings")
    public ResponseEntity fromDesktop(){
         return ResponseEntity.ok(
                 settingService.getAllByNames(Arrays.asList(desktop))
                         .stream()
                         .map(s->new SettingDto(s.getName(),s.getValue()))
                         .collect(Collectors.toList()));
    }
}
