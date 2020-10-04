package com.pkmnleague.game;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;


// TODO:
// Implement Menu state machine

public class ActionMenu {
	
	public static enum ACTION{
		ATTACK,
		//TODO: Once items/inventory are implemented
		//ITEM, 
		//TRADE,
		MOVE,
		CANCEL
	}
	
	public static enum ACTIONMENUSTATE{
		ACTION,
		// Attack submenu states
		ATTACK_TARGET,
		SELECT_ATTACK,
		// Trade submenu states
		TRADE_TARGET,
		TRADE
		
	}
	
	int selectedIndex;
	ArrayList<ACTION> actions;
	Pokemon pokemon;
	ArrayList<MapObject> interactables; // Pokemon, rocks, etc
	int prevX,prevY;
	
	public ActionMenu(Pokemon pkmn, int dstX, int dstY, int prevX, int prevY) {
		actions = new ArrayList<ACTION>();
		actions.add(ACTION.MOVE);
		actions.add(ACTION.CANCEL);
		this.prevX = prevX;
		this.prevY = prevY;
		this.pokemon = pkmn;
		this.pokemon.setPos(dstX, dstY); // We set the position immediately for drawing.
										 // Exiting without commiting reverts the position
	}
	
	public void up() {
		selectedIndex = (selectedIndex-1)%actions.size();
	}
	
	public void down() {
		selectedIndex = (selectedIndex+1)%actions.size();
	}
	
	public boolean select() {
		// Returns whether to destroy the menu
		ACTION action = actions.get(selectedIndex);
		
		if(action == ACTION.MOVE) {
			// Commit to move; change pokemon state to moved
			pokemon.setMoved();
			return true;
			
		}else if(action == ACTION.CANCEL) {
			undoAction();
			return true;
		}
		
		return false;
	}
	
	public boolean back() {
		// Returns whether to destroy the menu
		
		//For now, this always undos and exits.
		undoAction();
		return true;
		
	}
	
	public void undoAction() {
		pokemon.setPos(prevX, prevY);
	}
	
	public void render(Batch batch) {
		
	}

}

