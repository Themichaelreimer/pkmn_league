package com.pkmnleague.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Cursor extends Actor {
	
	private int pos_x,pos_y;
	private MapObject selectedObject1;
	private MapObject selectedObject2;
	private Texture texture;
	
	public Cursor(int startX, int startY) {
		 selectedObject1 = null;
		 selectedObject2 = null;
		 pos_x = startX;
		 pos_y = startY;
		 texture = new Texture("assets/sprites/cursor.png");
	}
	
	public void setPos(int x, int y) {
		this.pos_x = x;
		this.pos_y = y;
	}
	
	public void move(int dx, int dy) {
		this.pos_x += dx;
		this.pos_y += dy;
	}
	
	public int[] getPos() {
		int[] res = new int[2];
		res[0] = pos_x;
		res[1] = pos_y;
		return res;
	}
	
	public int X() {
		return this.pos_x;
	}
	
	public int Y() {
		return this.pos_y;
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		Color pre = batch.getColor();
		//batch.setColor(pre.r, pre.g, pre.b, alpha);
		batch.draw(texture, 16f*pos_x, 16f*pos_y, 16f,16f);
		//batch.setColor(pre.r, pre.g, pre.b, 1f);
	}
}