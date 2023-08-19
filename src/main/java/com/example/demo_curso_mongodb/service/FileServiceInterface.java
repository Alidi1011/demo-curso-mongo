package com.example.demo_curso_mongodb.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.List;

import com.example.demo_curso_mongodb.model.Fil;
import com.microsoft.azure.storage.StorageException;

public interface FileServiceInterface {
	public Fil download(Fil file);
	public Fil uploadWithAzure(Fil file) throws InvalidKeyException, URISyntaxException, StorageException, IOException;
	public Fil downloadWithAzure(Fil file) throws Exception;
	public Fil donwloadWithHttp3(Fil file) throws Exception;
	public Fil uploadWithAzureInZip(Fil file) throws InvalidKeyException, URISyntaxException, StorageException, IOException;
	public Fil uploadWithAzureInZip2(List<Fil> file);
	public Fil uploadToJira(Fil file) throws IOException;
}
