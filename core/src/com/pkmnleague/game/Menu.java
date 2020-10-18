package com.pkmnleague.game;


import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Menu extends Actor{
	
	enum STATE{
		DEFAULT,
		SELECT_ATTACK,
		SELECT_TRADE,
		CONFIRM_ATTACK,
		TRADE
	}
	
	private static final String[] OPTIONS = { "Battle", "Trade", "Move", "Info", "Cancel" };
	private ArrayList<String> options;
	private BitmapFont font;
	private Level level; // Might not need both of these
	private Cursor curs;
	private int index;
	private int selectorIndex;
	
	private int x, y;
	private int PADDING = 2;
	private ShapeRenderer sr;
	
	private Pokemon pokemon;
	private ArrayList<Pokemon> tradeable;
	private ArrayList<Pokemon> attackable;
	
	private STATE state;
	
	// INPUT CONTROLS ------------------------------------------------------------------------------
	
	public void up() {
		if(state == STATE.DEFAULT) {
			index = (index - 1)%options.size();
			// Java is dum, it returns remainder not modulus
			if (index<0)
				index += options.size(); 
		}else if(state == STATE.SELECT_ATTACK) {
			selectorIndex = (selectorIndex-1)%attackable.size();
			if(selectorIndex<0)
				selectorIndex += attackable.size();
		}else if(state == STATE.SELECT_TRADE) {
			selectorIndex = (selectorIndex-1)%tradeable.size();
			if(selectorIndex<0)
				selectorIndex += tradeable.size();
		}

		
	}
	
	public void down() {
		if(state == STATE.DEFAULT) {
			index = (index + 1)%options.size();
		} else if(state == STATE.SELECT_ATTACK) {
			selectorIndex = (selectorIndex+1)%attackable.size();
		}else if(state == STATE.SELECT_TRADE) {
			selectorIndex = (selectorIndex+1)%tradeable.size();
		}


	}
	
	public String press() {
		if(state == STATE.DEFAULT) {
			String command = options.get(index);
			if(command.equalsIgnoreCase("Battle")) {
				selectorIndex=0;
				state = STATE.SELECT_ATTACK;
			}else if(command.equalsIgnoreCase("Move")) {
				return "Move";
			}else if(command.equalsIgnoreCase("Trade")) {
				selectorIndex=0;
				state = STATE.SELECT_TRADE;
			}
		}

		return "";
	}
	
	public boolean back() {
		if(state == STATE.DEFAULT)
			return true;
		else if(state == STATE.SELECT_ATTACK) {
			state = STATE.DEFAULT;
			return false;
		}else if(state == STATE.SELECT_TRADE) {
			state = STATE.DEFAULT;
			return false;
		}
			
		return false;
	}

	// REQUIRED FUNCTIONS -------------------------------------------------------------------------
	
	public Menu(Level srcLevel, int x, int y) {
		font = new BitmapFont();
		this.x = x;
		this.y = y;
		index = 0;
		selectorIndex = 0;
		sr = new ShapeRenderer();
		state = STATE.DEFAULT;
		
		level = srcLevel;
		
		tradeable = level.getAdjacentTradable(true);
		attackable = level.getAdjacentAttackable(true);
		options = new ArrayList<String>();
		
		if(attackable.size() > 0)
			options.add("Battle");
		if(tradeable.size() > 0)
			options.add("Trade");
		options.add("Info");
		options.add("Move");
		
	}
	
	private void drawDefaultState(Batch batch, float alpha) {
		int xPixels = x;
		int yPixels = y;
		
		int top = yPixels + PADDING;
		int height = 14*options.size() + PADDING;
		int left = xPixels - PADDING;
		int width = 50;
		
		//System.out.printf("(%d,%d) to (%d,%d)\n", left,top,left+width,top+height);
		
		batch.end();
		Matrix4 mat = batch.getTransformMatrix();
		Matrix4 matt = batch.getProjectionMatrix();
		
		sr.setTransformMatrix(mat);
		sr.setProjectionMatrix(matt);
		sr.begin(ShapeType.Filled);
		
		//Background
		sr.setColor(0.1f, 0.1f, 0.1f, 1);
		sr.rect(left,top,width,height);
		
		//Selected Item
		sr.setColor(0.99f, 0.66f,0.05f,1);
		sr.rect(left,top+height-(14*index)-14,width,14);
		
		sr.end();
		batch.begin();
		
		for(int i=0;i<options.size();i++)
			font.draw(batch, options.get(i),xPixels,yPixels+height - 14*i);
	}
	
	private void drawAttackSelect(Batch batch, float alpha) {
		Pokemon curPokemon = attackable.get(selectorIndex);
		int[] coords = curPokemon.getPosition();
		int[] camPos = level.getCamPos();
		float[] viewp = level.getViewPort();
		int x = 16*(coords[0] - camPos[0] + (int)viewp[0]/32);
		int y = 16*(coords[1] - camPos[1] + (int)viewp[1]/32);
		
		batch.end();
		Matrix4 mat = batch.getTransformMatrix();
		Matrix4 matt = batch.getProjectionMatrix();
		
		sr.setTransformMatrix(mat);
		sr.setProjectionMatrix(matt);
		sr.begin(ShapeType.Line);
		
		//Base
		sr.setColor(0.99f, 0.66f, 0.1f, 1);
		sr.rect(x,y,16,16);
		
		//Highlight
		sr.setColor(0.99f, 0.88f,0.55f,1);
		sr.rect(x+1,y+1,14,14);
		
		sr.end();
		batch.begin();
		
	}
	
	private void drawTradeSelect(Batch batch, float alpha) {
		Pokemon curPokemon = tradeable.get(selectorIndex);
		int[] coords = curPokemon.getPosition();
		int[] camPos = level.getCamPos();
		float[] viewp = level.getViewPort();
		int x = 16*(coords[0] - camPos[0] + (int)viewp[0]/32);
		int y = 16*(coords[1] - camPos[1] + (int)viewp[1]/32);
		
		batch.end();
		Matrix4 mat = batch.getTransformMatrix();
		Matrix4 matt = batch.getProjectionMatrix();
		
		sr.setTransformMatrix(mat);
		sr.setProjectionMatrix(matt);
		sr.begin(ShapeType.Line);
		
		//Base
		sr.setColor(0.1f, 0.99f, 0.66f, 1);
		sr.rect(x,y,16,16);
		
		//Highlight
		sr.setColor(0.55f, 0.99f,0.88f,1);
		sr.rect(x+1,y+1,14,14);
		
		sr.end();
		batch.begin();
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		if(state == STATE.DEFAULT)
			drawDefaultState(batch,alpha);
		if(state == STATE.SELECT_ATTACK)
			drawAttackSelect(batch,alpha);
		if(state == STATE.SELECT_TRADE)
			drawTradeSelect(batch,alpha);
	}
	
}

