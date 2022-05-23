package com.wzju.rabbitmq.connection;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ConnectionUtil {
    public static Connection getConnection(String host, int port, String vHost, String userName, String passWord)
            throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setVirtualHost(vHost);
        factory.setUsername(userName);
        factory.setPassword(passWord);
        Connection conn = factory.newConnection();
        return conn;
    }
}
