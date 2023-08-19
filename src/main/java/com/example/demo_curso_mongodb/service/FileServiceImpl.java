package com.example.demo_curso_mongodb.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.security.InvalidKeyException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.example.demo_curso_mongodb.model.ResponseUpload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo_curso_mongodb.model.Fil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;

@Slf4j
@Service
public class FileServiceImpl extends FileClient implements FileServiceInterface {

	@Value("${azure.storage.connection-string}")
	private String storageConnection;

	@Value("${azure.storage.container-name}")
	private String containerName;

	@Value("${azure.storage.path-name}")
	private String pathName;

	@Value("${ta.client-id}")
	private String clientId;

	@Value("${ta.client-secret}")
	private String clientSecret;

	@Value("${ta.url-token}")
	private String urlToken;

	@Value("${jira.host}")
	private String jiraHost;

	@Value("${jira.path}")
	private String jiraPath;

	@Value("${jira.issue.key}")
	private String jiraIssueKey;
	
	@Value("${jira.basicAuth.clientId}")
	private String jiraClientId;
	
	@Value("${jira.basicAuth.clientSecret}")
	private String jiraClientSecret;
	
	@Value("${jira.enable.unsecure.httpclient}")
	private String isUnsecureEnable;

	private static final ObjectMapper mapper = new ObjectMapper();

	private static final String PATH_LOCAL_ZIP = "src/main/resources/images/";
	private static final String ATTACHMENT_NAME = "attachments-";

