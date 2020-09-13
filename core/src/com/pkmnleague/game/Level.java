package com.pkmnleague.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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
	
	// Map dimensions in tiles
	private int width;
	private int height;
		
	// Cursor position in screen space tiles
	// The cursor position in world space is in the cursor object
	private int camPosX, camPosY = 0;
	private int cursLocalX, cursLocalY = 0;
	
	private final int maxCursCamDist = 7;
	
	private int turnNumber = 0;
	
	/**
	 * Calculates the width and height of the viewport in tiles instead of pixels.
	 * @return [width,height]
	 */
	private int[] getDimensionsInTiles() {
		int[] res = new int[2];
		res[0] = (int) (camera.viewportWidth / 16);
		res[1] = (int) (camera.viewportHeight / 16);
		return res;
	}
	
	public Level(String path, OrthographicCamera cam) {
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
				this.mapData[y][x] = getTopTile(x,y);
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
	 * Note that this ignores tiles labelled foreground, which are always aesthetic only
	 * @param x - x coordinate on game board
	 * @param y - y coordinate on game board
	 * @return tile
	 */
	public Tile getTopTile(int x, int y) {
		Tile result = null;
		MapLayers layers = tiledMap.getLayers();
		for(int z=0;z<layers.getCount();z++) {
			TiledMapTileLayer.Cell cell = ((TiledMapTileLayer) layers.get(z)).getCell(x,y);
			if(cell != null) {
				Tile tile = new Tile(cell.getTile().getProperties(),x,y);
				if(!tile.foreground)
					result = tile;
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
					cursor.setSelectedObject(mapPkmn, getMoveableTiles(x,y,mapPkmn));
					log("SELECTED "+ mapPkmn.toString());
				}
			}
		}else {
			if(cursor.hasSelectedObject()) {
				// Nothing on map, but selected object
				// therefore, move to spot if possible
				
				MapObject cursMapObj = cursor.getSelectedObject();
				if(cursMapObj instanceof Pokemon) {
					Tile targetTile = mapData[y][x];
					Pokemon mapPkmn = (Pokemon)cursMapObj;
					
					if(cursor.getMoveableTiles().contains(targetTile)) {
						int[] oldPos = cursMapObj.getPosition();
						moveMapObj(cursMapObj,oldPos[0],oldPos[1],x,y);
						cursor.clearSelectedObject();
						log("PLACED "+ mapPkmn.toString());
					}
					
					//log("TOP TILE: Water(" +targetTile.water+"); Foreground("+targetTile.foreground+")" );
					
				}
			}else {
				//Nothing on map and nothing selected. Do nothing.
				
			}
		}
	}
	
	//TODO: Make pokemon the param and not pokemon move. So I can get types, AND move
	public ArrayList<Tile> getMoveableTiles(int x, int y, Pokemon pokemon){
		ArrayList<Tile> list = new ArrayList<Tile>();
		Queue<MapSearchStruct> tilesWithMove = new LinkedList<>();
		int pokemonMove = pokemon.getMove();
		tilesWithMove.add(new MapSearchStruct(mapData[y][x],pokemonMove));
		
		boolean canMoveOnWater = pokemon.hasType("water") || pokemon.hasType("flying");

		while(tilesWithMove.size() >0) {
			
			//TODO: On adding a tile to the queue, check if it already exists, with a better or equal move score
			
			//Move the current tile into the accept list ENFORCING UNIQUENESS
			MapSearchStruct tileStruct = tilesWithMove.remove();
			if(list.contains(tileStruct.tile) == false) {
				list.add(tileStruct.tile);	
			}

			// Properties of the tile "we're already on"
			int tileX = tileStruct.tile.x;
			int tileY = tileStruct.tile.y;
			int remMove = tileStruct.moveRem;
			int remMoveAfter = 0;
			
			if(remMove == 0) {
				continue;
			}
			
			
			if( pointInMap(tileX-1,tileY)) {
				//If we have non-negative move, put this tile into queue.
				Tile targetTile = mapData[tileY][tileX-1];
				
				if(targetTile.water && !canMoveOnWater)
					remMoveAfter = -1;
				else
					remMoveAfter = remMove - targetTile.moveCost;
				
				if(remMoveAfter >= 0 && !targetTile.solid) {
					tilesWithMove.add(new MapSearchStruct(targetTile,remMoveAfter));
				}
			}
			if( pointInMap(tileX+1,tileY)) {
				//If we have non-negative move, put this tile into queue.
				Tile targetTile = mapData[tileY][tileX+1];
				
				if(targetTile.water && !canMoveOnWater)
					remMoveAfter = -1;
				else
					remMoveAfter = remMove - targetTile.moveCost;
				
				if(remMoveAfter >= 0 && !targetTile.solid) {
					tilesWithMove.add(new MapSearchStruct(targetTile,remMoveAfter));
				}
			}
			if( pointInMap(tileX,tileY-1)) {
				//If we have non-negative move, put this tile into queue.
				Tile targetTile = mapData[tileY-1][tileX];
				
				if(targetTile.water && !canMoveOnWater)
					remMoveAfter = -1;
				else
					remMoveAfter = remMove - targetTile.moveCost;
				
				if(remMoveAfter >= 0 && !targetTile.solid) {
					tilesWithMove.add(new MapSearchStruct(targetTile,remMoveAfter));
				}
			}
			if( pointInMap(tileX,tileY+1)) {
				//If we have non-negative move, put this tile into queue.
				Tile targetTile = mapData[tileY+1][tileX];
				
				if(targetTile.water && !canMoveOnWater)
					remMoveAfter = -1;
				else
					remMoveAfter = remMove - targetTile.moveCost;
				
				if(remMoveAfter >= 0 && !targetTile.solid) {
					tilesWithMove.add(new MapSearchStruct(targetTile,remMoveAfter));
				}
			}
		}
		
		return list;
	}

	
	public boolean pointInMap(int x, int y) {
		return -1<x && x<width && -1<y && y<height;  
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
			//if(cursor.Y() < this.height-1)
				mapMove(0,1);
		}
		if(keycode == Input.Keys.DOWN) {
			//if(cursor.Y() > 0)
				mapMove(0,-1);
		}
		if(keycode == Input.Keys.RIGHT) {
			//if(cursor.X() < this.width-1)
				mapMove(1,0);
		}
		if(keycode == Input.Keys.LEFT) {
			//if(cursor.X() > 0)
				mapMove(-1,0);
		}
		if(keycode == Input.Keys.X) {
			mapClick();
		}
	}
	
	//Handles a cursor movement request on the map
	public void mapMove(int dx, int dy) {
		
		if(!(0 <= cursor.X() +dx && cursor.X() +dx < this.width-1))
			return;
		if(!(0 <= cursor.Y() +dy && cursor.Y() +dy < this.height-1))
			return;

		
		// "Global" cursor movement on the world map
		cursor.move(dx, dy);
		
		if(dx>0) { //MOVE RIGHT
			if(maxCursCamDist > cursLocalX) {
				// Cursor is inside the "inner" box - Move cursor local space
				cursLocalX += dx;
			}else {
				// Cursor is outside the "inner" box - Move camera instead
				camera.translate(16*dx,0);
			}
		}else { // MOVE LEFT
			
			if(-maxCursCamDist < cursLocalX) {
				// Cursor is inside the "inner" box - Move cursor local space
				cursLocalX += dx; // Note dx is negative
			}else {
				// Cursor is outside the "inner" box - Move camera instead
				camera.translate(16*dx,0);
			}
		}
		
		if(dy>0) { //MOVE UP
			if(maxCursCamDist > cursLocalY) {
				// Cursor is inside the "inner" box - Move cursor local space
				cursLocalY += dy;
			}else {
				// Cursor is outside the "inner" box - Move camera instead
				camera.translate(0,16*dy);
			}
		}else { // MOVE DOWN
			
			if(-maxCursCamDist < cursLocalY) {
				// Cursor is inside the "inner" box - Move cursor local space
				cursLocalY += dy; // Note dy is negative
			}else {
				// Cursor is outside the "inner" box - Move camera instead
				camera.translate(0,16*dy);
			}
		}

		
		
	}
	
	private class MapSearchStruct{
		public Tile tile;
		public int moveRem;
		
		MapSearchStruct(Tile t, int move){
			this.tile = t;
			this.moveRem = move;
		}
	}

}

