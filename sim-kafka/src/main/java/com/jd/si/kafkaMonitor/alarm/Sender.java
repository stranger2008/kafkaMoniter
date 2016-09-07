package com.jd.si.kafkaMonitor.alarm;

/**
 * 发送消息端
 * Created by lilianglin on 2016/8/29.
 */
public interface Sender {

    public void send(String title,String content);

}
