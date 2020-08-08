package com.pkmnleague.game;

import com.badlogic.gdx.maps.MapProperties;

public class Tile {
	public int moveCost;
	public boolean breakable, grass, water, solid;
	
	public Tile(int moveCost, boolean breakable, boolean grass, boolean water, boolean solid) {
		this.moveCost = moveCost;
		this.breakable = breakable;
		this.grass = grass;
		this.water = water;
		this.solid = solid;
	}
	
	public Tile(MapProperties props) {
		this.moveCost = Integer.parseInt((String)props.get("move_cost"));
		this.breakable = (boolean) props.get("breakable");
		this.grass = (boolean) props.get("grass");
		this.water = (boolean) props.get("water");
		this.solid = (boolean) props.get("solid");
	}
}
