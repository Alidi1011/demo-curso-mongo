package com.example.demo_curso_mongodb.service;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;

import okhttp3.OkHttpClient;

public abstract class FileClient {

	OkHttpClient http = new OkHttpClient.Builder()
			.hostnameVerifier((hostname, session) -> true)
			.connectTimeout(Long.parseLong("50000"), TimeUnit.SECONDS).build();
	
	OkHttpClient okHttpClientSecure = new OkHttpClient.Builder()
			.connectTimeout(Long.parseLong("50000"), TimeUnit.SECONDS).build();
	
	OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
            .hostnameVerifier((hostname, session) -> true)
            .connectTimeout(Long.parseLong("50000"), TimeUnit.SECONDS);

    OkHttpClient okHttpClientUnsecure = enableUnsecureHttpClient(okHttpClientBuilder).build();
   

    private OkHttpClient.Builder enableUnsecureHttpClient(OkHttpClient.Builder builder) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder;
    }
}
