package com.pkmnleague.game;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.OrderedSet;
import com.badlogic.gdx.utils.viewport.Viewport;

// TODO:
// Pass level the list of pokemon being deployed to this level.
public class Level {
	
	private Tile[][] mapData; // Contains the move cost of each tile, whether it is water, grass, or breakable "solid"
	private MapObject[][] objects; // Object layer
	private TiledMap tiledMap;
	private TiledMapRenderer tiledMapRenderer;
	//private Cursor cursor;
	//private OrthographicCamera camera;
	
	// Every frame the camera will move towards Target(x,y) according to another function
	//private Vector3 targetCameraPos;
	
	private ArrayList<Pokemon> playerPokemon;
	private ArrayList<Pokemon> enemyPokemon;
	
	// Map dimensions in tiles
	private int width;
	private int height;
	private int screenWidthTiles, screenHeightTiles;
		
	// Cursor position in screen space tiles
	// The cursor position in world space is in the cursor object
	private int cursLocalX, cursLocalY = 0;
	private final int maxCursCamDist = 7;
	
	private int turnNumber = 0;
	
	//Cursor movement vars
	
	//UI Variables
	private Container<Table> UIContainer;
	private Skin pokemonPreviewSkin;
	private Table pokemonPreview;
	private Label pokemonNameLabel;
	private Label pokemonDescLabel;
	private Image pokemonPreviewIcon;
	
	private Stage stage;
	
	private Menu menu;
	private Battle battle;
	
	public Stage getStage() {
		return this.stage;
	}

	public GridPoint2 getDimensionsInTiles(){
		return new GridPoint2(this.width, this.height);
	}

