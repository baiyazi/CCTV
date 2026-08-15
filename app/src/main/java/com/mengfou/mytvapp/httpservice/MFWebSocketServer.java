package com.mengfou.mytvapp.httpservice;

import com.mengfou.mytvapp.log.Log;
import com.mengfou.mytvapp.manager.ApkUpdateManager;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;


/**
 * 使用Java-WebSocket来构建服务端
 * @author 梦否 on 2024/1/20
 * @blog https://mengfou.blog.csdn.net/
 * https://www.cnblogs.com/guoapeng/p/17020317.html
 */
public class MFWebSocketServer extends WebSocketServer {
    private static final String TAG = MFWebSocketServer.class.getName();

    private MFWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    private MFWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Welcome to the server!");
        Log.e(TAG, "onOpen: " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        broadcast(conn + " has left the room!");
        Log.e(TAG, "has left the room!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // broadcast(message);
        Log.e(TAG, "onMessage: " + conn + ": " + message );
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        super.onMessage(conn, message);
        byte[] apkByteContent = message.array();
        Log.e(TAG, "onMessage apk大小: " + apkByteContent.length);

        ApkUpdateManager.updateApk(apkByteContent);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Log.e(TAG, "onError: " + ex.getLocalizedMessage());
    }

    @Override
    public void onStart() {
        Log.e(TAG, "Server started! " + getAddress().getHostName() + ":" + getAddress().getPort());
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    public static class Builder {
        private int mPort = 1111;
        public MFWebSocketServer.Builder setServerPort(int port) {
            mPort = port;
            return this;
        }

        public MFWebSocketServer build() {
            return new MFWebSocketServer(mPort);
        }
    }

    public void startService() {
        start();
        android.util.Log.e(TAG, "ChatServer started on port: " + this.getPort());
    }
}


