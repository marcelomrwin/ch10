package com.packtpub.mjbeap7.ch10;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;

public class JMSConsumer {

    private static final Integer NUM_OF_MESSAGES = 10;

    private static final String CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DESTINATION = "jms/queue/MJEAP7";
    private static final String DEFAULT_USERNAME = "jmsuser";
    private static final String DEFAULT_PASSWORD = "jmsuser.2016";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "http-remoting://192.168.59.104:8180";

    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
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
            consumer = session.createConsumer(destination);
            connection.start();

            do {
                try {message = (TextMessage)consumer.receiveNoWait();} catch (javax.jms.IllegalStateException ise) {ise.printStackTrace(); Thread.sleep(3000);message = (TextMessage)consumer.receiveNoWait();}
                if (message != null) {
                    System.out.println("["+System.currentTimeMillis() + "] - Receiving messages with content: " + message.getText());
                    Thread.sleep(1000);
                }
            } while (message != null);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (context != null) {
                context.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

    }
}
