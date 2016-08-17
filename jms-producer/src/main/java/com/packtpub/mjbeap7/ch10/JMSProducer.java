package com.packtpub.mjbeap7.ch10;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;

public class JMSProducer {

    private static final Integer NUM_OF_MESSAGES = 100;
    private static final String CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DESTINATION = "jms/queue/MJEAP7";
    private static final String DEFAULT_USERNAME = "jmsuser";
    private static final String DEFAULT_PASSWORD = "jmsuser.2016";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "http-remoting://192.168.59.104:8180";

    public static void main(String[] args) throws Exception {
        final StringBuilder msg = new StringBuilder();
        if (args != null && args.length > 0) {
            for (String arg : args) {
                msg.append(arg);
                msg.append(" ");
            }
        } else {
            msg.append("A message for JBoss EAP 7 running an embedded Apache ActiveMQ Artemis broker");
        }

        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer consumer = null;
        Destination destination = null;
        TextMessage message = null;
        Context context = null;

        try {
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, PROVIDER_URL));
            env.put(Context.SECURITY_PRINCIPAL, System.getProperty("username", DEFAULT_USERNAME));
            env.put(Context.SECURITY_CREDENTIALS, System.getProperty("password", DEFAULT_PASSWORD));
            context = new InitialContext(env);

            String connectionFactoryString = System.getProperty("connection.factory", CONNECTION_FACTORY);
            connectionFactory = (ConnectionFactory) context.lookup(connectionFactoryString);

            String destinationString = System.getProperty("destination", DESTINATION);
            destination = (Destination) context.lookup(destinationString);

            connection = connectionFactory.createConnection(System.getProperty("username", DEFAULT_USERNAME), System.getProperty("password", DEFAULT_PASSWORD));
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            connection.start();

            System.out.println("Sending messages with content: " + msg.toString());

            for (int i = 0; i < NUM_OF_MESSAGES; i++) {
                message = session.createTextMessage(i+msg.toString());
                producer.send(message);
                System.out.println("\t\tSending messages with content ["+i+"]: " + msg.toString());
                //Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (context != null) {
                context.close();
            }

            // closing the connection takes care of the session, producer, and consumer
            if (connection != null) {
                connection.close();
            }
        }

    }
}