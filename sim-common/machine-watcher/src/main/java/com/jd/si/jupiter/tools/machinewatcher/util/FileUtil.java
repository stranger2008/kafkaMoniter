package com.jd.si.jupiter.tools.machinewatcher.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtil {
    private final static Logger log = Logger.getLogger(FileUtil.class);

    /**
     * 读取指定文件中的字符内容
     *
     * @param path
     * @return
     * @throws java.io.IOException
     */
    public static String read(String path) throws IOException {
        StringBuffer sb = new StringBuffer();
        File file = new File(path);
        if (file.isFile()) {
            InputStream is = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            try {
                is = new FileInputStream(file);
                isr = new InputStreamReader(is, Charset.forName("utf-8"));
                br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            } finally {
                if (is != null) {
                    is.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (br != null) {
                    br.close();
                }
            }
        } else {
            throw new IOException("JOB file " + path + " not found.");
        }
        return sb.toString();
    }

    /**
     * 将info数据写入path路径下
     *
     * @param dir
     * @param fileName
     * @param info
     */
    public static void write(String dir, String fileName, String info) throws IOException {
        boolean mkdir = mkdir(dir);
        if (!mkdir) {
            log.error("create dir fail to -> " + dir);
            return;
        }
        File file = new File(dir + "/" + fileName);
        OutputStream os = null;
        OutputStreamWriter osw = null;
        try {
            os = new FileOutputStream(file);
            osw = new OutputStreamWriter(os,  Charset.forName("utf-8"));
            osw.write(info);
            osw.flush();
        } finally {
            if (os != null) {
                os.close();
            }
            if (osw != null) {
                osw.close();
            }
        }
    }

    /**
     * 创建目录
     *
     * @param dir
     * @return
     */
    public static boolean mkdir(String dir) {
        File fileDir = new File(dir);
        if (fileDir.exists()) {
            return true;
        }
        return fileDir.mkdirs();
    }

    /**
     * 取得目录下所有文件名称
     * @param dir
     * @return
     */
    public static List<String> getFiles(String dir){
        List<String> list = new ArrayList<String>();
        File directory=new File(dir);
        if(directory!=null && directory.exists()){
            for(File f : directory.listFiles()){
                list.add(f.getName());
            }
        }
        return list;
    }



    public static void main(String [] args){
        List<String> list = new ArrayList<String>();
        list.add("10141223.txt");
        list.add("10141221.txt");
        list.add("10141222222222.txt");
        list.add("10141222101010.txt");
        list.add("10141222111111.txt");

        Collections.sort(list);

        for(String s: list){
            System.out.println(s);
        }
    }
}
