package utils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;

/**
 * @author Bryan Wang
 * @class_name FileDecryptUtils
 * @description //TODO
 * @date 2019/11/4 11:28
 */
@Component
public class DecryptFileUtils {

    private static String boeEncryptionUrl;

    public String getBoeEncryptionUrl() {
        return boeEncryptionUrl;
    }


    /**
     * @param multipartFile
     * @return java.io.InputStream
     * @description decryptFile//TODO
     * @author Bryan Wang
     * @date 2019/11/4 14:22
     */
    public static File decryptFile2MultiFile(MultipartFile multipartFile) {
        long beginTime = Calendar.getInstance().getTimeInMillis();
        InputStream is = null;
        File destResultFile = null;
        try {
            String result = null;
            is = multipartFile.getInputStream();
            destResultFile = File.createTempFile(UuidUtils.getUUID(), null);
            multipartFile.transferTo(destResultFile);
            Files.copy(is, destResultFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
//            log.error("",e.getMessage(),e);
        }
        long endTime = Calendar.getInstance().getTimeInMillis() - beginTime;
        System.out.println("文件解密耗时===》" + endTime);
        return destResultFile;
    }

    private static MultipartFile getMulFileByPath(String picPath) {
        FileItem fileItem = createFileItem(picPath);
        MultipartFile mfile = new CommonsMultipartFile(fileItem);
        return mfile;
    }

    private static FileItem createFileItem(String filePath) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        String textFieldName = "textField";
        int num = filePath.lastIndexOf(".");
        String extFile = filePath.substring(num);
        FileItem item = factory.createItem(textFieldName, "text/plain", true,
                "MyFileName" + extFile);
        File newfile = new File(filePath);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];

        try (
                FileInputStream fis = new FileInputStream(newfile);
                OutputStream os = item.getOutputStream();
        ) {
            while ((bytesRead = fis.read(buffer, 0, 8192))
                    != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {

//            log.error("",e.getMessage(),e);
        }
        return item;
    }
}
