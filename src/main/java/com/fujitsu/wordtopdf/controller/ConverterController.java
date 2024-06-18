package com.fujitsu.wordtopdf.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fujitsu.wordtopdf.FileUploadUtil;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;

@Controller
public class ConverterController {
	
	@GetMapping("/wordtopdf")
	public String wordForm() {
		
		return "wordtopdf";
	}
	
	@PostMapping("/wordtopdf")
	public String wordProcess(@RequestParam("upload") MultipartFile file, Model m) {
		String url = "";
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
 
        String uploadDir = "D:\\java\\Spring\\boot\\SpringWordToPdfConverter\\src\\main\\resources\\static\\";
        try {
        	//System.out.println("FileUploadUtil ");
        	FileUploadUtil.saveFile(uploadDir+"uploads\\", fileName, file, m);
        } catch(IOException ie) {
        	System.out.println("Error! file upload "+ie.getMessage());
        	m.addAttribute("error_message", "Please select a valid docx file");
        }
        String[] fileNameArray = fileName.split("\\.");
        try {
    		InputStream docFile = new FileInputStream(new File(uploadDir+"uploads\\"+fileName));
    		XWPFDocument doc = new XWPFDocument(docFile);
    		PdfOptions pdfOptions = PdfOptions.create();
    		OutputStream out = new FileOutputStream(new File(uploadDir+"pdfs\\"+fileNameArray[0]+".pdf"));
    		PdfConverter.getInstance().convert(doc, out, pdfOptions);
    		url = fileNameArray[0]+".pdf";
    		m.addAttribute("downloadurl", url);
    		m.addAttribute("success_message", "Successfully converted "+fileName+" to "+url+". You can now download the converted file below.");
    		doc.close();
    		out.close();
    		System.out.println("Done");
    	}
        
    	catch(Exception e) {
    		e.printStackTrace();
    		m.addAttribute("error_message", "File Conversion failed! please try again.");
    	}
		return "wordtopdf";
	}
	
	@GetMapping("/download/{fileName:.+}")
	public ResponseEntity downloadFileFromLocal(@PathVariable String fileName) {
		String uploadDir = "D:\\java\\Spring\\boot\\SpringWordToPdfConverter\\src\\main\\resources\\static\\pdfs\\";
		Path path = Paths.get(uploadDir + fileName);
		Resource resource = null;
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("application/x-pdf"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
}
