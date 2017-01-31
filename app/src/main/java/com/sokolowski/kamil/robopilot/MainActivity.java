package com.sokolowski.kamil.robopilot;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sokolowski.kamil.robopilot.bluetooth.ConnectBluetooth;
import com.sokolowski.kamil.robopilot.bluetooth.ManageBluetooth;
import com.sokolowski.kamil.robopilot.robotControl.RobotControlCommand;

import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button buttonModeButton;
    private Button positionModeButton;
    private Button mapModeButton;
    private Button connectButton;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private TextView btStatusText;
    private TextView modeText;

    private ManageBluetooth manageBluetooth;

    private final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ConnectBluetooth connectBluetooth;
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btStatusText = (TextView) findViewById(R.id.textBtStatus);
        modeText = (TextView) findViewById(R.id.textMode);

        fragment = new StartingFragment();
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

        buttonModeButton = (Button) findViewById(R.id.buttonButtonMode);
        buttonModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new ButtonModeFragment();
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                modeText.setText("Tryb: sterowanie przyciskami");
                buttonModeButton.setEnabled(false);
                mapModeButton.setEnabled(true);
                positionModeButton.setEnabled(true);
                manageBluetooth.sendData(RobotControlCommand.stop());
                manageBluetooth.sendData(RobotControlCommand.mediumSpeed());
            }
        });

        positionModeButton = (Button) findViewById(R.id.buttonPositionMode);
        positionModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new PositionModeFragment();
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                modeText.setText("Tryb: sterowanie czujnikiem położenia");
                buttonModeButton.setEnabled(true);
                mapModeButton.setEnabled(true);
                positionModeButton.setEnabled(false);
                manageBluetooth.sendData(RobotControlCommand.stop());
                manageBluetooth.sendData(RobotControlCommand.mediumSpeed());
            }
        });

        mapModeButton = (Button) findViewById(R.id.buttonMapMode);
        mapModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new MapModeFragment();
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                modeText.setText("Tryb: rysowanie mapy terenu");
                buttonModeButton.setEnabled(true);
                mapModeButton.setEnabled(false);
                positionModeButton.setEnabled(true);
                manageBluetooth.sendData(RobotControlCommand.stop());
                manageBluetooth.sendData(RobotControlCommand.mediumSpeed());
            }
        });

        connectButton = (Button) findViewById(R.id.buttonBtConnect);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Próba połączenia", Toast.LENGTH_LONG).show();
                pairedDevices = bluetoothAdapter.getBondedDevices();
                for(BluetoothDevice device : pairedDevices)
                    if(device.getAddress().equals("20:15:04:15:80:86")){
                        connectBluetooth = new ConnectBluetooth();
                        if(connectBluetooth.connect(device, MY_UUID)){
                            manageBluetooth = ManageBluetooth.getInstance();
                            if(manageBluetooth.checkSocketState()) {
                                Toast.makeText(getApplicationContext(), "Połączono", Toast.LENGTH_LONG).show();
                                buttonModeButton.setEnabled(true);
                                mapModeButton.setEnabled(true);
                                positionModeButton.setEnabled(true);
                                connectButton.setEnabled(false);
                                btStatusText.setText("Status: połączony");
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Nie połączono", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            buttonModeButton.setEnabled(false);
            connectButton.setEnabled(false);
            positionModeButton.setEnabled(false);
            mapModeButton.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Urządzenie nie wspiera bluetooth!",
                    Toast.LENGTH_LONG).show();
        } else {
            if(!bluetoothAdapter.isEnabled()){
                connectButton.setEnabled(false);
                btStatusText.setText("Status: wyłączony");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                Toast.makeText(getApplicationContext(), "Prośba o włączenie bluetooth", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                btStatusText.setText("Status: Nie połączony");
                connectButton.setEnabled(true);
            }
        }
    }
}