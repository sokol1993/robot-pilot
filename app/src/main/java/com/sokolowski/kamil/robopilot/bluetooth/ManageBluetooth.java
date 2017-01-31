package com.sokolowski.kamil.robopilot.bluetooth;

import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Kamil on 2016-01-29.
 */
public class ManageBluetooth extends Thread {
    private static ManageBluetooth instance = null;
    private static BluetoothSocket socket;
    OutputStream mmOutputStream;
    InputStream mmInputStream;

    public ManageBluetooth(BluetoothSocket socket) {
        this.socket = socket;
        try {
            mmOutputStream = socket.getOutputStream();
            mmInputStream = socket.getInputStream();
        } catch (IOException e) {

        }
        instance = this;
    }

    public boolean checkSocketState() {
        return socket.isConnected();
    }

    public static ManageBluetooth getInstance() {
        return instance;
    }

    public void sendData(String message) {
        byte[] msgBuffer = message.getBytes();
        try {
            mmOutputStream.write(msgBuffer);
        } catch (IOException e) {
        }
    }

    public byte[] receiveData() {
        int bytes;
        int availableBytes;
        try {
            availableBytes = mmInputStream.available();
            if (availableBytes > 0) {
                byte[] buffer = new byte[availableBytes];  // buffer store for the stream
                // Read from the InputStream
                bytes = mmInputStream.read(buffer);
                return buffer;
            }
        } catch (IOException e) {

        }
        return null;
    }

}
