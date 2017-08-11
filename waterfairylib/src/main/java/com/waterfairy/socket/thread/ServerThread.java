package com.waterfairy.socket.thread;

import android.support.annotation.NonNull;
import android.util.Log;

import com.waterfairy.socket.listener.SocketServerListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


/**
 * Created by water_fairy on 2017/3/15.
 */

public class ServerThread extends Thread {
    private InputStream inputStream;
    private OutputStream outputStream;
    private SocketServerListener listener;
    private HashMap<String, OutputStream> outputStreamHashMap;
    private String receiveMsg;//接收到客户端的校验msg
    private String returnMsg;//返回给客户端的msg
    private ServerSocket serverSocket;
    private String interruptAddress;
    private static final String TAG = "ServerThread";
    private int port;

    private boolean isServerOpen;
    private boolean isOneUser;
    private HashMap<String, Socket> mServerSocketHashMap;
    private HashMap<String, Boolean> mUserConnect;
    private String connectUserIpAddress;


    public ServerThread(int port, boolean isOneUser, String receiveMsg, String returnMsg, @NonNull SocketServerListener listener) {
        this.port = port;
        this.isOneUser = isOneUser;
        this.receiveMsg = receiveMsg;
        this.returnMsg = returnMsg;
        this.listener = listener;
    }

    @Override
    public void run() {
        super.run();
        try {
            if (mServerSocketHashMap == null) {
                mServerSocketHashMap = new HashMap<>();
            }
            listener.onStarting();
            serverSocket = new ServerSocket(port);
            isServerOpen = true;
            listener.onStartServerSuccess();
            accept(serverSocket);
        } catch (IOException e) {
            e.printStackTrace();
            isServerOpen = false;
            listener.onStartServerError();
        }
    }

    private void accept(final ServerSocket serverSocket) {
        Log.i(TAG, "accept: 开启新服务 isOneUser:" + isOneUser);
        Socket socket = null;
        boolean canReturn = true;
        if (serverSocket == null) return;
        try {
            socket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (!isOneUser) {
            accept(serverSocket);
        }
        InetAddress inetAddress = socket.getInetAddress();
        String ipAddress = inetAddress.getHostAddress().toString();
        if (mUserConnect == null) {
            mUserConnect = new HashMap<>();
        }
        if (isOneUser) {
            connectUserIpAddress = ipAddress;
        }
        mServerSocketHashMap.put(ipAddress, socket);
        mUserConnect.put(ipAddress, true);
        listener.onConnectSuccess(ipAddress);
        Log.i(TAG, "run: " + ipAddress);
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            if (outputStreamHashMap == null) {
                outputStreamHashMap = new HashMap<>();
            }
            outputStreamHashMap.put(ipAddress, outputStream);
            while (true) {
                if (socket.isConnected()) {
                    int len = 0;
                    byte[] bytes = new byte[1024 * 1024];
                    try {
                        len = inputStream.read(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                        interruptAddress = ipAddress;
                        disconnectIn();
                    }

                    if (len != -1 && len != 0) {
                        byte[] readBytes = Arrays.copyOf(bytes, len);
                        if (receiveMsg != null && canReturn) {
                            canReturn = false;
                            if (receiveMsg.equals(new String(readBytes))) {
                                write(ipAddress, returnMsg.getBytes());
                            }
                        }
                        listener.onRead(readBytes);
                    } else {
                        interruptAddress = ipAddress;
                        disconnectIn();
                        if (isOneUser) {
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    accept(serverSocket);
                                }
                            }.start();
                            Log.i(TAG, "accept: ");
                        }
                        break;
                    }
                } else {
                    interruptAddress = ipAddress;
                    disconnectIn();
                    if (isOneUser) {
                        accept(serverSocket);
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            disconnectIn();
        }


    }


    private void disconnectIn() {
        mUserConnect.remove(interruptAddress);
        outputStreamHashMap.remove(interruptAddress);
        mServerSocketHashMap.remove(interruptAddress);
        listener.onDisconnect(isServerOpen, interruptAddress);
    }

    public void write(byte[] bytes) {
        write(connectUserIpAddress, bytes);
    }

    public void write(String ipAddress, byte[] bytes) {
        if (outputStreamHashMap != null) {
            OutputStream outputStream = outputStreamHashMap.get(ipAddress);
            if (outputStream != null) {
                try {
                    outputStream.write(bytes);
                    listener.onWrite(ipAddress, bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    listener.onWriteError(ipAddress);
                }
            } else {
                Log.i(TAG, "write: 未开启服务");
            }
        } else {
            Log.i(TAG, "write: 未开启服务");
        }
    }

    public void disconnect() {
        disconnect(connectUserIpAddress);
    }

    public void disconnect(String ip) {
        interruptAddress = ip;
        Socket socket = mServerSocketHashMap.get(ip);
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeServer() {
        if (serverSocket != null) {
            try {
                disconnectAllSocket();
                serverSocket.close();
                isServerOpen = false;
            } catch (IOException e) {
                e.printStackTrace();
                isServerOpen = false;
            }
            listener.onServerClose();
        }
    }

    /**
     * 关闭所有的用户连接
     */
    private void disconnectAllSocket() {
        if (mServerSocketHashMap != null) {
            Set<String> strings = mServerSocketHashMap.keySet();
            Iterator<String> iterator = strings.iterator();
            while (iterator.hasNext()) {
                String ip = iterator.next();
                Socket socket = mServerSocketHashMap.get(ip);
                try {
                    socket.close();
                    socket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isServerOpen() {
        return isServerOpen;
    }

    public HashMap<String, Socket> getAllUserSocket() {
        return mServerSocketHashMap;
    }

    public HashMap<String, Boolean> getAllConnectState() {
        return mUserConnect;
    }

    public boolean isUserConnect(String ip) {
        if (mUserConnect != null) {
            Boolean aBoolean = mUserConnect.get(ip);
            if (aBoolean != null) {
                return aBoolean;
            }
        }
        return false;
    }

    public String getLastUser() {
        return connectUserIpAddress;
    }

}
