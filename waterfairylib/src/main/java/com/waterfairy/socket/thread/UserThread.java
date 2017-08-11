package com.waterfairy.socket.thread;

import com.waterfairy.socket.listener.SocketUserListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by water_fairy on 2017/3/15.
 */

public class UserThread extends Thread {
    private String ip;
    private SocketUserListener listener;
    private String checkMsg;//校验
    private String returnMsg;//返回
    private final int timeout = 2000;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Socket socket;
    private int port;
    private HashMap<String, UserThread> mDeviceHashMap;
    public static String lastUserIpAddress;//上一次连接成功的ip地址

    /**
     * @param ip            连接服务器的ip地址
     * @param port          端口
     * @param checkMsg      发动校验信息
     * @param returnMsg     返回校验信息
     * @param deviceHashMap 已经连接服务器的进程
     * @param listener
     */
    public UserThread(String ip,
                      int port,
                      String checkMsg,
                      String returnMsg,
                      HashMap<String, UserThread> deviceHashMap,
                      SocketUserListener listener) {
        this.port = port;
        this.ip = ip;
        this.checkMsg = checkMsg;
        this.mDeviceHashMap = deviceHashMap;
        this.returnMsg = returnMsg;
        this.listener = listener;
    }

    @Override
    public void run() {
        super.run();
        socket = new Socket();
        try {
            boolean canCheck = true;//是否可以校验,只允许校验一次.
            listener.onConnecting();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            if (socket == null) return;
            mDeviceHashMap.put(ip, this);
            lastUserIpAddress = ip;
            outputStream = socket.getOutputStream();//输出 写入
            inputStream = socket.getInputStream();//输入 读取
            listener.onConnectSuccess();
            if (checkMsg != null) {
                write(checkMsg.getBytes());
            }
            while (true) {
                if (socket != null && socket.isConnected()) {
                    int len = 0;
                    byte[] bytes = new byte[1024 * 1024];
                    try {
                        len = inputStream.read(bytes);
                    } catch (IOException e) {
                        listener.onDisconnect();
                        mDeviceHashMap.remove(ip);
                        break;
                    }

                    if (len != -1) {
                        byte[] readBytes = Arrays.copyOf(bytes, len);
                        if (returnMsg != null && canCheck) {
                            canCheck = false;
                            if (returnMsg.equals(new String(readBytes))) {
                                listener.isRightServer(true);
                            } else {
                                listener.isRightServer(false);
                            }
                        }
                        listener.onRead(readBytes);
                    } else if (len == -1) {
//                        listener.onDisconnect();
//                        mDeviceHashMap.remove(ip);
//                        break;
                    }

                } else {
                    disconnect();
                    listener.onDisconnect();
                    mDeviceHashMap.remove(ip);
                    break;
                }
            }


        } catch (ConnectException e) {
            listener.onConnectError();
            e.printStackTrace();
        } catch (IOException e) {
            listener.onConnectError();
            e.printStackTrace();
        }
    }

    /**
     * 写入信息
     *
     * @param bytes
     */
    public void write(byte[] bytes) {
        try {
            if (outputStream != null) {
                outputStream.write(bytes);
                outputStream.flush();
            }
            if (listener != null) {
                listener.onWrite(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onWriteError();
            }
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (socket == null) return;
        try {
            socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
