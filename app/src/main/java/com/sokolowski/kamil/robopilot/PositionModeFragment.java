package com.sokolowski.kamil.robopilot;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sokolowski.kamil.robopilot.bluetooth.ManageBluetooth;
import com.sokolowski.kamil.robopilot.robotControl.RobotControlCommand;

/**
 * Created by Kamil on 2016-01-28.
 */
public class PositionModeFragment extends Fragment implements SensorEventListener{

    private ImageView imageView;
    private SensorManager sm;
    int mode = 0;
    private ManageBluetooth manageBluetooth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.position_mode_fragment, container, false);

        imageView = (ImageView) view.findViewById(R.id.imageView);

        manageBluetooth = ManageBluetooth.getInstance();
        sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(this,sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),0,null);

        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.values[1] > 20){
            if(mode != 1 && event.values[2] > 20){
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.upleft));
                manageBluetooth.sendData(RobotControlCommand.forwardsLeft());
                mode = 1;
            }
            else if(mode != 2 && event.values[2] < -20){
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.upright));
                manageBluetooth.sendData(RobotControlCommand.forwardsRight());
                mode = 2;
            }
            else if(mode != 3 && event.values[2] >= -20 && event.values[2] <= 20 ){
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.up));
                manageBluetooth.sendData(RobotControlCommand.forwards());
                mode = 3;
            }
        }
        else if (event.values[1] < -20){
            if(mode != 4 && event.values[2] > 20){
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.downleft));
                manageBluetooth.sendData(RobotControlCommand.backwardsLeft());
                mode = 4;
            }
            else if(mode != 5 && event.values[2] < -20){
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.downright));
                manageBluetooth.sendData(RobotControlCommand.backwardsRight());
                mode = 5;
            }
            else if(mode != 6 && event.values[2] >= -20 && event.values[2] <= 20){
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.down));
                manageBluetooth.sendData(RobotControlCommand.backwards());
                mode = 6;
            }
        }
        else{
            if(mode !=7 && event.values[2] > 20){
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.left));
                manageBluetooth.sendData(RobotControlCommand.leftwards());
                mode = 7;
            }
            else if(mode !=8 && event.values[2] < -20){
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.right));
                manageBluetooth.sendData(RobotControlCommand.rightwards());
                mode = 8;
            }
            else if(event.values[2] >= -20 && event.values[2] <= 20){
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.stop));
                manageBluetooth.sendData(RobotControlCommand.stop());
                mode = 0;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onStop() {
        super.onStop();
        sm.unregisterListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION));
    }
}
