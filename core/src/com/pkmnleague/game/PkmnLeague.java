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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;


public class PkmnLeague extends ApplicationAdapter implements InputProcessor {

	SpriteBatch batch;
	OrthographicCamera camera;
	//Level level;
	Stage stageFrame;
	Controller controller;
	Stack<BaseScreen> screens;
	Dataset pokemonDB;

	String[] maps = {
			"assets/maps/map1-2.tmx",
			"assets/maps/testmap1.tmx"
	};

	@Override
	public void create () {

		batch = new SpriteBatch();

		//Load map
		screens = new Stack<BaseScreen>();
		screens.push(new LevelScreen(maps[1]));
		controller = new Controller();

		//stageFrame = level.getStage();

		pokemonDB = Dataset.getDataset();
		Gdx.input.setInputProcessor(this);

	}

	@Override
	public void render () {
		Set<ControllerValues> inputs = controller.directionHandler();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		if(!screens.isEmpty()) {
			BaseScreen curScreen = screens.peek();
			curScreen.handleInput(inputs);
			curScreen.render(batch);
		}
		batch.end();
		
		//stageFrame = level.getStage();
		//if(stageFrame != null) {
		//	stageFrame.draw();
		//}
	}
	
	// Deleted a public void dispose()
	
	@Override
	public boolean keyDown(int keycode) {
		Set<ControllerValues> input = controller.keyDown(keycode);
		screens.peek().handleInput(input);  // Sends input commands to top of stack
		return false; 
	}
	
	@Override
	public boolean keyUp(int keycode) {
		controller.keyUp(keycode);
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
