package com.pkmnleague.game;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Cursor extends Actor {
	
	private int pos_x,pos_y;
	private MapObject selectedObject1;
	private MapObject selectedObject2;
	private Texture texture;
	private Texture mapArea;
	private ArrayList<Tile> moveableTiles;
	
	public Cursor(int startX, int startY) {
		 selectedObject1 = null;
		 selectedObject2 = null;
		 pos_x = startX;
		 pos_y = startY;
		 texture = new Texture("assets/sprites/cursor.png");
		 mapArea = new Texture("assets/sprites/area.png");
		 moveableTiles = null;
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
	
	public boolean hasSelectedObject() {
		return selectedObject1 != null;
	}
	
	public MapObject getSelectedObject() {
		return selectedObject1;
	}
	
	public void setSelectedObject(MapObject obj, ArrayList<Tile> tiles) {
		selectedObject1 = obj;
		moveableTiles = tiles;
	}
	
	public void clearSelectedObject() {
		selectedObject1 = null;
		moveableTiles = null;
	}
	
	public ArrayList<Tile> getMoveableTiles(){
		return moveableTiles;
	}
	
	@Override
	public void draw(Batch batch, float alpha) {

		batch.draw(texture, 16f*pos_x, 16f*pos_y, 16f,16f);
		//batch.setColor(pre.r, pre.g, pre.b, 1f);
		batch.setColor(0f, 0.3f, 1f, 0.5f);
		if(this.moveableTiles != null){
			for(int i=0; i<this.moveableTiles.size();i++) {
				Tile tile = moveableTiles.get(i);
				batch.draw(mapArea,16f*tile.x,16f*tile.y,16f,16f);
			}
		}
		batch.setColor(Color.WHITE);

	}
}