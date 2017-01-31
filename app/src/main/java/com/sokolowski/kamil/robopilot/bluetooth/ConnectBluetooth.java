package com.sokolowski.kamil.robopilot.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Kamil on 2016-01-29.
 */
public class ConnectBluetooth extends Thread {
    private BluetoothSocket btSocket;

    public boolean connect(BluetoothDevice bTDevice, UUID mUUID) {
        BluetoothSocket temp = null;
        try {
            temp = bTDevice.createRfcommSocketToServiceRecord(mUUID);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            btSocket = temp;
            btSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                btSocket.close();
            } catch (IOException close) {
                e.printStackTrace();
                return false;
            }
        }
        new ManageBluetooth(btSocket);
        return true;
    }

    public boolean cancel() {
        try {
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
