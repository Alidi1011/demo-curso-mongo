package com.example.demo_curso_mongodb.controller;


import com.example.demo_curso_mongodb.model.Course;
import com.example.demo_curso_mongodb.repository.CourseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(value = "/courses")
public class CourseController {
	
	@Value("${info.project.name}")
	private String valueFromFile;
	
	@Autowired
	CourseRepository repo;

	@GetMapping("/hola")
	public String index(){
		System.out.println("Valor desde el properties: " + valueFromFile);

		return "Hello from Azure Deployment Demo-mongoDb!!!!";
	}

	@GetMapping
	public List<Course> getCourses() {
		return repo.findAll();
	}

	@PostMapping
	public Course saveCourse(@RequestBody Course course)	{
		return repo.save(course);
	}

	@GetMapping("/{id}")
	public Course getCourse(@PathVariable String id){
		Optional<Course> optionalCourse = repo.findById(id);
		return optionalCourse.get();
	}

	@PutMapping
	public Course updateCourse(@RequestBody Course course) {
		return repo.save(course);
	}

	@DeleteMapping("/{id}")
	public void deleteCourse(@PathVariable String id) {
		repo.deleteById(id);
	}
}
