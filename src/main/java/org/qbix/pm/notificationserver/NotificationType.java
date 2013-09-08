package org.qbix.pm.notificationserver;

import java.io.Serializable;

public enum NotificationType implements Serializable{

	UPDATE_WITH_GAMEDTO,

	SESSION_PARAMETERS_CHANGED,
	
	SESSION_STARTED,
	
	PLAYER_CONFIRMED_STAKE,
	
	PLAYER_CANCELLED_STAKE,

	PLAYER_DISCONNECTED
}
