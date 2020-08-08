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

	TiledMap tiledMap;
	OrthographicCamera camera;
	TiledMapRenderer tiledMapRenderer;
	SpriteBatch batch;
	
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
		batch = new SpriteBatch();
		camera.setToOrtho(false,width,height);
		camera.update();

		//Load map
		level = new Level("assets/maps/map1-2.tmx");
		tiledMap = level.getTiledMap();
		tiledMapRenderer = level.getRenderer();
		tiledMapRenderer.setView(camera);
		
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
		tiledMapRenderer.setView(camera);
		batch.begin();
		level.render(batch);
		batch.end();
	}
	
	// Deleted a public void dispose()
	
	@Override
	public boolean keyDown(int keycode) {
		return false; 
	}
	
	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Input.Keys.LEFT) {
			camera.translate(-16,0);
		}
		if(keycode == Input.Keys.RIGHT) {
			camera.translate(16,0);
		}
		if(keycode == Input.Keys.UP) {
			camera.translate(0,16);
		}
		if(keycode == Input.Keys.DOWN) {
			camera.translate(0,-16);
		}
		if(keycode == Input.Keys.NUM_0) {
			tiledMap.getLayers().get(0).setVisible(!tiledMap.getLayers().get(0).isVisible());
		}
		if(keycode == Input.Keys.NUM_1) {
			tiledMap.getLayers().get(1).setVisible(!tiledMap.getLayers().get(1).isVisible());
		}
		if(keycode == Input.Keys.NUM_2) {
			tiledMap.getLayers().get(2).setVisible(!tiledMap.getLayers().get(2).isVisible());
		}
		if(keycode == Input.Keys.NUM_3) {
			tiledMap.getLayers().get(3).setVisible(!tiledMap.getLayers().get(3).isVisible());
		}
		
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
