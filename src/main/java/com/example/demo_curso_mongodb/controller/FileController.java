package com.example.demo_curso_mongodb.controller;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.InputStream;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo_curso_mongodb.model.File;
import com.example.demo_curso_mongodb.service.FileServiceInterface;

import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.ByteString;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@RestController
@RequestMapping(value = "/file")
public class FileController {
	
	private final String UPLOAD_DIR = "/path/to/upload/directory";
	UUID uid = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d"); 
	
	@Autowired
	FileServiceInterface fileService;

	
	@PostMapping("/upload")
	public String uploadFile(@RequestBody File file) throws Exception {
	   
	    byte[] decodedFile = Base64.getDecoder().decode(file.getBase64().getBytes(StandardCharsets.UTF_8));
	 
	    String path = "src/main/resources/imagenes/nombre.jpg";
	    String path2 = "src/main/resources/imagenes/imagen.jpg";
		String path3 = "C:/Users/aarteagq/Documents/imagenes/" + file.getName();
	
	    try {
	    	InputStream is = new ByteArrayInputStream(decodedFile);
	    	//InputStream is = event.getFile().getInputstream();
	    	OutputStream out = new FileOutputStream(path3);
	    	byte buf[] = new byte[1024];
	    	int len;
	    	while ((len = is.read(buf)) > 0)
	    		out.write(buf, 0, len);
	    		is.close();
	    		out.close();
	    	} catch (Exception e) {
	    		System.out.println(e);
	    	}
	    
		
		/*
		String path3 = "C:/Users/aarteagq/Documents/imagenes/";
		
	    byte[] decodedImg = Base64.getDecoder()
                .decode(file.getBase64().getBytes(StandardCharsets.UTF_8));
	    Path destinationFile = Paths.get(path3, file.getName());
	    Files.write(destinationFile, decodedImg);*/
	    
	    
	    return "true";
	}
	
	@PostMapping("/uploadMultipart")
	public String uploadFile(@RequestPart(value = "file", required = false) MultipartFile file) throws Exception {	  
	 
		String path3 = "C:/Users/aarteagq/Documents/imagenes/" + file.getOriginalFilename();
		
		System.out.println("file name: " + file.getOriginalFilename());
	
	    try {
	        InputStream inputStream = file.getInputStream();
	    	OutputStream out = new FileOutputStream(path3);
	    	byte buf[] = new byte[1024];
	    	int len;
	    	while ((len = inputStream.read(buf)) > 0)
	    		out.write(buf, 0, len);
	    		inputStream.close();
	    		out.close();
	    	} catch (Exception e) {
	    		System.out.println(e);
	    	}
	    
	    return "true";
	}
	
	@PostMapping("/download")
	public void downloadFile(@RequestBody File file) throws Exception {	  
	 
		String bin = "124";
		
		byte[] bytes = bin.getBytes();

		// returns byte[]
		Base64.getEncoder().encode(bytes);

		// returns String
		Base64.getEncoder().encodeToString(bytes);
	}
	
	@PostMapping("/downloadPrueba")
	public File downloadFile1(@RequestBody File file) throws Exception {
		fileService.download(file);
		return file;
	}
	
	@PostMapping("/downloadPrueba2")
	public File downloadFile2(@RequestBody File file) throws Exception {
		
		OkHttpClient http = new OkHttpClient();
		
		HttpUrl.Builder urlBuilder = HttpUrl.parse(file.getUrl()).newBuilder();
		String url = urlBuilder.toString();
		
		log.info("[URL COMMENT] URL: {}", url);
				
		Response response = null;
		Request request = new Request.Builder().url(url).get().build();
		
		response = http.newCall(request).execute();
		
		log.info("[RESPONSE] BODY: {}", response.body().toString());
		
		
		//ObjectMapper objectMapper = new ObjectMapper(); 
		
		//byte[] imageBytes = objectMapper.readValue(responseBody.bytes(), byte[].class);
		
		byte[] imageBytes = response.body().bytes();
		
		//byte[] imageBytes = response.body().source().readByteArray();
		
		String path3 = "C:/Users/aarteagq/Documents/imagenes/";
	    Path destinationFile = Paths.get(path3, file.getName());
		Files.write(destinationFile, imageBytes);
		
		String base64 = Base64.getEncoder().encodeToString(imageBytes);
		//String base64 = new String(Base64.getEncoder().encode(imageBytes), "UTF-8");
		
		file.setBase64(base64);
		return file;
	}
}
