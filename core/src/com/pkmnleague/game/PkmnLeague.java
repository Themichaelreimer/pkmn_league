package com.pkmnleague.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class PkmnLeague extends ApplicationAdapter implements InputProcessor {

	SpriteBatch batch;
	OrthographicCamera camera;
	Level level;
	
	public enum GameState{
		StartMenu,
		Story,
		Map
	}
	
	public enum MapState{
		PlayerTurn,
		EnemyTurn,
		NeutralTurn
	}
	
	public enum TurnState{
		ContextMenu,
		PartyView,
		Battle
	}
	// ArrayList of maps
	// (Master) ArrayList of pokemon
	// ArrayList of inventory
	

	@Override
	public void create () {
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		
		//Init Camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false,width,height);
		camera.update();
		batch = new SpriteBatch();

		//Load map
		level = new Level("assets/maps/map1-2.tmx",camera);

		Dataset pokemondb = Dataset.getDataset();
		Gdx.input.setInputProcessor(this);
		System.out.println("Checkpoint");
		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.begin();
		level.render(batch);
		batch.end();
	}
	
	// Deleted a public void dispose()
	
	@Override
	public boolean keyDown(int keycode) {
		level.keyDown(keycode);
		return false; 
	}
	
	@Override
	public boolean keyUp(int keycode) {
		
		level.keyUp(keycode);
		return false;
	}
	
    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
    @Override
    public boolean scrolled(int amount) {
        return false;
    }
	
}
