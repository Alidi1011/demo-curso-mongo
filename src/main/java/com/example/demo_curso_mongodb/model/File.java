package com.example.demo_curso_mongodb.model;

import lombok.Data;

@Data
public class File {
	private  String base64;
	private  String name;
	private String size;
	private String url;
}
