package com.jd.si.kafkaMonitor.alarm;

import com.jd.si.kafkaMonitor.common.SystemConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 报警队列
 * 所有的报警消息先发送到此队列来，另外一个地方一个线程再来消费
 * Created by lilianglin on 2016/8/26.
 */
public class AlarmQueue {

    private static final Log logger = LogFactory.getLog(AlarmQueue.class);
    private static AlarmQueue alarmQueue = null;
    private static final BlockingQueue<AlarmVo> queue = new ArrayBlockingQueue<AlarmVo>(SystemConfig.alarmQueueSize);
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static List<Sender> senderList = new ArrayList<Sender>();

    private AlarmQueue() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                while (true){
                    doConsumer();
                }
            }
        });
        //此处可添加多个消费端，如可再添加短信
        senderList.add(new MailSender());
    }

    public static AlarmQueue getAlarmQueue() {
        if (alarmQueue == null) {
            synchronized (AlarmQueue.class) {
                if (alarmQueue == null) {
                    alarmQueue = new AlarmQueue();
                }
            }
        }
        return alarmQueue;
    }

    /**
     * 放入报警消息
     * @param alarmVo
     */
    public void put(AlarmVo alarmVo){
        try {
            queue.put(alarmVo);
        } catch (InterruptedException e) {
            logger.error("put queue messge error",e);
        }
    }

    private void doConsumer(){
        AlarmVo alarmVo;
        try {
            alarmVo = queue.take();
            //System.out.println("=================================================" + alarmVo);
            if(CollectionUtils.isEmpty(senderList)){
                return;
            }
            for(Sender sender : senderList){
                sender.send(alarmVo.getTitle(),alarmVo.getAlarmStr());
                logger.info(alarmVo.getTitle() + " : " + alarmVo.getAlarmStr());
            }
        } catch (InterruptedException e) {
            logger.error("get queue messge error",e);
        }
    }
}
