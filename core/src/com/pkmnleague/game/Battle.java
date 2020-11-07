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
	private PlayerAnimation playerAnim;
	private EnemyAnimation enemyAnim;
	private boolean playerInit;
	private Level level;
	//private ShapeRenderer sr;
	private int ply;
	private BATTLE_STATE state;
	
	private BitmapFont font;
	
	private static Texture whiteBox = new Texture("assets/sprites/battle/fg_box1.png");
	private static Texture statusBoxR = new Texture("assets/sprites/battle/fg_player_status_box.png");
	private static Texture statusBoxL = new Texture("assets/sprites/battle/fg_enemy_status_box.png");
	
	private static Texture playerSideBG = new Texture("assets/sprites/battle/bg_player_grass.png");
	private static Texture enemySideBG = new Texture("assets/sprites/battle/bg_enemy_grass.png");
	
	private static Texture greenbar = new Texture("assets/sprites/battle/hp-bar-green.png");
	private static Texture yellowbar = new Texture("assets/sprites/battle/hp-bar-yellow.png");
	private static Texture redbar = new Texture("assets/sprites/battle/hp-bar-red.png");

	
	enum BATTLE_STATE{
		INIT,
		PLAYER_ATTACK,
		ENEMY_HIT,
		ENEMY_ATTACK,
		PLAYER_HIT,
		RESOLUTION
	}
	
	
	public Battle(Level level,Pokemon player, Pokemon enemy, boolean playerInitiated) {
		//sr = new ShapeRenderer();
		this.level = level;
		this.player = player;
		this.enemy = enemy;
		this.playerInit = playerInitiated;
		this.font = new BitmapFont();
		font.setColor(0.0f, 0.0f, 0.0f, 1.0f);
		ply = 0;
		state=BATTLE_STATE.INIT;
		
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
	
	public void handleBattleState() {
		
		// Idea: Trigger animations at state transitions
		// If no animations are playing, handle next transitino
		if(state == BATTLE_STATE.INIT) {
			if(this.playerInit) {
				state = BATTLE_STATE.PLAYER_ATTACK;
				playerAnim.playAttackAnimation();
			}
		}

		if(state == BATTLE_STATE.PLAYER_ATTACK) {
			if(!playerAnim.playingAttackAnimation) {
				state = BATTLE_STATE.ENEMY_HIT;
				enemyAnim.playHitAnimation();
			}
		}else if (state == BATTLE_STATE.ENEMY_HIT) {
			if(!enemyAnim.playingHitAnimation) {
				state = BATTLE_STATE.ENEMY_ATTACK;
				enemyAnim.playAttackAnimation();
			}
		}else if (state == BATTLE_STATE.ENEMY_ATTACK) {
			if(!enemyAnim.playingAttackAnimation) {
				state = BATTLE_STATE.PLAYER_HIT;
			}
		}
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
		
		int realPlayerX = playerPokemonX-pokemonSize;
		int realPlayerY = playerPokemonY-pokemonSize/2;
		int playerSize = 2*pokemonSize;
		
		int realEnemyX = enemyPokemonX;
		int realEnemyY = enemyPokemonY;
		int enemySize = pokemonSize;
		
		if(playerAnim == null) {
			playerAnim = new PlayerAnimation(player.portraitSpriteAtk,realPlayerX,realPlayerY,playerSize,playerSize);
			playerAnim.playAttackAnimation();
		}
			
		if(enemyAnim == null)
			enemyAnim = new EnemyAnimation(enemy.portraitSpriteDef, realEnemyX, realEnemyY, enemySize,enemySize);
		
		handleBattleState(); // This has to happen here so we know the anims are not null
				
		// Frame
		batch.draw(whiteBox,frameX, frameY, frameWidth, frameHeight);
		
		// Backgrounds
		batch.draw(playerSideBG,(int)(playerPokemonX-0.905*pokemonSize),playerPokemonY,2*pokemonSize,pokemonSize/2);
		batch.draw(enemySideBG,(int)(enemyPokemonX-0.25*pokemonSize),(int)(enemyPokemonY-0.25*pokemonSize),(int)(1.5*pokemonSize),pokemonSize);

		
		// Stat Boxes
		batch.draw(whiteBox,frameX+8,frameY+16,frameWidth/2-8,statBoxHeight);
		batch.draw(whiteBox,frameX+(frameWidth/2),frameY+16,frameWidth/2-6 ,statBoxHeight);
		
		// Pokemon
		//batch.draw(player.portraitSpriteAtk,playerPokemonX-pokemonSize,playerPokemonY-pokemonSize/2,2*pokemonSize,2*pokemonSize);
		//batch.draw(enemy.portraitSpriteDef,enemyPokemonX,enemyPokemonY,pokemonSize,pokemonSize);
		playerAnim.draw(batch);
		enemyAnim.draw(batch);
		
		
		// Status Boxes
		batch.draw(statusBoxR,frameX+(frameWidth/2)-10,playerPokemonY+16,frameWidth/2,statusBoxHeight);
		batch.draw(statusBoxL,frameX+16,enemyPokemonY+32,frameWidth/2,statusBoxHeight);
				
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

		// HP Bars
		drawHpBar(batch, player.totalHP(), player.currentHP(), playerBoxX + 96, playerBoxY + statusBoxHeight - 44 );
		drawHpBar(batch, enemy.totalHP(), enemy.currentHP(), enemyBoxX + 110, enemyBoxY + statusBoxHeight - 26);
		
	}
	
	public boolean canDouble(Pokemon attacker, Pokemon defender) {
		return false;
	}
	
	private void drawHpBar(Batch batch, int maxhp, int curhp, int x, int y) {
		
		int height = 16;
		int totalBarLength = 128;
		float hpPercent = curhp/maxhp;
		
		Texture tex;
		
		if(hpPercent > 0.5)
			tex = greenbar;
		else if(hpPercent > 0.25)
			tex = yellowbar;
		else
			tex = redbar;
		
		batch.draw(tex,x,y,totalBarLength*hpPercent, height);
		
	}
	
	private class PlayerAnimation{
		
		Texture sprite;
		int originX, originY;
		int sizeX, sizeY;
		float t;
		float duration = 1.0f;
		boolean isDone;
		boolean playingAttackAnimation;
		boolean playingHitAnimation;
		
		PlayerAnimation(Texture sprite, int originX, int originY, int sizeX, int sizeY){
			this.sprite = sprite;
			this.originX = originX;
			this.originY = originY;
			this.sizeX = sizeX;
			this.sizeY = sizeY;
			this.t = 0.0f;
			this.isDone = true;
		}
		
		boolean isDone() {
			return this.isDone;
		}
		
		void drawAttackingAnimation(Batch batch) {
			//System.out.printf("Attacking: t=%f\n",t);
			batch.draw(this.sprite,originX+calcOffset(t),originY,sizeX,sizeY);
		}
		
		void drawHitAnimation(Batch batch) {
			//System.out.printf("Hit: t=%f\n",t);
			if(inRange(t,0.0f,0.25f) || inRange(t,0.5f,0.75f)) {
				batch.draw(this.sprite,originX,originY,sizeX,sizeY);
			}
		}
		
		void draw(Batch batch) {
			
			if(!isDone) {
				float dt = Gdx.graphics.getDeltaTime();
				this.t += dt;
				
				System.out.printf("dt: %f t=%f\n",dt,t);

				
				if(playingAttackAnimation)
					drawAttackingAnimation(batch);
				else if(playingHitAnimation)
					drawHitAnimation(batch);
				
				if(this.t > this.duration) {
					isDone = true;
					playingAttackAnimation = false;
					playingHitAnimation = false;
				}
				
			} else {
				batch.draw(this.sprite,originX,originY,sizeX,sizeY);
			}
				
		}
		private boolean inRange(float x, float low, float high) {
			return (low <= x && x <= high);
		}
		
		private float calcOffset(float t) {
			if(t<0.3f)
				return 0.0f;
			else if(this.t < this.duration)
				return 4*16*t;
			return 0.0f;
		}
		
		void playAttackAnimation() {
			this.t = 0.0f;
			this.isDone = false;
			this.playingAttackAnimation = true;
		}
		
		void playHitAnimation() {
			this.t = 0.0f;
			this.isDone = false;
			this.playingHitAnimation = true;
		}
		
	}
	
private class EnemyAnimation{
		
		Texture sprite;
		int originX, originY;
		int sizeX, sizeY;
		float t;
		float duration = 1.0f;
		boolean isDone;
		boolean playingAttackAnimation;
		boolean playingHitAnimation;
		
		EnemyAnimation(Texture sprite, int originX, int originY, int sizeX, int sizeY){
			this.sprite = sprite;
			this.originX = originX;
			this.originY = originY;
			this.sizeX = sizeX;
			this.sizeY = sizeY;
			this.t = 0.0f;
			this.isDone = true;
			
		}
		
		boolean isDone() {
			return this.isDone;
		}
		
		void drawAttackingAnimation(Batch batch) {
			System.out.printf("Attacking: t=%f\n",t);
			batch.draw(this.sprite,originX-calcOffset(t),originY,sizeX,sizeY);
		}
		
		void drawHitAnimation(Batch batch) {
			System.out.printf("Hit: t=%f\n",t);
			if(inRange(t,0.0f,0.25f) || inRange(t,0.5f,0.75f)) {
				batch.draw(this.sprite,originX,originY,sizeX,sizeY);
			}
		}
		
		void draw(Batch batch) {
			
			if(!isDone) {
				System.out.printf("ANIMATION\n");
				float dt = Gdx.graphics.getDeltaTime();
				this.t += dt;
				
				if(playingAttackAnimation)
					drawAttackingAnimation(batch);
				else if(playingHitAnimation)
					drawHitAnimation(batch);
				
				if(this.t > this.duration) {
					isDone = true;
					playingAttackAnimation = false;
					playingHitAnimation = false;
				}
				
			} else {
				System.out.printf("STATIC\n");
				batch.draw(this.sprite,originX,originY,sizeX,sizeY);
			}
				
		}
		private boolean inRange(float x, float low, float high) {
			return (low <= x && x <= high);
		}
		
		private float calcOffset(float t) {
			if(t<0.3f)
				return 0.0f;
			else if(this.t < this.duration)
				return 4*16*t;
			return 0.0f;
		}
		
		void playAttackAnimation() {
			this.t = 0.0f;
			this.isDone = false;
			this.playingAttackAnimation = true;
		}
		
		void playHitAnimation() {
			this.t = 0.0f;
			this.isDone = false;
			this.playingHitAnimation = true;
		}
		
	}
	
}
