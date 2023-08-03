package com.example.demo_curso_mongodb.service;

import java.util.concurrent.TimeUnit;


import okhttp3.OkHttpClient;

public abstract class FileClient {
	OkHttpClient http = new OkHttpClient.Builder()
			.connectTimeout(Long.parseLong("50000"), TimeUnit.SECONDS).build();

}
