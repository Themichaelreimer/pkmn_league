package com.pkmnleague.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MapObject extends Actor {
	//TODO: I don't think these actually have to be actors
	
	private int[] coords;
	Texture texture;
	
	public MapObject() {
		coords = new int[2];
		coords[0] = 0;
		coords[1] = 0;
	}
	
	public MapObject(int x, int y) {
		coords = new int[2];
		coords[0] = x;
		coords[1] = y;
	}
	
	public int[] getPosition() {
		return coords;
	}
	
	public void setPos(int x, int y) {
		coords[0] = x;
		coords[1] = y;
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		batch.draw(texture, 16f*coords[0], 16f*coords[1],32,32);
	}
	
	public String logWithPosition(String str) {
		return String.format("%s(%d,%d)",str, coords[0],coords[1]);

	}
	
	public String toString() {
		return logWithPosition("MapObject");
	}
}
