package ng.upperlink.nibss.cmms.util;

import ng.upperlink.nibss.cmms.dto.UserDetail;
import ng.upperlink.nibss.cmms.dto.mandates.MandateRequest;
import ng.upperlink.nibss.cmms.errorHandler.InvalidFileException;
import ng.upperlink.nibss.cmms.model.mandate.Mandate;
import ng.upperlink.nibss.cmms.service.bank.BankService;
import ng.upperlink.nibss.cmms.service.biller.BillerService;
import ng.upperlink.nibss.cmms.service.biller.ProductService;
import ng.upperlink.nibss.cmms.service.mandateImpl.MandateStatusService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

@Component
public class FileUploadUtils {
    @Value("${file.maxSize}")
    private Long maxFileSize;
    private MandateStatusService mandateStatusService;
    private BillerService billerService;
    private BankService bankService;
    private ProductService productService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setBillerService(BillerService billerService) {
        this.billerService = billerService;
    }

    @Autowired
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }


    @Autowired
    public void setMandateStatusService(MandateStatusService mandateStatusService) {
        this.mandateStatusService = mandateStatusService;
    }

    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex < 0) {
            return null;
        }
        return fileName.substring(dotIndex + 1);
    }

    public boolean isValidFileExtension(String ext){
        String[] validExtensions = {".jpg",".pdf", ".png", ".jpeg",".zip"};
        return Arrays.asList(validExtensions).contains(ext);
    }

    public ResponseEntity validateFileToUpload(MultipartFile file, String extension) throws Exception{
        //check for valid extensions
        if(!isValidFileExtension(extension)) {
            throw new InvalidFileException("Invalid File Extension! Allowed extensions are .jpg, .pdf, .png, .jpeg and .zip");
        };

        if(file.getSize() > maxFileSize) {
            throw new InvalidFileException("File Size Exceeded!");
        }
        return null;
    }

    public ResponseEntity uploadFile(MultipartFile file, String mandateCode, HttpServletRequest request, Long userId, Mandate mandate, String ext) throws IOException,Exception {

        if (mandateCode.isEmpty()) {
            throw new InvalidFileException("Invalid Mandate Code!");
        }

        String tempDir = request.getServletContext().getRealPath("/mandateUploads") + File.separator;

        File tempDestination = new File(tempDir);

        log.info("Destination " + tempDir);

        if (!tempDestination.exists()) {
            boolean made = tempDestination.mkdirs(); // create the new
            // temp path
            log.info("---tempDestination.mkdirs()--" + made);
        }
        // transfer the file to the temp path
        file.transferTo(new File(tempDir + File.separator + file.getOriginalFilename()));
//
//        //set the mandate image
        mandate.setMandateImage(file.getOriginalFilename());

        return null;
    }

    public void processUploads(MandateRequest requestObject,
                               HttpServletRequest servletRequest, String mandateCode, UserDetail userDetail, Mandate mandate,String imgString) throws IOException, Exception{

        String fileInBase64String = "";
        if(StringUtils.isEmpty(imgString)){
            fileInBase64String = requestObject.getUploadImage();
        }else {
            fileInBase64String = imgString;
        }

        //split the string and remove headers
        String[] strings = fileInBase64String.split(",");
        String ext;
        switch (strings[0]) {//check image's extension
            case "data:image/jpeg;base64":
                ext = ".jpeg";
                break;
            case "data:image/jpg;base64":
                ext = ".jpg";
                break;
            case "data:image/pdf;base64":
                ext = ".pdf";
                break;
            case "data:image/png;base64":
                ext = ".png";
                break;
            default:
                ext = null;
                break;
        }

        byte[] fileByteArray = Base64.decodeBase64(strings[1]);
       // String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(fileByteArray));
        String fileName = userDetail.getUserId() + mandateCode.replace("/", "")  + System.currentTimeMillis()  + ext;
        BASE64DecodedMultipartFile multipartFile = new BASE64DecodedMultipartFile(fileByteArray,fileName);

        this.validateFileToUpload(multipartFile,ext);

        this.uploadFile(multipartFile, mandateCode, servletRequest, userDetail.getUserId(), mandate,ext);
    }

    public String decodeToBase64(Mandate mandate,HttpServletRequest request){
        String base64Image = "";
        String headerPath = "";
        String tempDir = request.getServletContext().getRealPath("/mandateUploads") + File.separator;

        File tempDestination = new File(tempDir);

        if (!tempDestination.exists()) {
            boolean made = tempDestination.mkdirs(); // create the new
            // temp path
            log.info("---tempDestination.mkdirs()--" + made);
        }

        log.info("Temporary Directory for File Uploads {} ",tempDir);

        String[] extension = StringUtils.split(mandate.getMandateImage(),".");
        if(extension[1].equalsIgnoreCase("png")){
            headerPath = "data:image/png;base64,";
        }else if(extension[1].equalsIgnoreCase("jpeg")){
            headerPath = "data:image/jpeg;base64,";
        }else if(extension[1].equalsIgnoreCase("jpg")){
            headerPath = "data:image/jpg;base64,";
        }else if(extension[1].equalsIgnoreCase("pdf")){
            headerPath = "data:image/pdf;base64,";
        }

        File file = new File(tempDir + File.separator + mandate.getMandateImage());
        ByteArrayOutputStream bos =new ByteArrayOutputStream();
        try(FileInputStream imageInFile = new FileInputStream(file)){
            byte[] imageBytes = new byte[(int)file.length()];
            imageInFile.read(imageBytes);
            base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
        }catch(Exception ex){
           log.error("Error thrown while encoding bytes --",ex);
        }

        return headerPath + base64Image;
    }

}
