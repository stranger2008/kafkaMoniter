package com.jd.si.jupiter.tools.machinewatcher.ruannable;

import com.jd.si.jupiter.monitor.machine.thrift.TMonitorBean;
import com.jd.si.jupiter.tools.machinewatcher.model.MachineGlobalInstance;
import com.jd.si.jupiter.tools.machinewatcher.util.FileUtil;
import com.jd.si.jupiter.tools.machinewatcher.util.JsonUtil;
import com.jd.si.jupiter.tools.machinewatcher.util.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangyun6 on 2014/12/19.
 */
public class MachineWatcherFileWriter implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(MachineWatcherFileWriter.class);

    @Override
    public void run() {
        try {
            TMonitorBean monitorBean = MachineGlobalInstance.getMonitorBean();
            writeFile(monitorBean);
        } catch (Exception e) {
            LOG.error("get monitor bean error",e);
        }
    }

    public static void writeFile(TMonitorBean monitorBean) throws Exception {
        String fileName = new SimpleDateFormat("yyyMMddHHmmss").format(new Date());
        String json = JsonUtil.toJson(monitorBean);
        FileUtil.write(SystemConfig.machineWatcherBakDir, fileName, json);
        LOG.info("write file: " + fileName);
        // 删除旧文件
        List<String> files = FileUtil.getFiles(SystemConfig.machineWatcherBakDir);
        if(files != null && files.size() > SystemConfig.machineWatcherBakCount){
            Collections.sort(files);
            int delCount = files.size() - SystemConfig.machineWatcherBakCount;
            for(int i=0;i<delCount;i++){
                new File(SystemConfig.machineWatcherBakDir + "/" + files.get(i)).delete();
            }
        }
    }

}
