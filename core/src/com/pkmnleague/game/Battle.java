package com.pkmnleague.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Battle extends Actor{

	private Pokemon player, enemy;
	private boolean playerInit;
	private Level level;
	//private ShapeRenderer sr;
	private int ply;
	private BitmapFont font;
	
	private static Texture whiteBox = new Texture("assets/sprites/battle/fg_box1.png");
	private static Texture statusBoxR = new Texture("assets/sprites/battle/fg_player_status_box.png");
	private static Texture statusBoxL = new Texture("assets/sprites/battle/fg_enemy_status_box.png");
	
	private static Texture playerSideBG = new Texture("assets/sprites/battle/bg_player_grass.png");
	private static Texture enemySideBG = new Texture("assets/sprites/battle/bg_enemy_grass.png");
	
	
	public Battle(Level level,Pokemon player, Pokemon enemy, boolean playerInitiated) {
		//sr = new ShapeRenderer();
		this.level = level;
		this.player = player;
		this.enemy = enemy;
		this.playerInit = playerInitiated;
		this.font = new BitmapFont();
		font.setColor(0.0f, 0.0f, 0.0f, 1.0f);
		ply = 0;
	}
	
	public int calcDamage(Pokemon atk, Pokemon def) {
		// TODO: This will eventually look at the equipped move
		// And can determine physical or special
		int res = atk.getAttack() - def.getDefence();
		if (res<0)
			res = 0;
		return res;
	}
	
	public int calcAttacks(Pokemon atk, Pokemon def) {
		
		if(atk.getName().equalsIgnoreCase("pikachu"))
			return 2;
		return 1;
	}
	
	public String[] statisticsStrings(Pokemon me, Pokemon other) {
		String[] strs = new String[4];
		strs[0] = String.format("DMG: %d", calcDamage(me,other));
		strs[1] = "HIT:    90";
		strs[2] = "CRT:   5";
		strs[3] =  String.format("ATKS: %d", calcAttacks(me,other));
		return strs;
	}
	
	public void draw(Batch batch, float alpha) {
		
		String[] playerStrs = this.player.battleDescStrs();
		String[] enemyStrs = this.enemy.battleDescStrs();
		String[] playerStats = statisticsStrings(player,enemy);
		String[] enemyStats = statisticsStrings(enemy,player);
		
		String playerLine1 = String.format("%s - Lv. %s", playerStrs[0],playerStrs[1]);
		String playerLine2 = String.format("HP: %s/%s",playerStrs[2],playerStrs[3]); // TODO, replace this with bar( Or both?)
		String playerLine3 = "Tackle";
		
		String enemyLine1 = String.format("%s - Lv. %s", enemyStrs[0],enemyStrs[1]);
		String enemyLine2 = String.format("HP: %s/%s",enemyStrs[2],enemyStrs[3]); // TODO, replace this with bar( Or both?)
		String enemyLine3 = "Tackle";
		
		float[] viewport = level.getViewPort();
		float width = viewport[0];
		float height = viewport[1];
		
		int[] camPos = level.getCamPos(false);
		
		int frameX = (int)(camPos[0] - 0.375*width);
		int frameY =  (int)(camPos[1] - 0.40*height);
		int frameWidth =  (int)(0.75*width);
		int frameHeight =  (int)(0.85*height);
		int pokemonSize = frameWidth/4;
		
		int statBoxHeight = frameHeight/4;
		
		int playerPokemonX = frameX + pokemonSize;
		int playerPokemonY = frameY + statBoxHeight + 16;
		
		int enemyPokemonX = (int)(frameX+(frameWidth/2) + (0.6*pokemonSize));
		int enemyPokemonY = (int)(frameY + 0.60*frameHeight);
		
		int playerBoxX = frameX+(frameWidth/2)-10;
		int playerBoxY = playerPokemonY+16;
		
		int enemyBoxX = frameX-10;
		int enemyBoxY = enemyPokemonY+16;
		
		int statusBoxHeight = frameHeight/5;
				
		// Frame
		batch.draw(whiteBox,frameX, frameY, frameWidth, frameHeight);
		
		// Backgrounds
		batch.draw(playerSideBG,(int)(playerPokemonX-0.905*pokemonSize),playerPokemonY,2*pokemonSize,pokemonSize/2);
		batch.draw(enemySideBG,(int)(enemyPokemonX-0.25*pokemonSize),(int)(enemyPokemonY-0.25*pokemonSize),(int)(1.5*pokemonSize),pokemonSize);

		
		// Stat Boxes
		batch.draw(whiteBox,frameX+8,frameY+16,frameWidth/2-8,statBoxHeight);
		batch.draw(whiteBox,frameX+(frameWidth/2),frameY+16,frameWidth/2-6 ,statBoxHeight);
		
		// Pokemon
		batch.draw(player.portraitSpriteAtk,playerPokemonX-pokemonSize,playerPokemonY-pokemonSize/2,2*pokemonSize,2*pokemonSize);
		batch.draw(enemy.portraitSpriteDef,enemyPokemonX,enemyPokemonY,pokemonSize,pokemonSize);
		
		// Status Boxes
		batch.draw(statusBoxR,frameX+(frameWidth/2)-10,playerPokemonY+16,frameWidth/2,statusBoxHeight);
		batch.draw(statusBoxL,frameX+16,enemyPokemonY+32,frameWidth/2,statusBoxHeight);
		
		// Text
		
		// Player status
		font.draw(batch, playerLine1, playerBoxX + 32, playerBoxY + statusBoxHeight - 16);
		font.draw(batch, playerLine2, playerBoxX + 32, playerBoxY + statusBoxHeight - 32);
		font.draw(batch, playerLine3, playerBoxX + 32, playerBoxY + statusBoxHeight - 48);
		
		// Enemy status
		font.draw(batch, enemyLine1, enemyBoxX + 40, enemyBoxY + statusBoxHeight);
		font.draw(batch, enemyLine2, enemyBoxX + 40, enemyBoxY + statusBoxHeight - 16);
		font.draw(batch, enemyLine3, enemyBoxX + 40, enemyBoxY + statusBoxHeight - 32);
		
		// Player statistics box
		font.draw(batch, playerStats[0], frameX+32, frameY+statBoxHeight-20);
		font.draw(batch, playerStats[1], frameX+32, frameY+statBoxHeight-40);
		font.draw(batch, playerStats[2], frameX+128, frameY+statBoxHeight-40);
		font.draw(batch, playerStats[3], frameX+128, frameY+statBoxHeight-20);
		
		// Enemy statistics box
		font.draw(batch, enemyStats[0], frameX+(frameWidth/2)+32, frameY+statBoxHeight-20);
		font.draw(batch, enemyStats[1], frameX+(frameWidth/2)+32, frameY+statBoxHeight-40);
		font.draw(batch, enemyStats[2], frameX+(frameWidth/2)+128, frameY+statBoxHeight-40);
		font.draw(batch, enemyStats[3], frameX+(frameWidth/2)+128, frameY+statBoxHeight-20);

	}
	
	public boolean canDouble(Pokemon attacker, Pokemon defender) {
		return false;
	}
	
	private class PlayerAnimation{
		
		Texture sprite;
		int originX, originY;
		float t;
		float duration;
		boolean isDone;
		
		PlayerAnimation(Texture sprite, float duration, int originX, int originY){
			this.sprite = sprite;
			this.originX = originX;
			this.originY = originY;
			this.t = 0.0f;
			this.duration = duration;
			this.isDone = false;
			
		}
		
		void draw() {
			float dt = Gdx.graphics.getDeltaTime();
			
			if(!isDone) {
				this.t = t + dt;
				if(this.t > this.duration)
					isDone = true;
			}
				
		}
		
	}
	
	private class EnemyAnimation{
		
		Texture sprite;
		int originX, originY;
		float t;
		float duration;
		boolean isDone;
		
		EnemyAnimation(Texture sprite, float duration, int originX, int originY){
			this.sprite = sprite;
			this.originX = originX;
			this.originY = originY;
			this.t = 0.0f;
			this.duration = duration;
			this.isDone = false;
			
		}
		
		void draw() {
			float dt = Gdx.graphics.getDeltaTime();
			
			if(!isDone) {
				this.t = t + dt;
				if(this.t > this.duration)
					isDone = true;
			}
				
		}
		
	}
		
	private class TextAnimation{
		
		String string;
		int originX, originY;
		float t;
		float duration;
		boolean isDone;
		
		TextAnimation(String str, float duration, int originX, int originY){
			this.string = str;
			this.originX = originX;
			this.originY = originY;
			this.t = 0.0f;
			this.duration = 0.0f;
			this.isDone = false;
			
		}
		
		void draw() {
			float dt = Gdx.graphics.getDeltaTime();
			
			if(!isDone) {
				this.t = t + dt;
				if(this.t > this.duration)
					isDone = true;
			}
		}
		
		
	}
	
}
