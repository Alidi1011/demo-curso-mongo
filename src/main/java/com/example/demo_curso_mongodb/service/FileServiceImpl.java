package com.example.demo_curso_mongodb.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo_curso_mongodb.model.File;

@Service
public class FileServiceImpl extends FileClient implements FileServiceInterface {

	@Override
	public File download(File file) {
		RestTemplate restTemplate = new RestTemplate();
		
		String url = "http://img.championat.com/news/big/l/c/ujejn-runi_1439911080563855663.jpg";
		byte[] imageBytes = restTemplate.getForObject(url, byte[].class);
		
		
		String path3 = "C:/Users/aarteagq/Documents/imagenes/";
	    Path destinationFile = Paths.get(path3, file.getName());
	    
		try {
			Files.write(destinationFile, imageBytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String base64 = Base64.getEncoder().encodeToString(imageBytes);
		
		file.setBase64(base64);
		return null;
	}

}
