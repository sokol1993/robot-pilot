package com.sokolowski.kamil.robopilot;

import android.app.Fragment;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.sokolowski.kamil.robopilot.bluetooth.ManageBluetooth;
import com.sokolowski.kamil.robopilot.map.field.Field;
import com.sokolowski.kamil.robopilot.robotControl.RobotControlCommand;

/**
 * Created by Kamil on 2016-01-28.
 */
public class MapModeFragment extends Fragment {

    ImageView map;

    private Button buttonModeButton;
    private Button positionModeButton;
    private Button mapModeButton;
    private Button connectButton;

    private ManageBluetooth manageBluetooth = ManageBluetooth.getInstance();

    Button startButton;
    Board board = new Board();
    Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buttonModeButton = (Button) getActivity().findViewById(R.id.buttonButtonMode);
        positionModeButton = (Button) getActivity().findViewById(R.id.buttonPositionMode);
        mapModeButton = (Button) getActivity().findViewById(R.id.buttonMapMode);
        connectButton = (Button) getActivity().findViewById(R.id.buttonBtConnect);

        View rootView = inflater.inflate(R.layout.map_mode_fragment, container, false);
        map = (ImageView) rootView.findViewById(R.id.drawLineImageView);
        map.setBackgroundColor(13619152);
        map.setImageDrawable(new DrawMapBoard());
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                map.setImageDrawable(new DrawMapBoard());
                map.invalidate();
            }
        };
        startButton = (Button) rootView.findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startButton.getText().toString().equals("Rozpocznij")) {
                    board.scanFields();
                    startButton.setEnabled(false);
                    buttonModeButton.setEnabled(false);
                    connectButton.setEnabled(false);
                    mapModeButton.setEnabled(false);
                    positionModeButton.setEnabled(false);
                }
            }
        });

        return rootView;
    }

    public class Board {

        private Field map[][];

        public Board() {
            map = new Field[8][8];
            clearBoard();
            manageBluetooth = ManageBluetooth.getInstance();
        }


        private void clearBoard() {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    map[i][j] = Field.UNKNOWN;
                }
            }
            map[7][0] = Field.ROBOT;
        }

        public Field[][] getMap() {
            return map;
        }

        public void scanFields() {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Message wiadomosc = new Message();
                        wiadomosc.obj = 1;
                        board.clearBoard();
                        handler.sendMessage(Message.obtain(wiadomosc));
                        for (int i = 0; i < 4; i++) {
                            if (i % 2 == 0) {
                                for (int j = 7; j > 0; j--) {
                                    manageBluetooth.sendData(RobotControlCommand.forwardsSensor());
                                    waitTime(1000);
                                    if (Integer.parseInt(new String(manageBluetooth.receiveData())) > 15) {
                                        manageBluetooth.sendData(RobotControlCommand.forwards());
                                        waitTime(4000);
                                        manageBluetooth.sendData(RobotControlCommand.stop());
                                        map[j][i] = Field.EMPTY;
                                        map[j - 1][i] = Field.ROBOT;
                                        Thread.sleep(2000);
                                        handler.sendMessage(Message.obtain(wiadomosc));
                                    } else {
                                        map[j - 1][i] = Field.USED;
                                        manageBluetooth.sendData(RobotControlCommand.rightwards());
                                        waitTime(4000);
                                        manageBluetooth.sendData(RobotControlCommand.stop());
                                        map[j][i] = Field.EMPTY;
                                        map[j][i + 1] = Field.ROBOT;
                                        handler.sendMessage(Message.obtain(wiadomosc));
                                        manageBluetooth.sendData(RobotControlCommand.forwardsSensor());
                                        waitTime(1000);
                                        if (Integer.parseInt(new String(manageBluetooth.receiveData())) > 15) {
                                            manageBluetooth.sendData(RobotControlCommand.forwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j][i + 1] = Field.EMPTY;
                                            map[j - 1][i + 1] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            if (j != 1) {
                                                manageBluetooth.sendData(RobotControlCommand.forwards());
                                                waitTime(4000);
                                                manageBluetooth.sendData(RobotControlCommand.stop());
                                                map[j - 1][i + 1] = Field.EMPTY;
                                                map[j - 2][i + 1] = Field.ROBOT;
                                                handler.sendMessage(Message.obtain(wiadomosc));
                                                manageBluetooth.sendData(RobotControlCommand.leftSensor());
                                                waitTime(1000);
                                                if (Integer.parseInt(new String(manageBluetooth.receiveData())) > 15) {
                                                    manageBluetooth.sendData(RobotControlCommand.leftwards());
                                                    waitTime(4000);
                                                    manageBluetooth.sendData(RobotControlCommand.stop());
                                                    map[j - 2][i + 1] = Field.EMPTY;
                                                    map[j - 2][i] = Field.ROBOT;
                                                    handler.sendMessage(Message.obtain(wiadomosc));
                                                } else {
                                                    if (j != 2) {
                                                        map[j - 2][i] = Field.USED;
                                                        manageBluetooth.sendData(RobotControlCommand.forwards());
                                                        waitTime(4000);
                                                        manageBluetooth.sendData(RobotControlCommand.stop());
                                                        map[j - 2][i + 1] = Field.EMPTY;
                                                        map[j - 3][i + 1] = Field.ROBOT;
                                                        handler.sendMessage(Message.obtain(wiadomosc));
                                                        manageBluetooth.sendData(RobotControlCommand.leftwards());
                                                        waitTime(4000);
                                                        manageBluetooth.sendData(RobotControlCommand.stop());
                                                        map[j - 3][i + 1] = Field.EMPTY;
                                                        map[j - 3][i] = Field.ROBOT;
                                                        handler.sendMessage(Message.obtain(wiadomosc));
                                                        j--;
                                                    }
                                                }
                                            }
                                        } else {
                                            map[j - 1][i + 1] = Field.USED;
                                            manageBluetooth.sendData(RobotControlCommand.rightwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j][i + 1] = Field.EMPTY;
                                            map[j][i + 2] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            manageBluetooth.sendData(RobotControlCommand.forwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j][i + 2] = Field.EMPTY;
                                            map[j - 1][i + 2] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            manageBluetooth.sendData(RobotControlCommand.forwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j - 1][i + 2] = Field.EMPTY;
                                            map[j - 2][i + 2] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            manageBluetooth.sendData(RobotControlCommand.leftwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j - 2][i + 2] = Field.EMPTY;
                                            map[j - 2][i + 1] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            manageBluetooth.sendData(RobotControlCommand.leftwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j - 2][i + 1] = Field.EMPTY;
                                            map[j - 2][i] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                        }
                                        j--;
                                    }
                                }
                                if (map[0][i] != Field.USED) {
                                    manageBluetooth.sendData(RobotControlCommand.rightwards());
                                    waitTime(4000);
                                    manageBluetooth.sendData(RobotControlCommand.stop());
                                    map[0][i] = Field.EMPTY;
                                    map[0][i + 1] = Field.ROBOT;
                                }
                                Thread.sleep(2000);
                                handler.sendMessage(Message.obtain(wiadomosc));
                            } else {
                                for (int j = 0; j < 7; j++) {
                                    manageBluetooth.sendData(RobotControlCommand.backwardsSensor());
                                    waitTime(1000);
                                    if (Integer.parseInt(new String(manageBluetooth.receiveData())) > 15) {
                                        manageBluetooth.sendData(RobotControlCommand.backwards());
                                        waitTime(4000);
                                        manageBluetooth.sendData(RobotControlCommand.stop());
                                        map[j][i] = Field.EMPTY;
                                        map[j + 1][i] = Field.ROBOT;
                                        Thread.sleep(2000);
                                        handler.sendMessage(Message.obtain(wiadomosc));
                                    } else {
                                        map[j + 1][i] = Field.USED;
                                        manageBluetooth.sendData(RobotControlCommand.rightwards());
                                        waitTime(4000);
                                        manageBluetooth.sendData(RobotControlCommand.stop());
                                        map[j][i] = Field.EMPTY;
                                        map[j][i + 1] = Field.ROBOT;
                                        handler.sendMessage(Message.obtain(wiadomosc));
                                        manageBluetooth.sendData(RobotControlCommand.backwardsSensor());
                                        waitTime(1000);
                                        if (Integer.parseInt(new String(manageBluetooth.receiveData())) > 15) {
                                            manageBluetooth.sendData(RobotControlCommand.backwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j][i + 1] = Field.EMPTY;
                                            map[j + 1][i + 1] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            if (j != 6) {
                                                manageBluetooth.sendData(RobotControlCommand.backwards());
                                                waitTime(4000);
                                                manageBluetooth.sendData(RobotControlCommand.stop());
                                                map[j + 1][i + 1] = Field.EMPTY;
                                                map[j + 2][i + 1] = Field.ROBOT;
                                                handler.sendMessage(Message.obtain(wiadomosc));
                                                manageBluetooth.sendData(RobotControlCommand.leftSensor());
                                                waitTime(1000);
                                                if (Integer.parseInt(new String(manageBluetooth.receiveData())) > 15) {
                                                    manageBluetooth.sendData(RobotControlCommand.leftwards());
                                                    waitTime(4000);
                                                    manageBluetooth.sendData(RobotControlCommand.stop());
                                                    map[j + 2][i + 1] = Field.EMPTY;
                                                    map[j + 2][i] = Field.ROBOT;
                                                    handler.sendMessage(Message.obtain(wiadomosc));
                                                } else {
                                                    if (j != 5) {
                                                        map[j + 2][i] = Field.USED;
                                                        manageBluetooth.sendData(RobotControlCommand.backwards());
                                                        waitTime(4000);
                                                        manageBluetooth.sendData(RobotControlCommand.stop());
                                                        map[j + 2][i + 1] = Field.EMPTY;
                                                        map[j + 3][i + 1] = Field.ROBOT;
                                                        handler.sendMessage(Message.obtain(wiadomosc));
                                                        manageBluetooth.sendData(RobotControlCommand.leftwards());
                                                        waitTime(4000);
                                                        manageBluetooth.sendData(RobotControlCommand.stop());
                                                        map[j + 3][i + 1] = Field.EMPTY;
                                                        map[j + 3][i] = Field.ROBOT;
                                                        handler.sendMessage(Message.obtain(wiadomosc));
                                                        j++;
                                                    }
                                                }
                                            }
                                        } else {
                                            map[j + 1][i + 1] = Field.USED;
                                            manageBluetooth.sendData(RobotControlCommand.rightwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j][i + 1] = Field.EMPTY;
                                            map[j][i + 2] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            manageBluetooth.sendData(RobotControlCommand.backwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j][i + 2] = Field.EMPTY;
                                            map[j + 1][i + 2] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            manageBluetooth.sendData(RobotControlCommand.backwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j + 1][i + 2] = Field.EMPTY;
                                            map[j + 2][i + 2] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            manageBluetooth.sendData(RobotControlCommand.leftwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j + 2][i + 2] = Field.EMPTY;
                                            map[j + 2][i + 1] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            manageBluetooth.sendData(RobotControlCommand.leftwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j + 2][i + 1] = Field.EMPTY;
                                            map[j + 2][i] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                        }
                                        j++;
                                    }
                                }
                                if (map[7][i] != Field.USED) {
                                    manageBluetooth.sendData(RobotControlCommand.rightwards());
                                    waitTime(4000);
                                    manageBluetooth.sendData(RobotControlCommand.stop());
                                    map[7][i] = Field.EMPTY;
                                    map[7][i + 1] = Field.ROBOT;
                                }
                                Thread.sleep(2000);
                                handler.sendMessage(Message.obtain(wiadomosc));
                            }
                        }

                        for (int i = 4; i < 8; i++) {
                            if (i % 2 == 0) {
                                for (int j = 7; j > 0; j--) {
                                    manageBluetooth.sendData(RobotControlCommand.forwardsSensor());
                                    waitTime(1000);
                                    if (Integer.parseInt(new String(manageBluetooth.receiveData())) > 15) {
                                        manageBluetooth.sendData(RobotControlCommand.forwards());
                                        waitTime(4000);
                                        manageBluetooth.sendData(RobotControlCommand.stop());
                                        map[j][i] = Field.EMPTY;
                                        map[j - 1][i] = Field.ROBOT;
                                        Thread.sleep(2000);
                                        handler.sendMessage(Message.obtain(wiadomosc));
                                    } else {
                                        map[j - 1][i] = Field.USED;
                                        manageBluetooth.sendData(RobotControlCommand.leftwards());
                                        waitTime(4000);
                                        manageBluetooth.sendData(RobotControlCommand.stop());
                                        map[j][i] = Field.EMPTY;
                                        map[j][i - 1] = Field.ROBOT;
                                        handler.sendMessage(Message.obtain(wiadomosc));
                                        manageBluetooth.sendData(RobotControlCommand.forwardsSensor());
                                        waitTime(1000);
                                        if (Integer.parseInt(new String(manageBluetooth.receiveData())) > 15) {
                                            manageBluetooth.sendData(RobotControlCommand.forwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j][i - 1] = Field.EMPTY;
                                            map[j - 1][i - 1] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            if (j != 1) {
                                                manageBluetooth.sendData(RobotControlCommand.forwards());
                                                waitTime(4000);
                                                manageBluetooth.sendData(RobotControlCommand.stop());
                                                map[j - 1][i - 1] = Field.EMPTY;
                                                map[j - 2][i - 1] = Field.ROBOT;
                                                handler.sendMessage(Message.obtain(wiadomosc));
                                                manageBluetooth.sendData(RobotControlCommand.rightSensor());
                                                waitTime(1000);
                                                if (Integer.parseInt(new String(manageBluetooth.receiveData())) > 15) {
                                                    manageBluetooth.sendData(RobotControlCommand.rightwards());
                                                    waitTime(4000);
                                                    manageBluetooth.sendData(RobotControlCommand.stop());
                                                    map[j - 2][i - 1] = Field.EMPTY;
                                                    map[j - 2][i] = Field.ROBOT;
                                                    handler.sendMessage(Message.obtain(wiadomosc));
                                                } else {
                                                    if (j != 2) {
                                                        map[j - 2][i] = Field.USED;
                                                        manageBluetooth.sendData(RobotControlCommand.forwards());
                                                        waitTime(4000);
                                                        manageBluetooth.sendData(RobotControlCommand.stop());
                                                        map[j - 2][i - 1] = Field.EMPTY;
                                                        map[j - 3][i - 1] = Field.ROBOT;
                                                        handler.sendMessage(Message.obtain(wiadomosc));
                                                        manageBluetooth.sendData(RobotControlCommand.rightwards());
                                                        waitTime(4000);
                                                        manageBluetooth.sendData(RobotControlCommand.stop());
                                                        map[j - 3][i - 1] = Field.EMPTY;
                                                        map[j - 3][i] = Field.ROBOT;
                                                        handler.sendMessage(Message.obtain(wiadomosc));
                                                        j--;
                                                    }
                                                }
                                            }
                                        } else {
                                            map[j - 1][i - 1] = Field.USED;
                                            manageBluetooth.sendData(RobotControlCommand.leftwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j][i - 1] = Field.EMPTY;
                                            map[j][i - 2] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            manageBluetooth.sendData(RobotControlCommand.forwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j][i - 2] = Field.EMPTY;
                                            map[j - 1][i - 2] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            manageBluetooth.sendData(RobotControlCommand.forwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j - 1][i - 2] = Field.EMPTY;
                                            map[j - 2][i - 2] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            manageBluetooth.sendData(RobotControlCommand.rightwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j - 2][i - 2] = Field.EMPTY;
                                            map[j - 2][i - 1] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            manageBluetooth.sendData(RobotControlCommand.rightwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j - 2][i - 1] = Field.EMPTY;
                                            map[j - 2][i] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                        }
                                        j--;
                                    }
                                }
                                if (map[0][i] != Field.USED) {
                                    manageBluetooth.sendData(RobotControlCommand.rightwards());
                                    waitTime(4000);
                                    manageBluetooth.sendData(RobotControlCommand.stop());
                                    map[0][i] = Field.EMPTY;
                                    map[0][i + 1] = Field.ROBOT;
                                }
                                Thread.sleep(2000);
                                handler.sendMessage(Message.obtain(wiadomosc));
                            } else {
                                for (int j = 0; j < 7; j++) {
                                    manageBluetooth.sendData(RobotControlCommand.backwardsSensor());
                                    waitTime(1000);
                                    if (Integer.parseInt(new String(manageBluetooth.receiveData())) > 15) {
                                        manageBluetooth.sendData(RobotControlCommand.backwards());
                                        waitTime(4000);
                                        manageBluetooth.sendData(RobotControlCommand.stop());
                                        map[j][i] = Field.EMPTY;
                                        map[j + 1][i] = Field.ROBOT;
                                        Thread.sleep(2000);
                                        handler.sendMessage(Message.obtain(wiadomosc));
                                    } else {
                                        map[j + 1][i] = Field.USED;
                                        manageBluetooth.sendData(RobotControlCommand.rightwards());
                                        waitTime(4000);
                                        manageBluetooth.sendData(RobotControlCommand.stop());
                                        map[j][i] = Field.EMPTY;
                                        map[j][i - 1] = Field.ROBOT;
                                        handler.sendMessage(Message.obtain(wiadomosc));
                                        manageBluetooth.sendData(RobotControlCommand.backwardsSensor());
                                        waitTime(1000);
                                        if (Integer.parseInt(new String(manageBluetooth.receiveData())) > 15) {
                                            manageBluetooth.sendData(RobotControlCommand.backwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j][i - 1] = Field.EMPTY;
                                            map[j + 1][i - 1] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            if (j != 6) {
                                                manageBluetooth.sendData(RobotControlCommand.backwards());
                                                waitTime(4000);
                                                manageBluetooth.sendData(RobotControlCommand.stop());
                                                map[j + 1][i - 1] = Field.EMPTY;
                                                map[j + 2][i - 1] = Field.ROBOT;
                                                handler.sendMessage(Message.obtain(wiadomosc));
                                                manageBluetooth.sendData(RobotControlCommand.rightSensor());
                                                waitTime(1000);
                                                if (Integer.parseInt(new String(manageBluetooth.receiveData())) > 15) {
                                                    manageBluetooth.sendData(RobotControlCommand.rightwards());
                                                    waitTime(4000);
                                                    manageBluetooth.sendData(RobotControlCommand.stop());
                                                    map[j + 2][i - 1] = Field.EMPTY;
                                                    map[j + 2][i] = Field.ROBOT;
                                                    handler.sendMessage(Message.obtain(wiadomosc));
                                                } else {
                                                    if (j != 5) {
                                                        map[j + 2][i] = Field.USED;
                                                        manageBluetooth.sendData(RobotControlCommand.backwards());
                                                        waitTime(4000);
                                                        manageBluetooth.sendData(RobotControlCommand.stop());
                                                        map[j + 2][i - 1] = Field.EMPTY;
                                                        map[j + 3][i - 1] = Field.ROBOT;
                                                        handler.sendMessage(Message.obtain(wiadomosc));
                                                        manageBluetooth.sendData(RobotControlCommand.rightwards());
                                                        waitTime(4000);
                                                        manageBluetooth.sendData(RobotControlCommand.stop());
                                                        map[j + 3][i - 1] = Field.EMPTY;
                                                        map[j + 3][i] = Field.ROBOT;
                                                        handler.sendMessage(Message.obtain(wiadomosc));
                                                        j++;
                                                    }
                                                }
                                            }
                                        } else {
                                            map[j + 1][i - 1] = Field.USED;
                                            manageBluetooth.sendData(RobotControlCommand.leftwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j][i - 1] = Field.EMPTY;
                                            map[j][i - 2] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            manageBluetooth.sendData(RobotControlCommand.backwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j][i - 2] = Field.EMPTY;
                                            map[j + 1][i - 2] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            manageBluetooth.sendData(RobotControlCommand.backwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j + 1][i - 2] = Field.EMPTY;
                                            map[j + 2][i - 2] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            manageBluetooth.sendData(RobotControlCommand.rightwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j + 2][i - 2] = Field.EMPTY;
                                            map[j + 2][i - 1] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                            manageBluetooth.sendData(RobotControlCommand.rightwards());
                                            waitTime(4000);
                                            manageBluetooth.sendData(RobotControlCommand.stop());
                                            map[j + 2][i - 1] = Field.EMPTY;
                                            map[j + 2][i] = Field.ROBOT;
                                            handler.sendMessage(Message.obtain(wiadomosc));
                                        }
                                        j++;
                                    }
                                }
                                if (i < 7) {
                                    if (map[7][i] != Field.USED) {
                                        manageBluetooth.sendData(RobotControlCommand.rightwards());
                                        waitTime(4000);
                                        manageBluetooth.sendData(RobotControlCommand.stop());
                                        map[7][i] = Field.EMPTY;
                                        map[7][i + 1] = Field.ROBOT;
                                    }
                                    Thread.sleep(2000);
                                    handler.sendMessage(Message.obtain(wiadomosc));
                                }
                            }
                        }
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startButton.setEnabled(true);
                                buttonModeButton.setEnabled(true);
                                mapModeButton.setEnabled(true);
                                positionModeButton.setEnabled(true);
                            }
                        }, 100);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }

        public void waitTime(int i) {
            long start = System.currentTimeMillis();
            while ((System.currentTimeMillis() - start) < i) {
            }
        }
    }

    public class DrawMapBoard extends Drawable {
        Board map = board;

        @Override
        public void draw(Canvas canvas) {

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(4);

            Paint paint2 = new Paint();
            paint2.setColor(Color.RED);

            Paint paint7 = new Paint();
            paint7.setColor(Color.GREEN);

            Paint paint5 = new Paint();
            paint5.setColor(Color.GRAY);

            Paint paint6 = new Paint();
            paint6.setColor(Color.YELLOW);

            Paint paint3 = new Paint();
            paint3.setColor(Color.WHITE);

            Paint paint4 = new Paint();
            paint4.setColor(Color.BLACK);
            paint4.setTextSize(50);

            Drawable robot = getResources().getDrawable(R.drawable.robot2);
            Drawable used = getResources().getDrawable(R.drawable.box);
            Drawable empty = getResources().getDrawable(R.drawable.empty);
            Drawable unknown = getResources().getDrawable(R.drawable.unknown);

            for (int i = 1; i < 9; i++) {
                for (int j = 2; j < 10; j++) {
                    switch (map.getMap()[i - 1][j - 2]) {
                        case ROBOT:
                            robot.setBounds(22 + j * 85, 72 + i * 85, 105 + j * 85, 155 + i * 85);
                            robot.draw(canvas);
                            break;
                        case USED:
                            used.setBounds(22 + j * 85, 72 + i * 85, 105 + j * 85, 155 + i * 85);
                            used.draw(canvas);
                            break;
                        case UNKNOWN:
                            unknown.setBounds(22 + j * 85, 72 + i * 85, 105 + j * 85, 155 + i * 85);
                            unknown.draw(canvas);
                            break;
                        case EMPTY:
                            empty.setBounds(22 + j * 85, 72 + i * 85, 105 + j * 85, 155 + i * 85);
                            empty.draw(canvas);
                            break;
                    }
                }
            }

            for (int i = 0; i < 9; i++) {
                canvas.drawRect(107, 72 + i * 85, 190, 155 + i * 85, paint5);
            }

            for (int j = 2; j < 10; j++) {
                canvas.drawRect(22 + j * 85, 72, 105 + j * 85, 155, paint5);
            }

            for (int i = 1; i < 11; i++) {
                canvas.drawLine(22 + 85 * i, 70, 22 + 85 * i, 837, paint);
            }

            for (int i = 0; i < 10; i++) {
                canvas.drawLine(105, 70 + 85 * i, 874, 70 + 85 * i, paint);
            }


            canvas.drawText("1", 222, 130, paint4);
            canvas.drawText("2", 307, 130, paint4);
            canvas.drawText("3", 392, 130, paint4);
            canvas.drawText("4", 477, 130, paint4);
            canvas.drawText("5", 562, 130, paint4);
            canvas.drawText("6", 647, 130, paint4);
            canvas.drawText("7", 732, 130, paint4);
            canvas.drawText("8", 817, 130, paint4);

            canvas.drawText("A", 132, 215, paint4);
            canvas.drawText("B", 132, 300, paint4);
            canvas.drawText("C", 132, 385, paint4);
            canvas.drawText("D", 132, 470, paint4);
            canvas.drawText("E", 132, 555, paint4);
            canvas.drawText("F", 132, 640, paint4);
            canvas.drawText("G", 132, 725, paint4);
            canvas.drawText("H", 132, 810, paint4);

        }

        @Override
        public int getOpacity() {
            return 0;
        }

        @Override
        public void setAlpha(int alpha) {
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
        }
    }

}
