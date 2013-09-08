package org.qbix.pm.notificationserver;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;

//TODO proper logging and exception handling
public class JMSClient {

	private static final String JMS_USER_PASSWORD = "123123";
	private static final String JMS_USER = "jmsuser";
	private static final String QUEUE_NAME = "NotificationQueue";

	private MessageListener listener;
	private final Map<String, Object> connectionSettings;

	public JMSClient(MessageListener listener) {
		this.listener = listener;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("host", "localhost");
		map.put("port", "5445");
		this.connectionSettings = map;
	}

	public void start() {
		TransportConfiguration config = new TransportConfiguration(
				NettyConnectorFactory.class.getName(), connectionSettings);
		ConnectionFactory factory = (ConnectionFactory) HornetQJMSClient
				.createConnectionFactoryWithoutHA(JMSFactoryType.CF, config);

		Queue queue = HornetQJMSClient.createQueue(QUEUE_NAME);

		Connection connection;
		try {
			connection = factory.createConnection(JMS_USER, JMS_USER_PASSWORD);
			connection.start();
			Session session = connection.createSession(false,
					QueueSession.AUTO_ACKNOWLEDGE);

			MessageConsumer consumer = session.createConsumer(queue);
			System.out.println("ready to receive");
			consumer.setMessageListener(listener);
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}
}