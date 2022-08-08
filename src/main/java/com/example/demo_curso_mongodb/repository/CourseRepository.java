package com.example.demo_curso_mongodb.repository;

import com.example.demo_curso_mongodb.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface CourseRepository extends MongoRepository<Course, String>{
}
