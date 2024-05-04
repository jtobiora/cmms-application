package ng.upperlink.nibss.cmms.util;

import com.google.common.io.Files;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.List;

/**
 * Created by stanlee on 09/01/2018.
 */
@Service
public abstract class ExcelOperations implements IExcelOperation {

    private final static String EXCEL_FILE_NAME = "creation_template.xls";
    private static final Logger logger = LoggerFactory.getLogger(ExcelOperations.class);

    private String agtMgtCode;


    public File generateExcelFile(@Valid @NotNull(message = "code is required") String code) throws Exception {

        //get the
        agtMgtCode = code;
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        createHeaderRowAndDropDownConstants(workbook, sheet);

        createAllCustomCell(workbook);

        File file = new File(EXCEL_FILE_NAME);

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            workbook.write(outputStream);
        }

        return file;
    }

    public void writeDropDown(Workbook workbook, Sheet sheet) throws Exception {

        //Set the agentCodes

       /* logger.info("The code is {}", agtMgtCode);
        logger.info("The agentService is {}", agentService);

        List<String> agentCodes = agentService.getAllAgentCode(agtMgtCode);

        dropDownList(workbook, sheet, agentCodes,3, "agentCodes");*/

    }

    public void setCellStyle(Sheet sheet){
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(false);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
    }

    public void createHeaderRowAndDropDownConstants(Workbook workbook,Sheet sheet) throws Exception{

        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        cellStyle.setFont(font);

        Row row = sheet.createRow(0);
        row.setRowStyle(cellStyle);
        setTitle(row);

        writeDropDown(workbook,sheet);

    }

    public void createAllCustomCell(Workbook workbook){
        setCellAsDate(workbook, 1);//TRANSACTION DATE
        setCellAsString(workbook, 2);//AMOUNT
        setCellAsString(workbook, 3);//AGENT CODE
    }

    public void setTitle(Row row){
        row.createCell(1).setCellValue("TRANSACTION DATE");//Date
        row.createCell(2).setCellValue("AMOUNT");
        row.createCell(3).setCellValue("AGENT CODE");
    }

    public void dropDownList(Workbook workbook, Sheet sheet, List<String> contents, int cellNumber, String typeName){
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, cellNumber, cellNumber);
        if(contents.size()==0)
            contents.add("NA");

        int size = contents.size();
                //create another sheet
        Sheet newSheet = workbook.createSheet(typeName);
        for (int i = 0; i < size; i++) {
            Row row = newSheet.createRow(i);
            row.createCell(0).setCellValue(contents.get(i));
        }

        //Use namedCell
        Name namedCell = workbook.createName();

        namedCell.setNameName(typeName+"1");
        namedCell.setRefersToFormula("'"+typeName+"'!$A$1:$A$" + size);
        DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(typeName+"1");

        DataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);

        dataValidation.setSuppressDropDownArrow(false);
        sheet.addValidationData(dataValidation);

        //hide the sheet
        workbook.setSheetHidden(workbook.getSheetIndex(newSheet),true);

    }

    public String validateFile(MultipartFile file){

        //get the file
        if (file == null){
            return "file is null";
        }

        if (!"application/vnd.ms-excel".equalsIgnoreCase(file.getContentType())){
            return "Not an excel file";
        }

        return null;
    }

    public void setCellAsDate(Workbook workbook, int cellNumber){

        Sheet sheetAt = workbook.getSheetAt(0);

        CellStyle cellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();

        // Set the date format of date
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

        //1000 rows starting from row 1
        for (int i = 1; i < 1000; i++) {
            Cell cell = sheetAt.createRow(i).createCell(cellNumber);
            cell.setCellStyle(cellStyle);
        }

    }

    public void setCellAsString(Workbook workbook, int cellNumber){

        Sheet sheetAt = workbook.getSheetAt(0);

        DataFormat fmt = workbook.createDataFormat();
        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setDataFormat(fmt.getFormat("@"));
        //1000 rows starting from row 1
        for (int i = 1; i < 1000; i++) {
            Cell cell = sheetAt.createRow(i).createCell(cellNumber);
            cell.setCellType(CellType.STRING);
            sheetAt.setDefaultColumnStyle(cellNumber, textStyle);
        }

    }

    public ResponseEntity<InputStreamResource> getGeneratedExcelFile(String code) throws Exception{
        File excelFile = generateExcelFile(code);

        return ResponseEntity
                .ok()
                .contentLength(new Double(excelFile.length()).longValue())
                .contentType(
                        MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(new ByteArrayInputStream(Files.toByteArray(excelFile))));
    }

    public ResponseEntity<InputStreamResource> bulkCreation(MultipartFile file, String downloadFileName, Object object) throws Exception{

        String error = validateFile(file);
        String messageName = "message";
        if (error != null){
            return ResponseEntity.badRequest().header(messageName,error).body(null);
        }

        InputStream inputStream =  new BufferedInputStream(file.getInputStream());
        Workbook workbook = WorkbookFactory.create(inputStream);
        int totalRowCount = 0;

        List<Object> createdObjects = generateAndSaveObjects(workbook, totalRowCount, object);

        inputStream.close();

        File finalExcelFile = new File(downloadFileName+".xls");

        //response with stat and file
        try (FileOutputStream outputStream = new FileOutputStream(finalExcelFile)) {
            workbook.write(outputStream);
        }

        return ResponseEntity
                .ok()
                .header(messageName, "Successfully created "+createdObjects.size()+" out of "+totalRowCount)
                .contentLength(new Double(finalExcelFile.length()).longValue())
                .contentType(
                        MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(new ByteArrayInputStream(Files.toByteArray(finalExcelFile))));
    }


}
