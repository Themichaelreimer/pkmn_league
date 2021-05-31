package com.pkmnleague.game;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.OrderedSet;

public class Cursor extends Actor {
	
	private int pos_x,pos_y;
	private Vector3 drawCoords;
	private MapObject selectedObject1;
	private MapObject selectedObject2;
	private final Texture texture;
	private final Texture mapArea;

	private ArrayList<Tile> moveableTiles;
	private OrderedSet<Tile> attackableTiles;
	
	public Cursor(int startX, int startY) {
		 selectedObject1 = null;
		 selectedObject2 = null;
		 pos_x = startX;
		 pos_y = startY;
		 drawCoords = new Vector3();
		 texture = new Texture("assets/sprites/cursor.png");
		 mapArea = new Texture("assets/sprites/area.png");
		 moveableTiles = null;
		 attackableTiles = null;
	}
	
	public void setPos(int x, int y) {
		this.pos_x = x;
		this.pos_y = y;
	}
	
	public void cancel() {
		if(selectedObject1 != null)
			selectedObject1.deselect();
			selectedObject1 = null;
		if(moveableTiles != null)
			moveableTiles = null;
		if(attackableTiles != null)
			attackableTiles = null;
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
	
	public void setSelectedObject(MapObject obj, ArrayList<Tile> tiles, OrderedSet<Tile> attackable) {
		selectedObject1 = obj;
		moveableTiles = tiles;
		attackableTiles = attackable;
		selectedObject1.select(this);
		System.out.printf("#attackable tiles = %d\n ",attackable.size);
	}
	
	public void clearSelectedObject() {
		selectedObject1.deselect();
		selectedObject1 = null;
		moveableTiles = null;
		attackableTiles = null;
		
	}
	
	public ArrayList<Tile> getMoveableTiles(){
		return moveableTiles;
	}

	public OrderedSet<Tile> getAttackableTiles() { return attackableTiles; }

	protected float[] getTargetCamPos() {
		float[] pos = new float[2];
		pos[0] = 16f*pos_x;
		pos[1] = 16f*pos_y;
		return pos;
	}
	
	protected void moveToTargetPos() {
		
		float[] targetPos = getTargetCamPos();
		Vector3 target = new Vector3(targetPos[0],targetPos[1],0);
		
		float dist = target.dst(drawCoords);
		float maxDistPerFrame = dist/4;
		if(maxDistPerFrame<4f)
			maxDistPerFrame=4f;
		
		if(dist < maxDistPerFrame) {
			drawCoords = target;
		}else {
			Vector3 dv = target.sub(drawCoords).nor().scl(maxDistPerFrame);
			drawCoords.add(dv);
		}

	}

	/**
	 * Draws the cursor and meta tiles (attackable and movable tiles)
	 *
	 * @param batch - Sprite Batch
	 * @param alpha - Alpha blending constant
	 * @param drawMetaTiles - Whether to draw the meta tiles. True iff
	 * we're selecting a space to move to. If we tentatively select a space
	 * then this should be false.
	 */
	public void draw(Batch batch, float alpha, boolean drawMetaTiles) {

		moveToTargetPos();
		batch.draw(texture, drawCoords.x, drawCoords.y, 16f,16f);

		//Rest of function only draws the meta tiles
		if(!drawMetaTiles){
			return;
		}

		//batch.setColor(pre.r, pre.g, pre.b, 1f);
		batch.setColor(0f, 0.3f, 1f, 0.5f);
		if(this.moveableTiles != null){
			for(int i=0; i<this.moveableTiles.size();i++) {
				Tile tile = moveableTiles.get(i);
				batch.draw(mapArea,16f*tile.x,16f*tile.y,16f,16f);
			}
		}
		batch.setColor(1f,0.2f,0.2f, 0.5f);
		if (attackableTiles != null && moveableTiles != null){
			for(Tile t : attackableTiles){
				if (!moveableTiles.contains(t)){
					batch.draw(mapArea,16f*t.x,16f*t.y,16f,16f);
				}
			}
		}
		batch.setColor(Color.WHITE);

	}
}