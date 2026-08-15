package com.mengfou.mytvapp.httpservice.demo;


import com.mengfou.mytvapp.log.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

/**
 * @author 梦否
 * @date 2024/01/20 8:43
 * https://blog.csdn.net/yingtian648/article/details/91043353
 * com.neovisionaries:nv-websocket-client
 */
public class MFWebSocketClient extends WebSocketClient {
    public MFWebSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.e("JWebSocketClient", "onOpen()");
    }

    @Override
    public void onMessage(String message) {
        Log.e("JWebSocketClient", "onMessage()");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e("JWebSocketClient", "onClose()");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        Log.e("JWebSocketClient", "onError()");
    }

    public static void main(String[] args) {
        URI uri = URI.create("ws://192.168.1.6:1111");
        final MFWebSocketClient client = new MFWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                Log.e("JWebSocketClientService", message);
            }

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                super.onOpen(handshakedata);
                Log.e("JWebSocketClientService", "websocket连接成功");
                sendMsg(this,"id");

                String apkPath = "D:\\project_learn_work\\App\\MyTvApp\\app\\build\\outputs\\apk\\release\\app-release.apk";
                File file = new File(apkPath);
                Log.e("JWebSocketClientService", "file.exist " + file.exists());
                if(file.exists()) {
                    Log.e("JWebSocketClientService", "上传apk中...");
                    uploadApk(this, file);
                }
            }
        };
        connectS(client);
    }

    /**
     * 连接websocket
     */
    private static void connectS(final MFWebSocketClient client) {
        new Thread() {
            @Override
            public void run() {
                try {
                    //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                    client.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    public void sendMsg(MFWebSocketClient client, String msg) {
        if (null != client) {
            Log.e("JWebSocketClientService", msg);
            client.send(msg);
        }
    }

    void uploadApk(MFWebSocketClient client, File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len = -1;
            byte[] buffer = new byte[2048 * 10];
            while((len = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            client.send(outputStream.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


