package com.stst.head.services;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.stst.head.models.Employee;
import com.stst.head.models.Notif;
import com.stst.head.repos.NotifRepo;

@Service
@Configurable
public class Notifs {
	
	private static final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
	private static final String TG_TOKEN = "5514859577:AAFEcTZ1hGARCmBoM38oyrZ14WMfqj2vrBA";
	private static final String ADMIN_CHAT_ID = "-865259424";
	private static final String DOMEN = "http://46.0.234.126:25255";
	
	@Autowired
	private NotifRepo notifRepo;
	
	public void sendNotif(Employee employee, String text, String link) {
		Notif notif = new Notif(employee, text, link);
		notifRepo.save(notif);
		sendTg(notif);
	}
	
	public List<Notif> getNotifs(String cId) {
		List<Notif> notifs = notifRepo.findByEmployeeId1( Long.valueOf(cId) ); // all for EmployeeId
		
		return (notifs == null || notifs.size() == 0) ? null : notifs;
	}
	
	public void dellNotif(long id) {
		Optional<Notif> oNotif = notifRepo.findById(id);
		if (oNotif.isEmpty()) {
			return;
		}
		notifRepo.delete(oNotif.get());
	}
	
	
	private void sendTg(Notif notif) {
		// Check if the employee has a telegram
		if ( notif.getEmployee().getTelegramChatId() == null || notif.getEmployee().getTelegramChatId() == 0 ) {return;}
		
		String fullLink = notif.getLink() == null ? "" : DOMEN + notif.getLink();
		String urlString = "https://api.telegram.org/bot" +
							TG_TOKEN +
							"/sendmessage?chat_id=" +
							notif.getEmployee().getTelegramChatId() +
							"&text=" + 
							notif.getText().replaceAll("\\s+","+") +
							"+" + fullLink;
		HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .timeout(Duration.ofSeconds(5))
                .uri(URI.create(urlString))
                .build();

        try {
			httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean sendTg(String text, Employee employee) {
		// Check if the employee has a telegram
		if ( employee.getTelegramChatId() == null || employee.getTelegramChatId() == 0 ) {return false;}
		
		String urlString = "https://api.telegram.org/bot" +
							TG_TOKEN +
							"/sendmessage?chat_id=" +
							employee.getTelegramChatId() +
							"&text=" + 
							text.replaceAll("\\s+","+");
		HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .timeout(Duration.ofSeconds(5))
                .uri(URI.create(urlString))
                .build();

        try {
			httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
        return true;
	}
	
	public static void sendTgToAdminChat(String text) {
		try {
			String urlString = "https://api.telegram.org/bot" +
								TG_TOKEN +
								"/sendmessage?chat_id=" +
								ADMIN_CHAT_ID +
								"&text=" + 
								URLEncoder.encode(text, "UTF-8");
			HttpRequest request = HttpRequest.newBuilder()
	                .GET()
	                .timeout(Duration.ofSeconds(5))
	                .uri(URI.create(urlString))
	                .build();

	        try {
				httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			} catch (IOException | InterruptedException e) {
			}
		} catch (Exception e1) {
		}
	}
	
}
