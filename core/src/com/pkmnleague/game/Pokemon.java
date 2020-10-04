package com.pkmnleague.game;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;

public class Pokemon extends MapObject{

	// The following is directly derived from pokemon.csv and stored here on load
	private String name;
	private String type1,type2;
	private int hp,atk,def,spd,spAtk,spDef,acc;
	private int hpGrowth, atkGrowth, defGrowth,spdGrowth,spAtkGrowth,spDefGrowth,accGrowth;
	private int move;
	private boolean mountable;
	private int evolvesInto, evolvesAt;
	
	//Derived values
	private int level, exp;
	private boolean moved;
	
	//The following is determined by the pokemon's name
	Texture iconSprite, portraitSpriteAtk, portraitSpriteDef;
	
	//TODO: Inventory list, including moves
	
	private enum EXPGROUP{
		SLOW,
		MEDIUM,
		FAST
	}
	
	public boolean hasMoved() {
		return moved;
	}
	
	public void setMoved() {
		moved = true;
	}
	
	public boolean hasType(String typeName) {
		boolean result = typeName.equalsIgnoreCase(type1); //Yes I could make this one line. Ehhh.
		if(type2 != null) {
			return result || (typeName.equalsIgnoreCase(type2));	
		}
		return result;
	}
	
	private static String getIconPath(String pokemonName) {
		return String.format("assets/sprites/pokemon/icon/%s_g.png",pokemonName);
	}
	
	private static String getPortraitPath(String pokemonName) {
		return String.format("assets/sprites/pokemon/portrait/%s.png",pokemonName); //TODO: Append 2 for attacker, 1 for defender sprite
	}
	
	public Pokemon(String name, int level) throws Exception {
		Dataset data = Dataset.getDataset();
		HashMap<String,String> pokemonData = data.lookupPokemon(name);
		iconSprite = new Texture(getIconPath(name));
		portraitSpriteDef = new Texture(getPortraitPath(name));
		this.texture = iconSprite;
		
		if(pokemonData == null) {
			throw new Exception("Pokemon '"+name+"' not found!");
		}
		
		//Read data from """database""" """row"""
		this.name = name.substring(0,1).toUpperCase() + name.substring(1);
		this.type1 = pokemonData.get("type1");
		this.type2 = pokemonData.get("type2");
		this.hpGrowth = Integer.parseInt(pokemonData.get("hp"));
		this.atkGrowth = Integer.parseInt(pokemonData.get("attack"));
		this.defGrowth = Integer.parseInt(pokemonData.get("defense"));
		this.spdGrowth = Integer.parseInt(pokemonData.get("speed"));
		this.spAtkGrowth = Integer.parseInt(pokemonData.get("spAtk"));
		this.spDefGrowth = Integer.parseInt(pokemonData.get("spDef"));
		this.accGrowth = Integer.parseInt(pokemonData.get("acc"));
		this.move = Integer.parseInt(pokemonData.get("move"));
		this.evolvesInto = Integer.parseInt(pokemonData.get("evolvesTo"));
		this.evolvesAt = Integer.parseInt(pokemonData.get("evolvesAt"));
		
		this.level = level;
		this.exp = 0;
		
		this.hp = 9;
		this.atk = 1;
		this.def = 1;
		this.spd = 1;
		this.spAtk = 1;
		this.spDef = 1;
		this.acc = 1;
		
		//Init stats. Loop starts at -4, because level 1 should be equivilent to 1 + (5 level ups)
		for(int i=-4;i<level;i++) {
			this.levelUpStatMod();
		}

	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescStr() {
		return String.format("Lv.%d - HP:%d/%d", this.level,this.hp,this.hp);
	}
	
	/*
	 * Calculates and applies the stat change portion of leveling up.
	 * This is seperate from the rest of level up, because when we initialize a pokemon to level 1,
	 * We actually want to do 1+(5 level ups), but still be at level 1
	 * 
	 * Returns the changes in stats
	 * */
	private int[] levelUpStatMod() {
		
		int[] points = new int[7];
		int sum = 0;
		while(sum<3) {
			sum=0;
			points[0] = rollStat(this.hpGrowth);
			points[1] = rollStat(this.atkGrowth);
			points[2] = rollStat(this.defGrowth);
			points[3] = rollStat(this.spdGrowth);
			points[4] = rollStat(this.spAtkGrowth);
			points[5] = rollStat(this.spDefGrowth);
			points[6] = rollStat(this.accGrowth);
			for(int i=0;i<7;i++)
				sum += points[i];
		}

		applyLevelupStatChange(points);
		return points;

	}
	
	private void applyLevelupStatChange(int[] stats) {
		this.hp += stats[0];
		this.atk += stats[1];
		this.def += stats[2];
		this.spd += stats[3];
		this.spAtk += stats[4];
		this.spDef += stats[5];
		this.acc += stats[6];
	}
	
	/*
	 * Rolls for the level up bonus to a stat.
	 * Stats are rolled fire emblem style, where a % over 100 guarentees 1 point, and rolls again for the modulus
	 */
	private int rollStat(int percentChance) {
		int val = (int)(Math.random()*100);
		int result = Math.floorDiv(percentChance, 100);
		int randComponent = percentChance%100;
		if(val<=randComponent)
			result++;
		
		//System.out.printf("Rolled: %d; Growth: %d\n",val,percentChance);
		return result;
	}
	
	public String toString() {
		return logWithPosition(name);
	}
	
	public int getMove() {
		return move;
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		// Only difference is a position offset because of an
		// Eccentricity in the sprite image files
		moveToTargetPos();
		batch.draw(texture, this.screenSpaceCoords.x-8,this.screenSpaceCoords.y,32,32);
	}
	
}
