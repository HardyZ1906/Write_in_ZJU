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
public class RabbitmqConsumer {
        public final String qname;
        Channel channel = null;
        Connection connection = null;
        public List<String> buff;

        public RabbitmqConsumer(String qname) {
                this.qname = qname;
        }

        @PostConstruct
        void init() throws Exception {
                Connection connection = ConnectionUtil.getConnection("10.214.241.124", 5672, "/", "guest", "guest");

                channel = connection.createChannel();
                channel.queueDeclare(qname, true, false, true, null);
                buff = new ArrayList<String>();

        }

        @PreDestroy
        void uninit() throws IOException, TimeoutException {
                connection.close();
                channel.close();
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
                channel.basicConsume(qname, false, deliverCallback, (consumerTag) -> {
                });
        }

}
