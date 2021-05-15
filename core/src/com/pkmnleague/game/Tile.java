package com.pkmnleague.game;

import com.badlogic.gdx.maps.MapProperties;

public class Tile {
	public int moveCost;
	public boolean breakable, grass, water, solid, foreground;
	public int x, y;

	
	public Tile(MapProperties props, int x, int y) {
		this.moveCost = Integer.parseInt((String)props.get("move_cost"));
		this.breakable = (boolean) props.get("breakable");
		this.grass = (boolean) props.get("grass");
		this.water = (boolean) props.get("water");
		this.solid = (boolean) props.get("solid");
		this.foreground = (boolean) props.get("foreground");
		this.x = x;
		this.y = y;
	}
	
}
