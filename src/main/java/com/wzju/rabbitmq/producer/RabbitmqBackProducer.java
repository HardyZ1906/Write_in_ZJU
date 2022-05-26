package com.wzju.rabbitmq.producer;

import com.wzju.rabbitmq.entity.Produceentity;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import com.wzju.rabbitmq.connection.ConnectionUtil;
import java.util.HashSet;

public class RabbitmqBackProducer {
    Channel channel = null;
    static Connection connection = ConnectionUtil.getConnection("10.214.241.124", 5672, "/", "guest", "guest");
    public final String filename;

    public RabbitmqBackProducer(String fn) {
        this.filename = fn;
    }

    @PostConstruct
    void init() throws Exception {
        // 从连接中获取一个通道
        channel = connection.createChannel();
        channel.exchangeDeclare(filename, "direct", false);
    }

    @PreDestroy
    void uninit() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }

    public void SendData(Produceentity msg, String usr) throws IOException {
        channel.basicPublish(filename, usr, MessageProperties.PERSISTENT_TEXT_PLAIN,
                msg.toString().getBytes("UTF-8"));
    }

}
