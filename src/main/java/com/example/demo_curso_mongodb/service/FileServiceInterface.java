package com.example.demo_curso_mongodb.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.List;

import com.example.demo_curso_mongodb.model.FileProcess;
import com.microsoft.azure.storage.StorageException;

public interface FileServiceInterface {
	public FileProcess downloadImageWithRestTemplate(FileProcess file);
	public FileProcess downloadImageWithHttp3(FileProcess file) throws IOException;
	public FileProcess uploadWithAzure(FileProcess file) throws InvalidKeyException, URISyntaxException, StorageException, IOException;
	public FileProcess downloadWithAzure(FileProcess file) throws Exception;
	public FileProcess donwloadFromTaWithHttp3(FileProcess file) throws Exception;
	public FileProcess donwloadFromTaMetadataWithHttp3(FileProcess file) throws Exception;
	public FileProcess uploadWithAzureInZip(FileProcess file) throws InvalidKeyException, URISyntaxException, StorageException, IOException;
	public FileProcess uploadWithAzureInZip2(List<FileProcess> file);
	public FileProcess uploadToJiraWithHttp3(FileProcess file) throws IOException;
	public FileProcess downloadToJiraWithHttp3(FileProcess file) throws Exception;
}
