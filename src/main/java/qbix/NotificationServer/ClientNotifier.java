package qbix.NotificationServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.google.gson.Gson;

public class ClientNotifier implements MessageListener {
	private final static int SOCKET_PORT = 4444;

	private final Map<Long, Socket> sockets = new ConcurrentHashMap<Long, Socket>();
	private final ServerSocket serverSocket;
	private final Gson gson;
	private final JMSClient jmsClient;

	public ClientNotifier() throws IOException {
		serverSocket = new ServerSocket(SOCKET_PORT);
		gson = new Gson();
		jmsClient = new JMSClient(this);
	}
	
	public void start(){
		jmsClient.start();
		new Thread(new SocketAcceptor()).start();
	}

	private class SocketAcceptor implements Runnable {
		public void run() {
			System.out.println("started the acceptor thread");
			while (true) {
				Socket newSocket;
				try {
					newSocket = serverSocket.accept();
					System.out.println("accepted a guy");
					BufferedReader in = new BufferedReader(
							new InputStreamReader(newSocket.getInputStream()));
					System.out.println("opened a stream");
					String accountIdString = in.readLine();
					System.out.println("read " + accountIdString);
					long accountId = Long.parseLong(accountIdString);
					sockets.put(accountId, newSocket);
					System.out.println("starting a reader thread");
					new Thread(new SocketReader(accountId, newSocket)).start();
				} catch (Exception e) {
					System.out.println("caught ex");
					e.printStackTrace();
					// unable to connect
				}
			}
		}
	}

	private class SocketReader implements Runnable {
		private final Long accountId;
		private final Socket socket;

		private SocketReader(Long accountId, Socket socket) {
			this.accountId = accountId;
			this.socket = socket;
		}

		public void run() {
			BufferedReader in;
			try {
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				int read = in.read();
				if (read == -1) {
					throw new IOException();
				}
			} catch (IOException e) {
				System.out.println("client disconnected");
				// client disconnected. do something
				try {
					socket.close();
				} catch (IOException e1) {
					System.out.println("unable to close socket");
					// unable to close socket
				} finally {
					sockets.remove(accountId);
					System.out.println("removed socket" + accountId
							+ " from map. current clients - " + sockets.size());
				}
			}
		}

	}

	@Override
	public void onMessage(Message message) {
		
		TextMessage tm = (TextMessage) message;
		String notificationJson;
		try {
			notificationJson = tm.getText();
		} catch (JMSException e) {
			System.out.println("unable to parse message");
			return;
		}
		System.out.println("received a message "+notificationJson);
		Notification n = gson.fromJson(notificationJson, Notification.class);
		List<Long> userIds = n.getAccountIDs();
		for (Long userId : userIds) {
			Socket socket = sockets.get(userId);
			if (socket == null) {
				System.out.println(userId + " client has already disconnected");
				continue;
			}
			try {
				OutputStream outputStream = socket.getOutputStream();
				System.out.println("writing to " + userId);
				outputStream.write(notificationJson.getBytes("UTF-8"));
			} catch (IOException ex) {
				System.out.println("error sending update to " + userId);
				// error sending update
				// do smoething about it
			}
		}
	}
}