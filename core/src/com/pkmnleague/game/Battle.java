package com.pkmnleague.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Battle extends Actor{

	private Pokemon attacker, defender;
	private ShapeRenderer sr;
	private int ply;
	
	public Battle(Pokemon attacker, Pokemon defender) {
		this.attacker = attacker;
		this.defender = defender;
		ply = 0;
	}
	
	public void draw(Batch batch, float alpha) {
		batch.end();
		sr.begin(ShapeType.Filled);
		
		sr.end();
		
	}
	
	public boolean canDouble(Pokemon attacker, Pokemon defender) {
		return false;
	}
}
