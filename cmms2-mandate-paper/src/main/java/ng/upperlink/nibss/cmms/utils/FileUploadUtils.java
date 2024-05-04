package ng.upperlink.nibss.cmms.utils;

import ng.upperlink.nibss.cmms.enums.Errors;
import ng.upperlink.nibss.cmms.errorHandler.ErrorDetails;
import ng.upperlink.nibss.cmms.errorHandler.InvalidFileException;
import ng.upperlink.nibss.cmms.exceptions.ServerBusinessException;
import ng.upperlink.nibss.cmms.model.Mandate;
import ng.upperlink.nibss.cmms.model.User;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.SizeLimitExceededException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileUploadUtils {
    @Value("${upload.mandate.extensions}")
    private String validMandateFileExtensions;

    @Value("${file.maxSize}")
    private Long maxFileSize;

    @Value("${file.rootLocation}")
    private String uploadPath;

    Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private final Path rootLocation = Paths.get("upload-dir");

    public String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex < 0) {
            return null;
        }
        return fileName.substring(dotIndex + 1);
    }

    public boolean isValidExtension(String fileName) throws InvalidFileException {
        String fileExtension = getFileExtension(fileName);

        if (fileExtension == null) {
            throw new InvalidFileException("No File Extension");
        }

        fileExtension = fileExtension.toLowerCase();

        for (String validExtension : validMandateFileExtensions.split(",")) {
            if (fileExtension.equals(validExtension)) {
                return true;
            }
        }
        return false;
    }

    public ResponseEntity validateFileToUpload(MultipartFile file) throws Exception{
        String fileName = file.getOriginalFilename();
        String cleanFileName = fileName.replaceAll("[^A-Za-z0-9.()]", "");
        //check for valid extensions
        if(!isValidExtension(cleanFileName)) {
            return new ResponseEntity("Invalid File Extension", HttpStatus.NOT_ACCEPTABLE);
        };

        //limit size to 50 Mb
        if(file.getSize() > maxFileSize) {
            return new ResponseEntity("File Size Exceeded", HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    public ResponseEntity uploadFile(MultipartFile file, String mandateCode, HttpServletRequest request, Long userId, Mandate mandate) throws IOException{

        if (mandateCode.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.INVALID_DATA_PROVIDED.getValue()));
        }

        String tempDir = request.getServletContext().getRealPath("/temp") + File.separator
                + userId + File.separator + System.currentTimeMillis();


        String fileName = mandateCode.replace("/", "") + "_0."
                + FilenameUtils.getExtension(file.getOriginalFilename());

        File tempDestination = new File(tempDir);

        log.info("Destination " + tempDir);
        log.info("Extension " + FilenameUtils.getExtension(file.getOriginalFilename()));

        if (!tempDestination.exists()) {
            boolean made = tempDestination.mkdirs(); // create the new
            // temp path
            log.info("---tempDestination.mkdirs()--" + made);
        }
        // transfer the file to the temp path
        file.transferTo(new File(tempDir + File.separator + file.getOriginalFilename()));

         //set the mandate image
        mandate.setMandateImage(fileName);
        return null;
    }

}