	public Level(String path) {
		// By convention, we will make the base layer define the playable bounds
		
		stage =  new Stage();
		playerPokemon = new ArrayList<Pokemon>();
		enemyPokemon = new ArrayList<Pokemon>();
		
		tiledMap = new TmxMapLoader().load(Gdx.files.internal(path).file().getAbsolutePath());
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		
		TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		width = layer.getWidth();
		height = layer.getHeight();
		
		//////////////////////////////////////////////////////////////////////////////////////////
		//																						//
		//    UI Setup																			//
		//																						//
		//////////////////////////////////////////////////////////////////////////////////////////
		pokemonPreviewSkin = new Skin(Gdx.files.internal("uiskin.json"));
		pokemonNameLabel = new Label("TEST",pokemonPreviewSkin);
		pokemonDescLabel = new Label("TEST",pokemonPreviewSkin);
		
		pokemonPreview = new Table(pokemonPreviewSkin);
		pokemonPreview.top().right();
		pokemonPreview.setFillParent(true);
		Texture previewBG = new Texture(Gdx.files.internal("assets/sprites/LoyaltyBattleUI/battleMessage.png"));

		Table previewContainer = new Table();
		previewContainer.setBackground(new TextureRegionDrawable(previewBG));
		
		Table textPreviewComponent = new Table();
		pokemonPreviewIcon = new Image();

		textPreviewComponent.add(pokemonNameLabel);
		textPreviewComponent.row();
		textPreviewComponent.add(pokemonDescLabel);
		
		previewContainer.add(pokemonPreviewIcon);
		previewContainer.add(textPreviewComponent).pad(2.0f);
		
		pokemonPreview.add(previewContainer).pad(2.0f).prefSize(180f, 64f);
		stage.addActor(pokemonPreview);

		this.mapData = new Tile[height][width];
		this.objects = new MapObject[height][width];

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				this.mapData[y][x] = getTopTile(x,y);
				this.objects[y][x] = null;
			}
		}
		
		makePokemonAtPosition("charmander",2,20,6,playerPokemon);
		makePokemonAtPosition("pikachu",4,20,7,playerPokemon);
		makePokemonAtPosition("sandshrew",9,21,8,playerPokemon);
		
		makePokemonAtPosition("koffing",5,24,26,enemyPokemon);
		makePokemonAtPosition("weezing",12,23,27,enemyPokemon);
		makePokemonAtPosition("rattata",7,24,27,enemyPokemon);

		
	}
	
	/**
	 * Generates a new pokemon at a given level, at position (x,y) on the map.
	 * This includes the actual generation, adding the pokemon to a team, Updating the pokemon's copordinates both internally and on the object layer
	 * @param pokemon - Name of pokemon. If name doesn't match up, nothing will happen.
	 * @param level - Level of pokemon being generated
	 * @param x - x coord in map space
	 * @param y - y coord in map space
	 * @param team - Arraylist representing the team's pokemon
	 */
	public void makePokemonAtPosition(String pokemon, int level, int x, int y, ArrayList<Pokemon> team) {
		try {
			Pokemon pkmn = new Pokemon(pokemon,level);
			pkmn.setPos(x, y);
			objects[y][x] = pkmn;
			team.add(pkmn);
			
		} catch(Exception e) {
			System.out.printf("Unable to make pokemon (%s,%d) at (%d,%d)!\n",pokemon,level,x,y);
			e.printStackTrace();
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
	
	public void makeBattle(Pokemon attacker, Pokemon defender, boolean playerInit) {
		battle = new Battle(this,attacker,defender, playerInit);
	}

	// FIXME: Delete this if not needed in redesign, which I suspect
	public int[] calcMenuCoords() {
		// Add offsets to make them draw in the desired place
		int x = cursLocalX;
		int y = cursLocalY;
		
		if(x+4 <= screenWidthTiles) {
			x +=1;
		}else {
			x -=3;
		}
		if(y+6 <= screenHeightTiles) {
			y +=1;
		}else {
			y -=5;
		}
		
		int[] result = new int[2];
		result[0] = 16*(x+screenWidthTiles);
		result[1] = 16*(y+screenHeightTiles);
		return result;
	}

	/*
	public MapObject getCursorHoverObject() {
		int[] coords = cursor.getPos();
		int x = coords[0];
		int y = coords[1];
		return objects[y][x];
	}
	*/

	public MapObject getObjectAtPosition(GridPoint2 position){
		return this.getObjectAtPosition(position.x, position.y);
	}

	/**
	 * "safely" gets an object at (x,y) if it exists, without having to be
	 * worried about accessing an array out of index
	 * @param x - x coordinate on tile map
	 * @param y - y coordinate on tile map
	 * @return Object on tile (x,y) if it exists, otherwise null
	 */
	public MapObject getObjectAtPosition(int x, int y){
		if (0 <= x && x <this.width && 0<= y && y < this.height){
			return this.objects[y][x];
		}
		return null;
	}

	public Tile getTileAtPosition(GridPoint2 position){
		return mapData[position.y][position.x];
	}

	/* Returns a list of all mapobjects with a Manhatten distance in [1,distance]
	 * from point
	 */
	//TODO: Is this necessary, or do we add a flag to getAttackableTiles to ignore terrain?
	public ArrayList<MapObject> getAdjacentObjects(GridPoint2 point, int distance){

		if(distance == 0){
			ArrayList<MapObject> result = new ArrayList<>();
			result.add(this.getObjectAtPosition(point));
			return result;
		}

		ArrayList<MapObject> result = new ArrayList<>();
		ArrayList<GridPoint2> adjacents = new ArrayList<>();

		adjacents.add(point.cpy().add(1,0));
		adjacents.add(point.cpy().add(0,1));
		adjacents.add(point.cpy().add(-1,0));
		adjacents.add(point.cpy().add(0,-1));

		for(GridPoint2 adjacentPoint : adjacents){
			for(MapObject obj : getAdjacentObjects(adjacentPoint,distance-1)){
				if(obj!=null && !result.contains(obj)){
					result.add(obj);
				}
			}
		}

		return result;
	}

	public boolean pointInMap(int x, int y) {
		return -1<x && x<width && -1<y && y<height;
	}

	/**
	 * Moves a game object from (startX,startY) -> (endX, endY).
	 * Updates object's internal location data, and updates the object layer
	 * on map
	 *
	 * @param obj - object being moved
	 * @param startX - initial x
	 * @param startY - inital y
	 * @param endX - final x
	 * @param endY - final y
	 */
	public void moveMapObj(MapObject obj, int startX, int startY, int endX, int endY) {
		obj.setPos(endX, endY);
		objects[startY][startX] = null;
		objects[endY][endX] = obj;
	}
	
	public void render(Batch batch, OrthographicCamera camera, Cursor cursor) {

		if(objects[cursor.Y()][cursor.X()] == null) {
			pokemonPreview.setVisible(false);
		}else {
			updateUILabels(cursor);
			pokemonPreview.setVisible(true);
		}

		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		batch.setColor(0.3f, 0.6f, 1.0f, 1.0f);
		for (Pokemon pokemon : playerPokemon) pokemon.draw(batch, 1);
		//-------------------------------------------------
		batch.setColor(Color.WHITE);
		
		batch.setColor(1.0f, 0.4f, 0.4f, 1.0f);
		for (Pokemon pokemon : enemyPokemon) pokemon.draw(batch, 1);
		//-------------------------------------------------
		batch.setColor(Color.WHITE);


	}

	// FIXME - not implemented in redesign yet
	public void updateUILabels(Cursor cursor) {
		int[] pos = cursor.getPos();
		MapObject obj = objects[pos[1]][pos[0]];
		if(obj != null) {
			if(obj instanceof Pokemon) {
				Pokemon pkmn = (Pokemon)obj;
				Sprite spr = new Sprite(pkmn.portraitSpriteDef);
				spr.setSize(64.0f, 64.0f);
				pokemonPreviewIcon.setDrawable(new SpriteDrawable(spr));
				
				pokemonNameLabel.setText(pkmn.getName());
				pokemonDescLabel.setText(pkmn.getDescStr());
			}
		}
	}

	/**
	 * Given a set of tiles (which represents the tiles a pokemon can move to)
	 * Returns all tiles that can be attacked at a distance of 'distance'
	 *
	 * @param tiles: Set of moveable tiles
	 * @param distance: Maximum attackable distance
	 * @return set of tiles that can be attacked by a pokemon
	 */
	public OrderedSet<Tile> getAttackableTiles(OrderedSet<Tile> tiles, int distance){
		OrderedSet<Tile> result = new OrderedSet<>();

		for(Tile tile: tiles){
			OrderedSet<Tile> tileAttackable = getAttackableTilesFromSpace(tile.x,tile.y,distance);
			result.addAll(tileAttackable);
		}

		return result;
	}

	/**
	 * Given a tile-space coordinate (x,y) representing the destination
	 * tile after moving, and a distance d, this function returns the set
	 * of tiles that are attackable from (x,y)
	 *
	 * @param x x coordinate on tile map
	 * @param y y coordinate on tile map
	 * @param distance attacking distance
	 * @return set of tiles that can be attacked on (x,y)
	 */
	public OrderedSet<Tile> getAttackableTilesFromSpace(int x, int y, int distance){
		OrderedSet<Tile> result = new OrderedSet<Tile>();

		Tile thisTile = mapData[y][x];

		result.add(thisTile);

		if(distance == 0)
			return result;

		if(checkTileExistsAndPassable(x+1,y)){
			result.addAll(getAttackableTilesFromSpace(x+1,y,distance-1));
		}
		if(checkTileExistsAndPassable(x-1,y)){
			result.addAll(getAttackableTilesFromSpace(x-1,y,distance-1));
		}
		if(checkTileExistsAndPassable(x,y+1)){
			result.addAll(getAttackableTilesFromSpace(x,y+1,distance-1));
		}
		if(checkTileExistsAndPassable(x,y-1)){
			result.addAll(getAttackableTilesFromSpace(x,y-1,distance-1));
		}

		return result;
	}

	/**
	 * Given a set of tiles, returns all map objects in that tile set.
	 * Can be futher narrowed down by checking subclasses and properties
	 * to get pokemon to attack, to trade with, or items to pick up
	 *
	 * @param tiles - input tileset to check for objects in
	 * @return set of map objects inside tiles
	 */
	public OrderedSet<MapObject> getObjectsInTileSet(OrderedSet<Tile> tiles){
		OrderedSet<MapObject> results = new OrderedSet<>();
		for(Tile tile: tiles){
			MapObject obj = this.objects[tile.y][tile.x];
			if(obj != null)
				results.add(obj);
		}
		return results;
	}

	/**
	 * Returns whether it is possible *for all* pokemon to pass onto a space
	 * in theory. This is false iff the point is outside the map, or the tile
	 * is marked as completely unpassible in all situations (superSolid)
	 * @param x x coordinate of tile
	 * @param y y coordinate of tile
	 * @return boolean indicating whether to even consider this as a tile
	 */
	private boolean checkTileExistsAndPassable(int x, int y){
		return pointInMap(x,y) && !mapData[y][x].superSolid;
	}

	/**
	 *
	 * @param x x coordinate of pokemon in tiles
	 * @param y y coordinate of pokemon in tiles
	 * @param pokemon pokemon that may move
	 * @return List of tiles. TODO: Make this an ordered set for consistency?
	 */
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
			if(!list.contains(tileStruct.tile)) {
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


	/**
	 * Returns the camera position in tiles
	 * @return Camera Position in Tiles
	 */
	private static class MapSearchStruct{
		public Tile tile;
		public int moveRem;

		MapSearchStruct(Tile t, int move){
			this.tile = t;
			this.moveRem = move;
		}
	}

}

