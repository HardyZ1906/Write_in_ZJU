package com.wzju.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.stereotype.Component;
import com.wzju.rabbitmq.connection.ConnectionUtil;
import com.rabbitmq.client.*;
import java.io.IOException;

import java.util.*;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class RabbitmqBackConsumer {
    Channel channel = null;
    Connection connection = null;
    public final String filename;
    public final String usrname;
    public String queueName;
    public List<String> buff;

    public RabbitmqBackConsumer(String fn, String un) {
        this.filename = fn;
        this.usrname = un;
    }

    @PostConstruct
    void init() throws Exception {
        // 获取连接
        Connection connection = ConnectionUtil.getConnection("10.214.241.124", 5672, "/", "guest", "guest");
        // 从连接中获取一个通道
        channel = connection.createChannel();
        this.queueName = channel.queueDeclare().getQueue();
        channel.exchangeDeclare(filename, "direct", false);
        channel.queueBind(queueName, filename, usrname);
    }

    @PreDestroy
    void uninit() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }

    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");

        System.out.println(" [x] Received ");
        try {
            this.buff.add(message);
        } finally {
            System.out.println(" [x] Done");
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
    };
    CancelCallback cancel = new CancelCallback() {
        @Override
        public void handle(String consumerTag) throws IOException {
            System.out.println("Something wrong");
        }
    };

    public void ConsumeData() throws IOException {
        channel.basicConsume(queueName, false, deliverCallback, (consumerTag) -> {
        });
    }

}
