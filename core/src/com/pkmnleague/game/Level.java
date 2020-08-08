package com.pkmnleague.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
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
	
	private ArrayList<Pokemon> playerPokemon;
	private ArrayList<Pokemon> enemyPokemon;
	
	private int width;
	private int height;
	private int cursorX, cursorY;
	
	private int turnNumber = 0;
	
	public Level(String path) {
		// By convention, we will make the base layer define the playable bounds
		
		playerPokemon = new ArrayList<Pokemon>();
		enemyPokemon = new ArrayList<Pokemon>();

		cursorX = 15;
		cursorY = 26;
		cursor = new Cursor(cursorX,cursorY);
		
		tiledMap = new TmxMapLoader().load(Gdx.files.internal(path).file().getAbsolutePath());
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		
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
			objects[20][21] = pikachu;
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
	
	public void render(Batch batch) {
		tiledMapRenderer.render();
		for(int i=0;i<playerPokemon.size();i++)
			playerPokemon.get(i).draw(batch,1);
		cursor.draw(batch, 0.3f);
	}
}
