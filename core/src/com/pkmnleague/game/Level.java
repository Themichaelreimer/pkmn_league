package com.pkmnleague.game;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

	/*
	public float[] getViewPort() {
		float[] res = new float[2];
		res[0] = camera.viewportWidth;
		res[1] = camera.viewportHeight;
		return res;
				
	}

	private int[] getDimensionsInTiles() {
		int[] res = new int[2];
		res[0] = (int) (camera.viewportWidth / 16);
		res[1] = (int) (camera.viewportHeight / 16);
		return res;
	}
	*/

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
		//pokemonPreview.setBackground(new TextureRegionDrawable(previewBG));
		
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
		
		makePokemonAtPosition("charmander",2,20,20,playerPokemon);
		makePokemonAtPosition("pikachu",4,20,21,playerPokemon);
		makePokemonAtPosition("sandshrew",9,21,21,playerPokemon);
		
		makePokemonAtPosition("koffing",5,24,24,enemyPokemon);
		makePokemonAtPosition("weezing",12,23,23,enemyPokemon);
		makePokemonAtPosition("rattata",7,23,24,enemyPokemon);

		
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


	public boolean pointInMap(int x, int y) {
		return -1<x && x<width && -1<y && y<height;
	}
	
	public void moveMapObj(MapObject obj, int startX, int startY, int endX, int endY) {
		obj.setPos(endX, endY);
		objects[startY][startX] = null;
		objects[endY][endX] = obj;
	}
	
	public void render(Batch batch) {

		/*
		moveCameraToTargetPos();
		//inputHandler();

		if(objects[cursor.Y()][cursor.X()] == null || battle != null) {
			pokemonPreview.setVisible(false);
		}else {
			pokemonPreview.setVisible(true);
		}
		
		camera.update();
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		*/
		
		
		batch.setColor(0.3f, 0.6f, 1.0f, 1.0f);
		for (Pokemon pokemon : playerPokemon) pokemon.draw(batch, 1);
		//-------------------------------------------------
		batch.setColor(Color.WHITE);
		
		batch.setColor(1.0f, 0.4f, 0.4f, 1.0f);
		for (Pokemon pokemon : enemyPokemon) pokemon.draw(batch, 1);
		//-------------------------------------------------
		batch.setColor(Color.WHITE);
		
		//Draw enemy team in red
		//cursor.draw(batch, 0.3f);

	}

	/*
	public void moveToNextFreePokemon() {
		MapObject obj = getCursorHoverObject();
		boolean getNext=false;
		// Search for first unmoved pokemon
		if(obj != null && playerPokemon.contains(obj)) {
			int iPokemon = playerPokemon.indexOf(obj);
			iPokemon = (iPokemon+1)%playerPokemon.size();
			Pokemon pokemon = playerPokemon.get(iPokemon);
			while(pokemon.hasMoved()) {
				iPokemon = (iPokemon+1)%playerPokemon.size();
				pokemon = playerPokemon.get(iPokemon);
			}
			//Move cursor
			int[] coords = pokemon.coords;
			cursor.setPos(coords[0], coords[1]);
			
		}else{
			for (Pokemon pokemon : playerPokemon) {
				if (!pokemon.hasMoved()) {
					//Move cursor
					int[] coords = pokemon.coords;
					cursor.setPos(coords[0], coords[1]);
				}
			}
		}
	}
	*/

	
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

