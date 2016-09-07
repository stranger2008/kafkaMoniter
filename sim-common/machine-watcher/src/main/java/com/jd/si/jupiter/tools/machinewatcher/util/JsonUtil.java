package com.jd.si.jupiter.tools.machinewatcher.util;

import com.google.gson.Gson;

/**
 * Created by yangxianda on 2016/8/24.
 */
public class JsonUtil {

    private static Gson gson = new Gson();
    public static String toJson(Object obj){
        return gson.toJson(obj);
    }
}
