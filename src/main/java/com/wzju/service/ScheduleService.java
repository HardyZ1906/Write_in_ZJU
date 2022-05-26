package com.wzju.service;

import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.TSFBuilder;
import com.wzju.rabbitmq.consumer.*;
import com.wzju.rabbitmq.producer.*;

@Service
public class ScheduleService {
    public static HashMap<String, RabbitmqConsumer> RCM = new HashMap<>();
    public static HashSet<String> files = new HashSet<>();

    public static HashMap<String, RabbitmqBackProducer> RBPM = new HashMap<>();
    public static HashMap<String, HashMap<String, RabbitmqBackConsumer>> RBCM = new HashMap<>();
    public static HashMap<String, List<String>> usrFiles = new HashMap<>();

    @Scheduled(cron = "0/2 * * * * ?")
    public void ConsumeAll() {
        for (String file : files) {

            ConsumeThread cThread = new ConsumeThread(RCM.get(file));
            cThread.start();

        }
    }

    public void addFile(String filename) {
        if (!files.contains(filename)) {
            files.add(filename);
        }
        if (!RCM.containsKey(filename)) {
            RCM.put(filename, new RabbitmqConsumer(filename));
        }
    }

    public void removeFile(String filename) {
        if (files.contains(filename)) {
            files.remove(filename);
            RCM.remove(filename);

        }
    }

    // 完成将对应用户的消息取出，发回前端
    public Map<String, String> usrConsume(String usr) throws IOException {
        if (usrFiles.containsKey(usr)) {
            HashMap<String, String> infos = new HashMap<>();
            for (String file : usrFiles.get(usr)) {
                RabbitmqBackConsumer tcsmer;
                StringBuilder info = new StringBuilder();
                if (RBCM.get(file) != null) {
                    tcsmer = RBCM.get(file).get(usr);
                    if (tcsmer != null) {
                        tcsmer.ConsumeData();
                        for (String str : tcsmer.buff) {
                            info.append(str + " ");
                        }
                        tcsmer.buff = new ArrayList<>();
                    } else {
                        tcsmer = new RabbitmqBackConsumer(file, usr);
                        tcsmer.ConsumeData();
                        for (String str : tcsmer.buff) {
                            info.append(str + " ");
                        }
                        tcsmer.buff = new ArrayList<>();
                    }
                    infos.put(file, new String(info));
                }
            }
            return infos;
        } else
            return new HashMap<>();
    }

    public JSONObject toJson(Map<String, String> otOutput) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String> oentry : otOutput.entrySet()) {
            jsonObject.put(oentry.getKey(), oentry.getValue());
        }
        return jsonObject;
    }

    class ConsumeThread extends Thread {
        public RabbitmqConsumer inqueue;

        ConsumeThread(RabbitmqConsumer inqueue) {
            this.inqueue = inqueue;
        }

        @Override
        public void run() {
            try {
                this.SingleQueueConsume();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void SingleQueueConsume() throws IOException {
            inqueue.ConsumeData();
            // doOT(inqueue.buff,ScheduleService.RBPM,ScheduleService.RBCM);

            inqueue.buff = new ArrayList<String>();
            System.out.println(inqueue.qname + "has been consumed");
        }
    }
}
