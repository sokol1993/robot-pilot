package com.sokolowski.kamil.robopilot.robotControl;

/**
 * Created by Kamil on 2016-02-23.
 */
public class RobotControlCommand {

    public static String forwards() {
        return "1";
    }

    public static String backwards(){
        return "2";
    }

    public static String rightwards() {
        return "3";
    }

    public static String leftwards() {
        return "4";
    }

    public static String stop() {
        return "5";
    }

    public static String forwardsRight() {
        return "6";
    }

    public static String forwardsLeft() {
        return "7";
    }

    public static String backwardsRight(){return "8";}

    public static String backwardsLeft(){return "9";}

    public static String forwardsSensor(){return "a";}

    public static String backwardsSensor(){return "b";}

    public static String rightSensor(){return "c";}

    public static String leftSensor(){return "d";}

    public static String lowSpeed(){return "e";}

    public static String mediumSpeed(){return "f";}

    public static String highSpeed(){return "g";}

    public static String goRun(){return "h";}
}
