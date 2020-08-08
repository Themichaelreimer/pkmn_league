package com.pkmnleague.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Cursor extends Actor {
	
	private int x,y;
	private MapObject selectedObject1;
	private MapObject selectedObject2;
	private Texture texture;
	
	public Cursor(int startX, int startY) {
		 selectedObject1 = null;
		 selectedObject2 = null;
		 x = startX;
		 y = startY;
		 texture = new Texture("assets/sprites/cursor.png");
	}
	
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int[] getPos() {
		int[] res = new int[2];
		res[0] = x;
		res[1] = y;
		return res;
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		Color pre = batch.getColor();
		//batch.setColor(pre.r, pre.g, pre.b, alpha);
		batch.draw(texture, 16f*x, 16f*y, 16f,16f);
		//batch.setColor(pre.r, pre.g, pre.b, 1f);
	}
}