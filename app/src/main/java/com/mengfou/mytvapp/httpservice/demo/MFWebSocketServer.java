package com.mengfou.mytvapp.httpservice.demo;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
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

    public MFWebSocketServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public MFWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Welcome to the server!"); // This method sends a message to the new client
        broadcast("new connection: " + handshake.getResourceDescriptor()); // This method sends a message to all clients connected
        Log.e(TAG, "onOpen: " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        broadcast(conn + " has left the room!");
        Log.e(TAG, "has left the room!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        broadcast(message);
        Log.e(TAG, "onMessage: " + conn + ": " + message );
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        Log.e(TAG, "onError: " + ex.getLocalizedMessage());
    }

    @Override
    public void onStart() {
        Log.e(TAG, "Server started! " + getAddress().getHostName() + ":" + getAddress().getPort());
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }


    public static void main(String[] args) throws InterruptedException, IOException {
        int port = 8887;

        MFWebSocketServer s = new MFWebSocketServer(port);
        s.start();
        System.out.println("ChatServer started on port: " + s.getPort());

        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = sysin.readLine();
            s.broadcast(in);
            if (in.equals("exit")) {
                s.stop(1000);
                break;
            }
        }
    }

}


