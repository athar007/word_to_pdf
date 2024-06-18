package com.fujitsu.wordtopdf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {
    
    public static void saveFile(String uploadDir, String fileName,
            MultipartFile multipartFile, Model m) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
         
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
        	m.addAttribute("error_message", "Please select a valid docx file");
            throw new IOException("Could not save doc file: " + fileName, ioe);
            
        }      
    }
}