	@Override
	public Fil download(Fil file) {
		RestTemplate restTemplate = new RestTemplate();

		String url = "http://img.championat.com/news/big/l/c/ujejn-runi_1439911080563855663.jpg";
		byte[] imageBytes = restTemplate.getForObject(url, byte[].class);

		String path3 = "C:/Users/aarteagq/Documents/imagenes/";
		Path destinationFile = Paths.get(path3, file.getName());

		try {
			Files.write(destinationFile, imageBytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String base64 = Base64.getEncoder().encodeToString(imageBytes);

		file.setBase64(base64);
		return null;
	}

	@Override
	public Fil uploadWithAzure(Fil file) throws InvalidKeyException, URISyntaxException, StorageException, IOException {

		CloudStorageAccount storageAccount = CloudStorageAccount.parse(this.storageConnection);

		CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
		CloudBlobContainer blobContainer = blobClient.getContainerReference(containerName);

		String imageName = file.getName();

		String pathImage = pathName.concat(file.getFolderName()).concat("/").concat(imageName);

		CloudBlockBlob blockBlob = blobContainer.getBlockBlobReference(pathImage);

		log.info("pathImage: " + pathImage);

		byte[] decodedFile = Base64.getDecoder().decode(file.getBase64().getBytes(StandardCharsets.UTF_8));
		InputStream inputStream = new ByteArrayInputStream(decodedFile);

		int length = inputStream.available();
		blockBlob.upload(inputStream, length);

		inputStream.close();

		log.info("blockBlob.name: " + blockBlob.getName());
		log.info("blockBlob.length: " + length);
		log.info("blockBlob.properties.length: " + blockBlob.getProperties().getLength());

		Fil fileResponse = new Fil();
		fileResponse.setUrl(blockBlob.getUri().toString());
		fileResponse.setName(imageName);

		return fileResponse;
	}

	@Override
	public Fil downloadWithAzure(Fil file) throws Exception {

		CloudStorageAccount storageAccount = CloudStorageAccount.parse(this.storageConnection);
		CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
		CloudBlobContainer blobContainer = blobClient.getContainerReference(this.containerName);
		CloudBlockBlob blockBlob1 = new CloudBlockBlob(URI.create(file.getUrl()));
		CloudBlockBlob blockBlob = blobContainer.getBlockBlobReference(blockBlob1.getName());

		Fil fileResponse = null;

		if (blockBlob.exists()) {
			// save in local path
			String path3 = "C:/Users/aarteagq/Documents/imagenes/" + file.getName();
			blockBlob.downloadToFile(path3);

			InputStream inputStream = blockBlob.openInputStream();
			byte[] decodedFile = inputStream.readAllBytes();
			String base64 = Base64.getEncoder().encodeToString(decodedFile);
			inputStream.close();

			log.info("base64: " + base64);
			log.info("blob container name: " + blobContainer.getName());
			log.info("prueba blockBlob1 name: " + blockBlob1.getName());
			log.info("prueba blockBlob name: " + blockBlob.getName());
			log.info("length: " + blockBlob.getProperties().getLength());
			log.info("URI: " + blockBlob.getUri());
			log.info("getUrl enviado: " + file.getUrl());

			fileResponse = new Fil();
			fileResponse.setBase64(base64);
			fileResponse.setName(file.getName());
		} else {
			log.info("El archivo blockBlob no existe el siguiente recurso:  " + file.getUrl());
			throw new Exception("El archivo blockBlob no existe");
		}

		return fileResponse;
	}

	@Override
	public Fil donwloadWithHttp3(Fil file) throws IOException {

		OkHttpClient http = new OkHttpClient.Builder()
				.hostnameVerifier((hostname, session) -> true)
				.connectTimeout(Long.parseLong("50000"), TimeUnit.SECONDS).build();
		
		log.info("HOSTNAME VERIFIER: " + false);

		HttpUrl.Builder urlBuilder = HttpUrl.parse(file.getUrl()).newBuilder();
		String url = urlBuilder.toString();

		log.info("[URL COMMENT] URL: {}", url);

		Response response = null;

		String oauthAuthorization = this.getOauthAuthorization();
		String basicAuthorization = this.getBasicAuthorization();

		Request request = new Request.Builder().url(url).get().header("Authorization", basicAuthorization)
				.header("Accept", "application/octet-stream").build();

		log.info("[REQUEST] REQUEST: {}", request);

		response = http.newCall(request).execute();

		log.info("[RESPONSE] BODY: {}", response.body().toString());

		byte[] imageBytes = response.body().bytes();

		// Save image in local
		String path3 = "C:/Users/aarteagq/Documents/imagenes/";
		Path destinationFile = Paths.get(path3, file.getName());
		Files.write(destinationFile, imageBytes);
		// Save image in local

		String base64 = Base64.getEncoder().encodeToString(imageBytes);
		file.setBase64(base64);

		return file;
	}

	private String getOauthAuthorization() {

		OkHttpClient http = new OkHttpClient();

		RequestBody formBody = new FormBody.Builder().add("grant_type", "client_credentials")
				// .add("client_id", clientId)
				// .add("client_secret", clientSecret)
				.build();

		String auth = clientId + ":" + clientSecret;
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
		String authHeader = "Basic " + new String(encodedAuth);
		log.info("AuthHeader: " + authHeader);

		Request requestToken = new Request.Builder().url(urlToken).method("POST", formBody)
				.addHeader("Authorization", authHeader).addHeader("Content-Type", "application/x-www-form-urlencoded")
				.build();

		log.info("[Request] : " + requestToken);
		log.info("[Request] : " + requestToken.body().toString());

		Response responseToken;
		String tokenValue = null;
		try {
			responseToken = http.newCall(requestToken).execute();
			String jsonResponse = responseToken.body().string();

			log.info("[Response] : " + jsonResponse.toString());

			tokenValue = mapper.readTree(jsonResponse.toString()).get("access_token").asText();

			log.info("[response accessToken] access_token: {}", tokenValue);

		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		return "Bearer " + tokenValue;
	}

	private String getBasicAuthorization() {
		String auth = clientId + ":" + clientSecret;
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
		String authorizationHeader = "Basic " + new String(encodedAuth);
		return authorizationHeader;
	}

	@Override
	public Fil uploadWithAzureInZip(Fil file)
			throws InvalidKeyException, URISyntaxException, StorageException, IOException {
		UUID uuid = UUID.randomUUID();

		CloudStorageAccount storageAccount = CloudStorageAccount.parse(this.storageConnection);

		CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
		CloudBlobContainer blobContainer = blobClient.getContainerReference(this.containerName);

		String imageName = file.getName();
		String pathZip = this.pathName.concat(file.getFolderName()).concat("/").concat(uuid.toString() + ".zip");

		CloudBlockBlob blockBlob = blobContainer.getBlockBlobReference(pathZip);

		log.info("pathZip: " + pathZip);

		byte[] decodedFile = Base64.getDecoder().decode(file.getBase64().getBytes(StandardCharsets.UTF_8));
		InputStream inputStream = new ByteArrayInputStream(decodedFile);

		// ZIP PRUEBAS
		// Dirección y nombre del zip a crear
		String dest = "C:/Users/aarteagq/Documents/imagenes/prueba123.zip";
		File zipFile = new File(dest);
		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream zipOut = new ZipOutputStream(fos);

		/*
		 * FileInputStream fis = new FileInputStream(zipFile); ZipInputStream zipIn =
		 * new ZipInputStream(fis);
		 * 
		 * zipIn.getNextEntry()
		 */

		// Buscamos el archivo fisico
		ZipEntry zipEntry = new ZipEntry(file.getName()); // considerar la extension del archivo que se va a zipear
		// Agregamos la entrada del zip con el archivo al archivo de salida.
		zipOut.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = inputStream.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}

		zipOut.closeEntry();
		// inputStream.close();

		// Cerramos los recursos.
		zipOut.close();

		// ZIP PRUEBAS
		blockBlob.uploadFromFile(dest);

		fos.close();
		zipFile.delete();

		log.info("blockBlob.name: " + blockBlob.getName());
		log.info("blockBlob.properties.length: " + blockBlob.getProperties().getLength());

		Fil fileResponse = new Fil();
		fileResponse.setUrl(blockBlob.getUri().toString());
		fileResponse.setName(imageName);

		return fileResponse;
	}

	@Override
	public Fil uploadWithAzureInZip2(List<Fil> files) {

		Fil fileResponse = new Fil();
		String folderName = "PANGEA-7777";
		String attachmentZipName = ATTACHMENT_NAME.concat(folderName.toLowerCase()).concat(".zip");

		try {

			CloudStorageAccount storageAccount = CloudStorageAccount.parse(this.storageConnection);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer blobContainer = blobClient.getContainerReference(containerName);
			String pathZip = this.pathName.concat(folderName).concat("/").concat(attachmentZipName);
			CloudBlockBlob blockBlob = blobContainer.getBlockBlobReference(pathZip);

			log.info("PATHZIP: " + pathZip);

			String destinationPath = PATH_LOCAL_ZIP.concat(attachmentZipName);

			File zipFolder = new File(PATH_LOCAL_ZIP);
			if (!zipFolder.exists()) {
				zipFolder.mkdirs();
			}

			// zipFolder.delete();

			File zipFile = new File(destinationPath);

			log.info("zip file absolute path: " + zipFile.getCanonicalPath());
			log.info("zip file absolute path: " + zipFile.getAbsolutePath());

			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zipOut = new ZipOutputStream(fos);

			Integer index = 0;
			for (Fil file : files) {
				index++;
				String imageName = index.toString().concat("-").concat(file.getName());
				String base64 = file.getBase64();

				byte[] decodedFile = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
				InputStream inputStream = new ByteArrayInputStream(decodedFile);

				ZipEntry zipEntry = new ZipEntry(imageName);
				zipOut.putNextEntry(zipEntry);

				byte[] bytes = new byte[1024];
				int length;
				while ((length = inputStream.read(bytes)) >= 0) {
					zipOut.write(bytes, 0, length);
				}

				zipOut.closeEntry();
				inputStream.close();
			}

			zipOut.close();
			fos.close();
			blockBlob.uploadFromFile(destinationPath);

			if (zipFile.delete()) {
				log.info("File se borro correctamente");
			} else {
				log.info("File no se borro correctamente");
			}

			fileResponse.setUrl(blockBlob.getUri().toString());
			fileResponse.setName(attachmentZipName);
			log.info("attachment uri: " + blockBlob.getUri());

		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}

		return fileResponse;
	}

	@Override
	public Fil uploadToJira(Fil file) throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		String temporaryFolder = "src/main/resources/attachments-jira";
		String uriFilePath = temporaryFolder.concat("/").concat(file.getName());

		try {

			File fileSave = new File(temporaryFolder);
			if (!fileSave.exists()) {
				fileSave.mkdir();
			}

			File fileToSave = new File(uriFilePath);

			try (FileOutputStream fos = new FileOutputStream(uriFilePath)) {
				byte[] data = Base64.getDecoder().decode(file.getBase64());
				fos.write(data);
			}

			log.info("[JIRA UPLOAD ATTACHMENT] uriFileLocalPath: {}", uriFilePath);

			
			Path path = fileToSave.toPath();
		    String mimeType = Files.probeContentType(path);
		    
		    log.info("[mimeType] : {}", mimeType);
		   
			// upload to JIRA:
			MediaType mediaType = MediaType.parse(mimeType);
			
			RequestBody requestBody = RequestBody.create(fileToSave, mediaType);

			RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
					.addFormDataPart("file", file.getName(), requestBody)
					.build();

			HttpUrl.Builder urlBuilder = HttpUrl
					.parse(jiraHost.concat("/").concat(jiraPath).replace(jiraIssueKey, file.getIssueKey()))
					.newBuilder();

			String url = String.valueOf(urlBuilder);	

			log.info("[JIRA UPLOAD ATTACHMENT] REQUEST URL: {}", url);

			Request request = new Request.Builder()
					.url(url).post(formBody)
					.addHeader("X-Atlassian-Token", "no-check")
					.addHeader("Accept", "application/json")
			        .addHeader("X-Atlassian-Token", "no-check")
					.addHeader("Authorization", this.getJiraBasicAuthorization()).build();

			log.info("[JIRA UPLOAD ATTACHMENT] REQUEST: {}", request);

			Response response = null;
			if(Boolean.valueOf(isUnsecureEnable)) {
				response = okHttpClientUnsecure.newCall(request).execute();
			}else {
				response = okHttpClientSecure.newCall(request).execute();
			}

			log.info("[JIRA UPLOAD ATTACHMENT] RESPONSE: {} ", response);

			String responseBody = response.body().string();
			
			List<ResponseUpload> list = mapper.readValue(responseBody, mapper.getTypeFactory().constructCollectionType(List.class, ResponseUpload.class));

			log.info("response: " + list.get(0).getContent());
			log.info("response: " + list.get(0).getFileName());
			log.info("response: " + list.get(0).getId());
			log.info("response: " + list.get(0).getMimeType());

			log.info("[JIRA UPLOAD ATTACHMENT] RESPONSE Ok: {} ", responseBody);

		} catch (IOException e) {
			log.info("[JIRA UPLOAD ATTACHMENT] error: {} ", e);

		} finally {
			Files.delete(Paths.get(uriFilePath));
			log.info("[JIRA UPLOAD ATTACHMENT] FILE DELETED OK: {} ", uriFilePath);
		}

		return null;
	}
	
	private String getJiraBasicAuthorization() {
		String auth = jiraClientId + ":" + jiraClientSecret;
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
		String authorizationHeader = "Basic " + new String(encodedAuth);
		return authorizationHeader;
	}
}
