package com.example.demo_curso_mongodb.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "course")
public class Course {
	@Id 
	private  String id;
	private  String name;
	private Integer credit;
}