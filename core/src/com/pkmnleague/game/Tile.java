package com.pkmnleague.game;

import com.badlogic.gdx.maps.MapProperties;

public class Tile {
	public int moveCost;
	public boolean breakable, superSolid, water, solid, foreground;
	public int x, y;

	
	public Tile(MapProperties props, int x, int y) {
		//this.moveCost = Integer.parseInt((String)props.get("moveCost"));
		try {
			this.moveCost = (int) props.get("moveCost");
		} catch(Exception e){
			this.moveCost = 1;
			System.err.printf("WARNING: Could not read moveCost (int) on tile at (%d,%d)\n",x,y);
		}
		//this.breakable = (boolean) props.get("breakable");
		try {
			this.superSolid = (boolean) props.get("superSolid");
		} catch(Exception e){
			this.superSolid = false;
			System.err.printf("WARNING: Could not read superSolid (bool) on tile at (%d,%d)\n",x,y);
		}

		try {
			this.solid = (boolean) props.get("solid");
		} catch(Exception e){
			this.solid = false;
			System.err.printf("WARNING: Could not read solid (bool) on tile at (%d,%d)\n",x,y);
		}

		try {
			this.water = (boolean) props.get("water");
		} catch(Exception e){
			this.water = false;
			System.err.printf("WARNING: Could not read superSolid (bool) on tile at (%d,%d)\n",x,y);
		}

		//this.foreground = (boolean) props.get("foreground");
		this.x = x;
		this.y = y;
	}
	
}
