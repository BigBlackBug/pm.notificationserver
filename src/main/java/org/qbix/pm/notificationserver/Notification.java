package org.qbix.pm.notificationserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Notification {

	private final NotificationType notificationType;
	private final String jsonData;
	private final List<Long> accountIDs;

	public Notification(NotificationType notificationType, List<Long> accountIds) {
		this.notificationType = notificationType;
		this.jsonData = "";
		this.accountIDs = accountIds;
	}

	public Notification(NotificationType notificationType,
			List<Long> accountIds, String jsonData) {
		this.notificationType = notificationType;
		this.jsonData = jsonData;
		this.accountIDs = accountIds;
	}

	public Notification(NotificationType notificationType,
			List<Long> accountIds, String key, Object value) {
		this.notificationType = notificationType;
		Map<String, Object> data = new HashMap<>();
		data.put(key, value);
		Gson gson = new Gson();
		this.jsonData = gson.toJson(data, new TypeToken<Map<String, Object>>() {
		}.getType());
		this.accountIDs = accountIds;
	}

	public Notification(NotificationType notificationType,
			List<Long> accountIds, Map<String, Object> data) {
		this.notificationType = notificationType;
		Gson gson = new Gson();
		this.jsonData = gson.toJson(data, new TypeToken<Map<String, Object>>() {
		}.getType());
		this.accountIDs = accountIds;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public String getJsonData() {
		return jsonData;
	}

	public List<Long> getAccountIDs() {
		return accountIDs;
	}
}
