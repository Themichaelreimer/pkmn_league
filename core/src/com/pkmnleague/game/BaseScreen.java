package com.pkmnleague.game;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.Set;

public interface BaseScreen {

    public void render(Batch batch);
    public void handleInput(Set<ControllerValues> input);

}
