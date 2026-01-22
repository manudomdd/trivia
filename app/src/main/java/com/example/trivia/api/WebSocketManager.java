package com.example.trivia.api; // O el paquete que uses

import android.util.Log;
import java.net.URISyntaxException;
import io.socket.client.IO;
import io.socket.client.Socket;

public class WebSocketManager {
    private static WebSocketManager instance;
    private Socket mSocket;

    private static final String SERVER_URL = "https://nourishable-grapiest-alayna.ngrok-free.dev";

    private WebSocketManager() {
        try {
            IO.Options options = IO.Options.builder()
                    .setForceNew(true)
                    .build();
            mSocket = IO.socket(SERVER_URL, options);
        } catch (URISyntaxException e) {
            Log.e("Socket", "Error URI", e);
        }
    }

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public void connect() {
        if (!mSocket.connected()) {
            mSocket.connect();
        }
    }

    public void disconnect() {
        if (mSocket != null) {
            mSocket.disconnect();
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}