package com.pkmnleague.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

// TODO:
// Pass level the list of pokemon being deployed to this level.
public class Level {
	
	private Tile[][] mapData; // Contains the move cost of each tile, whether it is water, grass, or breakable "solid"
	private MapObject[][] objects; // Object layer
	private TiledMap tiledMap;
	private TiledMapRenderer tiledMapRenderer;
	private Cursor cursor;
	private OrthographicCamera camera;
	
	private ArrayList<Pokemon> playerPokemon;
	private ArrayList<Pokemon> enemyPokemon;
	
	private int width;
	private int height;
	
	private int turnNumber = 0;
	
	public Level(String path,OrthographicCamera cam) {
		// By convention, we will make the base layer define the playable bounds
		
		playerPokemon = new ArrayList<Pokemon>();
		enemyPokemon = new ArrayList<Pokemon>();

		cursor = new Cursor(15,26);
		
		tiledMap = new TmxMapLoader().load(Gdx.files.internal(path).file().getAbsolutePath());
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		camera = cam;
		
		TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		width = layer.getWidth();
		height = layer.getHeight();
		
		this.mapData = new Tile[height][width];
		this.objects = new MapObject[height][width];
		
		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				this.mapData[y][x] = new Tile(getTopTile(x,y).getTile().getProperties());
				this.objects[y][x] = null;
			}
		}
		
		try {
			
			Pokemon charmander = new Pokemon("charmander",1);
			Pokemon pikachu = new Pokemon("pikachu",4);
			Pokemon sandshrew = new Pokemon("sandshrew",9);
			
			charmander.setPos(20,20);
			pikachu.setPos(20,21);
			sandshrew.setPos(21,21);
			
			playerPokemon.add(pikachu);
			playerPokemon.add(charmander);
			playerPokemon.add(sandshrew);
			
			objects[20][20] = charmander;
			objects[21][20] = pikachu;
			objects[21][21] = sandshrew;
			
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		
	}
	
	/**
	 * Returns the tile on the highest layer. This is the one least obscured when drawing, and the one we want to consult for properties.
	 * @param x - x coordinate on game board
	 * @param y - y coordinate on game board
	 * @return cell
	 */
	public TiledMapTileLayer.Cell getTopTile(int x, int y) {
		TiledMapTileLayer.Cell result = null;
		MapLayers layers = tiledMap.getLayers();
		for(int z=0;z<layers.getCount();z++) {
			TiledMapTileLayer.Cell cell = ((TiledMapTileLayer) layers.get(z)).getCell(x,y);
			if(cell != null) {
				result = cell;
			}
		}
		return result;
	}
	
	public int getTurnCount() {
		return turnNumber;
	}
	
	public TiledMap getTiledMap() {
		return tiledMap;
	}
	
	public TiledMapRenderer getRenderer() {
		return tiledMapRenderer;
	}
	
	public void onTurnStart() {
		turnNumber++;
	}
	
	public void log(String str) {
		System.out.println(str);
	}
	
	public void mapClick() {
		int[] coords = cursor.getPos();
		int x = coords[0];
		int y = coords[1];
		MapObject mapObj = objects[y][x];
		if(mapObj != null) {
			if (cursor.hasSelectedObject()) {
				// Pokemon Battle, maybe --- TODO: Tons
			} else {
				// Set selected object
				
				//Only a pokemon can be a slot 1 selected object
				if(mapObj instanceof Pokemon) {
					Pokemon mapPkmn = (Pokemon)mapObj;
					cursor.setSelectedObject(mapPkmn);
					log("SELECTED "+ mapPkmn.toString());
				}
				
				
			}
		}else {
			if(cursor.hasSelectedObject()) {
				// Nothing on map, but selected object
				// therefore, move to spot if possible
				MapObject cursMapObj = cursor.getSelectedObject();
				int[] oldPos = cursMapObj.getPosition();
				moveMapObj(cursMapObj,oldPos[0],oldPos[1],x,y);
				cursor.clearSelectedObject();
				
				if(cursMapObj instanceof Pokemon) {
					Pokemon mapPkmn = (Pokemon)cursMapObj;
					log("PLACED "+ mapPkmn.toString());
				}
			}else {
				//Nothing on map and nothing selected. Do nothing.
				
			}
		}
	}
	
	public void moveMapObj(MapObject obj, int startX, int startY, int endX, int endY) {
		obj.setPos(endX, endY);
		objects[startY][startX] = null;
		objects[endY][endX] = obj;
	}
	
	public void render(Batch batch) {
		camera.update();
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		
		// Draw player's pokemon on board  IN BLUE-----------------
		//Color pre = batch.getColor();
		//batch.setColor(0.7f, 0.7f, 1.0f, 1.0f);
		for(int i=0;i<playerPokemon.size();i++)
			playerPokemon.get(i).draw(batch,1);
		//batch.setColor(pre.r, pre.g, pre.b, 1f);
		//-------------------------------------------------
		cursor.draw(batch, 0.3f);
	}
	
	// TODO: Make handler functions depending on map state
	public void keyDown(int keycode) {
		if(keycode == Input.Keys.UP) {
			if(cursor.Y() < this.height-1)
				cursor.move(0,1);
		}
		if(keycode == Input.Keys.DOWN) {
			if(cursor.Y() > 0)
				cursor.move(0,-1);
		}
		if(keycode == Input.Keys.RIGHT) {
			if(cursor.X() < this.width-1)
				cursor.move(1, 0);
		}
		if(keycode == Input.Keys.LEFT) {
			if(cursor.X() > 0)
				cursor.move(-1,0);
		}
		if(keycode == Input.Keys.X) {
			mapClick();
		}
	}
}
