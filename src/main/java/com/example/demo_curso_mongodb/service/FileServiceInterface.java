package com.example.demo_curso_mongodb.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.List;

import com.example.demo_curso_mongodb.model.Fil;
import com.microsoft.azure.storage.StorageException;

public interface FileServiceInterface {
	public Fil downloadImageWithRestTemplate(Fil file);
	public Fil downloadImageWithHttp3(Fil file) throws IOException;
	public Fil uploadWithAzure(Fil file) throws InvalidKeyException, URISyntaxException, StorageException, IOException;
	public Fil downloadWithAzure(Fil file) throws Exception;
	public Fil donwloadFromTaWithHttp3(Fil file) throws Exception;
	public Fil uploadWithAzureInZip(Fil file) throws InvalidKeyException, URISyntaxException, StorageException, IOException;
	public Fil uploadWithAzureInZip2(List<Fil> file);
	public Fil uploadToJiraWithHttp3(Fil file) throws IOException;
	public Fil downloadToJiraWithHttp3(Fil file) throws Exception;
}
