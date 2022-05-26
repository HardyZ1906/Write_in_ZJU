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

public class RabbitmqProducer {
    Channel channel = null;
    static Connection connection = ConnectionUtil.getConnection("10.214.241.124", 5672, "/", "guest", "guest");
    public String filename;

    public RabbitmqProducer() {
    }

    public RabbitmqProducer(String fn) {
        this.filename = fn;
    }

    @PostConstruct
    void init() throws Exception {
        // 从连接中获取一个通道
        channel = connection.createChannel();
        channel.queueDeclare(filename, true, false, true, null);
    }

    @PreDestroy
    void uninit() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }

    public void SendData(String msg) throws IOException {
        channel.basicPublish("", filename, MessageProperties.PERSISTENT_TEXT_PLAIN,
                msg.getBytes("UTF-8"));
    }

}