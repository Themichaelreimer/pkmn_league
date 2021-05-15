package com.pkmnleague.game;
import com.badlogic.gdx.Input;

import java.util.HashSet;
import java.util.Set;

public class Controller {

    //Cursor movement vars
    private boolean up,down,left,right = false;
    private int upFrames, downFrames, leftFrames, rightFrames = 0;

    public Set<ControllerValues> keyDown(int keycode) {

        Set<ControllerValues> result = new HashSet<ControllerValues>();

        if(keycode == Input.Keys.UP) {
            up=true;
        }
        if(keycode == Input.Keys.DOWN) {
            down=true;
        }
        if(keycode == Input.Keys.RIGHT) {
            right=true;
        }
        if(keycode == Input.Keys.LEFT) {
            left=true;
        }
        if(keycode == Input.Keys.X) {
            result.add(ControllerValues.A);
        }
        if(keycode == Input.Keys.Z) {
            result.add(ControllerValues.B);
        }

        //if(keycode == Input.Keys.A) {
        //    result.add(ControllerValues.L);
        //}

        result.addAll(directionHandler());
        return result;
    }

    public void keyUp(int keycode) {
        if(keycode == Input.Keys.UP) {
            up=false;
        }
        if(keycode == Input.Keys.DOWN) {
            down=false;
        }
        if(keycode == Input.Keys.RIGHT) {
            right=false;
        }
        if(keycode == Input.Keys.LEFT) {
            left=false;
        }
    }

    public Set<ControllerValues> directionHandler() {
        Set<ControllerValues> result = new HashSet<>();
        final int LAG_FRAMES = 15;
        if(up) {
            if(upFrames == 0 || upFrames == LAG_FRAMES)
                result.add(ControllerValues.UP);
            if(upFrames < LAG_FRAMES)
                upFrames++;
        }
        else {
            upFrames = 0;
        }
        if(down) {
            if(downFrames == 0 || downFrames == LAG_FRAMES)
                result.add(ControllerValues.DOWN);
            if(downFrames < LAG_FRAMES)
                downFrames++;
        }else {
            downFrames = 0;
        }
        if(left) {
            if(leftFrames == 0 || leftFrames == LAG_FRAMES)
                result.add(ControllerValues.LEFT);
            if(leftFrames < LAG_FRAMES)
                leftFrames++;
        }else {
            leftFrames = 0;
        }
        if(right) {
            if(rightFrames == 0 || rightFrames == LAG_FRAMES)
                result.add(ControllerValues.RIGHT);
            if(rightFrames < LAG_FRAMES)
                rightFrames++;
        }else {
            rightFrames = 0;
        }
        return result;
    }
}
