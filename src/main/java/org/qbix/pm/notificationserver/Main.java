package org.qbix.pm.notificationserver;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException,
			InterruptedException {
		ClientNotifier c = new ClientNotifier();
		c.start();
	}
}
