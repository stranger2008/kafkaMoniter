package com.jd.si.jupiter.tools.machinewatcher.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Author: xiajun
 * Date: 2014-08-05
 * Time: 09:26:00
 */
public class NetWorkUtil {
    private static final Logger logger = LoggerFactory.getLogger(NetWorkUtil.class);

    public static final String LOCALHOST = "127.0.0.1";

    public static final String ANYHOST = "0.0.0.0";

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    /**
     * 获取本机ip地址
     *
     * @return
     * @throws SocketException
     */
    public static String getAddress() throws SocketException {
        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
        String address = null;
        while (e.hasMoreElements()) {
           NetworkInterface ni =  e.nextElement();
            if(ni.getName().startsWith("docker") || ni.getName().startsWith("lo")) continue;
            List<InterfaceAddress> lsit = ni.getInterfaceAddresses();
            for (InterfaceAddress interfaceAddress : lsit) {
                if (interfaceAddress.getAddress() instanceof Inet4Address) {
                    String host = interfaceAddress.getAddress().getHostAddress();
                    if (host.startsWith("192.168") || host.startsWith("10") || host.startsWith("172")) {
                        address = host;
                    } else if (host.equals("127.0.0.1")) {
                        continue;
                    } else {
                        return host;
                    }
                }
            }
        }
        return address;
    }

    /**
     * 返回第一个可用ip
     * @return
     */
    public static String getLocalHost() {
        InetAddress address = getLocalAddress();
        return address == null ? null : address.getHostAddress();
    }

    /**
     * 获取本机ip地址
     * @return
     */
    private static InetAddress getLocalAddress() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        if (addresses != null) {
                            while (addresses.hasMoreElements()) {
                                try {
                                    InetAddress address = addresses.nextElement();
                                    if (isValidAddress(address)) {
                                        return address;
                                    }
                                } catch (Throwable e) {
                                    logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
        }
        logger.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }

    /**
     * 验证当前address对象中ip是否合法
     * @param address
     * @return
     */
    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress())
            return false;
        String name = address.getHostAddress();
        return (name != null
                && !ANYHOST.equals(name)
                && !LOCALHOST.equals(name)
                && (name.startsWith("172.")|| name.startsWith("192.")|| name.startsWith("10."))
                && IP_PATTERN.matcher(name).matches());
    }

}
