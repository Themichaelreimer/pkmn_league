package com.pkmnleague.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MapObject extends Actor {
	//TODO: I don't think these actually have to be actors
	
	protected int[] coords;
	protected Vector3 screenSpaceCoords;
	
	Texture texture;
	
	public MapObject() {
		coords = new int[2];
		coords[0] = 0;
		coords[1] = 0;
		screenSpaceCoords = new Vector3();
	}
	
	public MapObject(int x, int y) {
		coords = new int[2];
		coords[0] = x;
		coords[1] = y;
		screenSpaceCoords = new Vector3(16f*x, 16f*y, 0.0f);
	}
	
	public int[] getPosition() {
		return coords;
	}
	
	public void setPos(int x, int y) {
		coords[0] = x;
		coords[1] = y;
	}
	
	protected float[] getTargetCamPos() {
		float[] pos = new float[2];
		pos[0] = 16f*coords[0];
		pos[1] = 16f*coords[1];
		return pos;
	}
	
	protected void moveToTargetPos() {
		
		float[] targetPos = getTargetCamPos();
		Vector3 target = new Vector3(targetPos[0],targetPos[1],0);
		
		
		float dist = target.dst(screenSpaceCoords);
		float maxDistPerFrame = dist/4;
		if(maxDistPerFrame<4f)
			maxDistPerFrame=4f;
		
		if(dist < maxDistPerFrame) {
			screenSpaceCoords = target;
		}else {
			Vector3 dv = target.sub(screenSpaceCoords).nor().scl(maxDistPerFrame);
			screenSpaceCoords.add(dv);
		}

	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		moveToTargetPos();
		batch.draw(texture, screenSpaceCoords.x, screenSpaceCoords.y,32,32);
	}
	
	public String logWithPosition(String str) {
		return String.format("%s(%d,%d)",str, coords[0],coords[1]);

	}
	
	public String toString() {
		return logWithPosition("MapObject");
	}
}
