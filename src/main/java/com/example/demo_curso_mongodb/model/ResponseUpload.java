package com.example.demo_curso_mongodb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ResponseUpload {
	private  String id;
	@JsonProperty("filename")
	private  String fileName;
	private String mimeType;
	private  String content;
}
