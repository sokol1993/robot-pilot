package com.sokolowski.kamil.robopilot;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sokolowski.kamil.robopilot.bluetooth.ManageBluetooth;
import com.sokolowski.kamil.robopilot.robotControl.RobotControlCommand;


/**
 * Created by Kamil on 2016-01-28.
 */
public class ButtonModeFragment extends Fragment {
    private ImageButton up;
    private ImageButton upRight;
    private ImageButton upLeft;
    private ImageButton left;
    private ImageButton right;
    private ImageButton down;
    private ImageButton downRight;
    private ImageButton downLeft;
    private ImageButton stop;

    private TextView speedText;
    private SeekBar speedBar;

    int speed = 0;
    int mode;

    private ManageBluetooth manageBluetooth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.button_mode_fragment, container, false);

        manageBluetooth = ManageBluetooth.getInstance();

        speedText = (TextView) view.findViewById(R.id.textPredkosc);

        up = (ImageButton) view.findViewById(R.id.imageButtonUp);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manageBluetooth.sendData(RobotControlCommand.forwards());
                mode = 1;
            }
        });
        upRight = (ImageButton) view.findViewById(R.id.imageButtonUpRight);
        upRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manageBluetooth.sendData(RobotControlCommand.forwardsRight());
                mode = 2;
            }
        });
        upLeft = (ImageButton) view.findViewById(R.id.imageButtonUpLeft);
        upLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = 3;
                manageBluetooth.sendData(RobotControlCommand.forwardsLeft());
            }
        });
        left = (ImageButton) view.findViewById(R.id.imageButtonLeft);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manageBluetooth.sendData(RobotControlCommand.leftwards());
                mode = 1;
            }
        });
        right = (ImageButton) view.findViewById(R.id.imageButtonRight);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = 1;
                manageBluetooth.sendData(RobotControlCommand.rightwards());

            }
        });
        down = (ImageButton) view.findViewById(R.id.imageButtonDown);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manageBluetooth.sendData(RobotControlCommand.backwards());
                mode = 1;
            }
        });
        downRight = (ImageButton) view.findViewById(R.id.imageButtonDownRight);
        downRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manageBluetooth.sendData(RobotControlCommand.backwardsRight());
                mode = 4;
            }
        });
        downLeft = (ImageButton) view.findViewById(R.id.imageButtonDownLeft);
        downLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manageBluetooth.sendData(RobotControlCommand.backwardsLeft());
                mode = 5;
            }
        });
        stop = (ImageButton) view.findViewById(R.id.imageButtonStop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manageBluetooth.sendData(RobotControlCommand.stop());

            }
        });

        speedBar = (SeekBar) view.findViewById(R.id.seekBar1);
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = progress;
                Toast.makeText(getActivity().getApplicationContext(), "Zmieniono prędkość", Toast.LENGTH_SHORT).show();
                switch (speed) {
                    case 0:
                        speedText.setText("Szybkość: mała");
                        manageBluetooth.sendData(RobotControlCommand.lowSpeed());
                        break;
                    case 1:
                        speedText.setText("Szybkość: średnia");
                        manageBluetooth.sendData(RobotControlCommand.mediumSpeed());
                        break;
                    case 2:
                        speedText.setText("Szybkość: duża");
                        manageBluetooth.sendData(RobotControlCommand.highSpeed());
                        break;
                }
                switch (mode) {
                    case 1:
                        manageBluetooth.sendData(RobotControlCommand.goRun());
                        break;
                    case 2:
                        manageBluetooth.sendData(RobotControlCommand.forwardsRight());
                        break;
                    case 3:
                        manageBluetooth.sendData(RobotControlCommand.forwardsLeft());
                        break;
                    case 4:
                        manageBluetooth.sendData(RobotControlCommand.backwardsRight());
                        break;
                    case 5:
                        manageBluetooth.sendData(RobotControlCommand.backwardsLeft());
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        return view;
    }
}
