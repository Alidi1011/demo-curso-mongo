package com.example.demo_curso_mongodb.model;

import lombok.Data;

@Data
public class FileProcess {
	private  String base64;
	private  String name;
	private String size;
	private String url;
	private String folderName;
	private String issueKey;
	private String mimeType;
}
