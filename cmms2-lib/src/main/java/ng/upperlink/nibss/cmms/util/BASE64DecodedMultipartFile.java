package ng.upperlink.nibss.cmms.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class BASE64DecodedMultipartFile implements MultipartFile {
    private byte[] imgContent;

    private String fileName;

    private String contentType;

    private File file;

    @Value("${file.rootLocation}")
    private String uploadPath;

    public BASE64DecodedMultipartFile(byte[] imgContent,String name) {
        this.imgContent = imgContent;
        this.fileName = name;
        file = new File(uploadPath + fileName);
    }

    @Override
    public String getName() {

        return null;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        // TODO - implementation depends on your requirements
        return null;
    }

    @Override
    public boolean isEmpty() {
        return imgContent == null || imgContent.length == 0;
    }

    @Override
    public long getSize() {
        return imgContent.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return imgContent;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(imgContent);
    }

    @Override public void transferTo(File dest) throws IOException, IllegalStateException {
        try(OutputStream os = new FileOutputStream(dest)) { os.write(imgContent); }
    }
}