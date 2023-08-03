package com.example.demo_curso_mongodb.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class CourseJPA implements Serializable{
	private  String id;
	private  String name;
	private Integer credit;
}
