package com.stst.head.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Service;

@Service
public class YandexDisk {
	
	private final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
	private static final String TOKEN = "OAuth y0_AgAAAABmxU8BAADLWwAAAADV9HE14KF-mpu4S5C36o0eZG-rzhXH3xM";
	private static final String SPACER = "/Объекты/";
	
	public void newFolder(String name) throws IOException, InterruptedException, YandexDiskException {
		// https://cloud-api.yandex.net/v1/disk/resources?path=%2F%D0%BE%D1%80
		String urlString = "https://cloud-api.yandex.net/v1/disk/resources?path=" + SPACER + name.replaceAll("\\s+","+");
		HttpRequest request = HttpRequest.newBuilder()
						.PUT(HttpRequest.BodyPublishers.noBody())
						.header("Accept", "application/json")
						.header("Authorization", TOKEN)
						.uri(URI.create(urlString))
						.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() >= 300) {
			throw new YandexDiskException(response.body());
		}	
	}
		
	public void publishFolder(String name) throws IOException, InterruptedException, YandexDiskException {
		// https://cloud-api.yandex.net/v1/disk/resources/publish
		String urlString = "https://cloud-api.yandex.net/v1/disk/resources/publish?path=" + SPACER + name.replaceAll("\\s+","+");
		HttpRequest request = HttpRequest.newBuilder()
						.PUT(HttpRequest.BodyPublishers.noBody())
						.header("Accept", "application/json")
						.header("Authorization", TOKEN)
						.uri(URI.create(urlString))
						.build();
	
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() >= 300) {
			throw new YandexDiskException(response.body());
		}
	}
	
	public void editFolderName(String pastName, String newName) throws IOException, InterruptedException, YandexDiskException {
		// https://cloud-api.yandex.net/v1/disk/resources/move?from=%2F%D0%BE%D1%801&path=%2F%D0%BE%D1%802
		String urlString = "https://cloud-api.yandex.net/v1/disk/resources/move?from=" + SPACER + pastName.replaceAll("\\s+","+") + "&path=" + SPACER + newName.replaceAll("\\s+","+");
		HttpRequest request = HttpRequest.newBuilder()
						.POST(HttpRequest.BodyPublishers.noBody())
						.header("Content-Type", "application/json")
						.header("Accept", "application/json")
						.header("Authorization", TOKEN)
						.uri(URI.create(urlString))
						.build();
	
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() >= 300) {
			throw new YandexDiskException(response.body());
		}
	}
	
	public String getFolderLink(String name) throws IOException, InterruptedException, YandexDiskException {
		// https://cloud-api.yandex.net/v1/disk/resources?path=%2F%D0%9E%D0%B1%D1%8A%D0%B5%D0%BA%D1%82%D1%8B%2
		String urlString = "https://cloud-api.yandex.net/v1/disk/resources?path=" + SPACER + name.replaceAll("\\s+","+");
		HttpRequest request = HttpRequest.newBuilder()
						.GET()
						.header("Accept", "application/json")
						.header("Authorization", TOKEN)
						.uri(URI.create(urlString))
						.build();
	
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() >= 300) {
			throw new YandexDiskException(response.body());
		}
		return Etc.valueFormHttpResponseBody(response.body(), "public_url");
	}
	
	
	
	
	
	
	public static class YandexDiskException extends Exception {
		private static final long serialVersionUID = 757578L;

		public YandexDiskException(String message){
	        super(message);
	    }
	}
	
}
