package com.pkmnleague.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.OrderedSet;

import java.util.ArrayList;
import java.util.Set;

public class LevelScreen implements BaseScreen{

    private final Level level;
    private final Cursor cursor;

    private final MapCamera camera;
    private GridPoint2 screenTileDimensions;
    private final GridPoint2 levelTileDimensions;

    private final int maxCursCamDist = 7;
    private ActionMenuScreen menu;

    // TODO: Add player party once persistence is added
    public LevelScreen(String path){

        //Load Map
        this.level = loadMap(path);
        this.levelTileDimensions = this.level.getDimensionsInTiles();

        //Init Camera
        this.camera = new MapCamera();
        this.screenTileDimensions = this.camera.getScreenTileDimensions();

        //Set cursor and location
        int initialX = this.levelTileDimensions.x/2;
        int initialY = this.levelTileDimensions.y/2;

        this.cursor = new Cursor(initialX, initialY);
        this.placeCameraInLevel();
        this.menu = null;

    }

    public void clearMenu(){
        this.menu = null;
    }

    public void makeActionMenu(Pokemon p){
        this.menu = new ActionMenuScreen(this, p);
    }

    @Override
    public void render(Batch batch) {
        camera.update();
        this.level.render(batch, this.camera.getOrthoCam(), cursor);
        this.cursor.draw(batch, 1.0f);
        if(this.menu != null){
            this.menu.render(batch);
        }
    }

    private Level loadMap(String path){
        return new Level(path);
    }

    private void placeCameraInLevel(){

        int screenWidthTiles = this.camera.getScreenTileDimensions().x;
        int screenHeightTiles = this.camera.getScreenTileDimensions().y;

        int camX = screenWidthTiles/2;
        int camY = screenHeightTiles/2;

        //Place camera
        Vector2 targetCameraPos = new Vector2(16f*camX, 16f*camY);
        this.camera.moveCameraToPosition(targetCameraPos);

    }

    public void handleInput(Set<ControllerValues> input){

        if (this.menu != null){
            this.menu.handleInput(input);
            return;
        }

        if (input.contains(ControllerValues.UP))
            up();
        if (input.contains(ControllerValues.DOWN))
            down();
        if (input.contains(ControllerValues.LEFT))
            left();
        if (input.contains(ControllerValues.RIGHT))
            right();
        if (input.contains(ControllerValues.A))
            A();
        if (input.contains(ControllerValues.B))
            B();

    }

    public void A(){
        this.mapClick();
    }

    public void B(){

    }

    public void up() {
        this.mapMove(0,1);
    }

    public void down() {
        this.mapMove(0,-1);
    }

    public void right() {
        this.mapMove(1,0);
    }

    public void left() {
        this.mapMove(-1,0);
    }


    /**
     * Responds to a *request* to change cursor position. If we can't
     * move the cursor, the request will be ignored
     *
     * @param dx - requested change in x axis
     * @param dy - requested change in y axis
     **/
    public void mapMove(int dx, int dy) {

        int width = this.screenTileDimensions.x;
        int height = this.screenTileDimensions.y;

        // Cursor cant move outside of level
        if (!(0 <= cursor.X() + dx && cursor.X() + dx < this.levelTileDimensions.x))
            return;
        if (!(0 <= cursor.Y() + dy && cursor.Y() + dy < this.levelTileDimensions.y))
            return;

        GridPoint2 camPos = this.camera.getTargetCameraPositionInTiles();
        GridPoint2 screenDim = this.camera.getScreenTileDimensions();
        int w2 = screenDim.x/2;
        int h2 = screenDim.y/2;

        int cursorCamDistanceX = camPos.x - cursor.X(); // +'ve => cam is right of cursor
        int cursorCamDistanceY = camPos.y - cursor.Y(); // +'ve => cam is above cursor

        // "Global" cursor movement on the world map
        // Everything after cursor.move() is just visual
        cursor.move(dx, dy);

        // MOVE RIGHT
        if(dx > 0){
            // Able to scroll
            if(camPos.x + w2 < this.levelTileDimensions.x && cursorCamDistanceX < -maxCursCamDist){
                this.camera.moveCamera(dx,0);
            }
        }

        // MOVE LEFT
        if(dx < 0){
            // Able to scroll
            if(camPos.x - w2 > 0 && cursorCamDistanceX > maxCursCamDist){
                this.camera.moveCamera(dx,0);
            }
        }

        // MOVE UP
        if(dy > 0){
            // Able to scroll
            if(camPos.y + h2 < this.levelTileDimensions.y && cursorCamDistanceY < -maxCursCamDist){
                this.camera.moveCamera(0,dy);
            }
        }

        // MOVE DOWN
        if(dy < 0){
            // Able to scroll
            if(camPos.y - h2 > 0 && cursorCamDistanceY > maxCursCamDist){
                this.camera.moveCamera(0,dy);
            }
        }

    }

    public void mapClick() {
        int[] coords = cursor.getPos();
        GridPoint2 pos = new GridPoint2(coords[0], coords[1]);
        int x = coords[0];
        int y = coords[1];
        MapObject mapObj = level.getObjectAtPosition(pos);
        if(mapObj != null) {
            if (cursor.hasSelectedObject()) {
                if(cursor.getSelectedObject() == mapObj) {
                    Pokemon selPokemon = (Pokemon)cursor.getSelectedObject();
                    makeActionMenu(selPokemon);
                    //GridPoint2 menuCoords = calcMenuCoords();
                    //menu = new Menu(this,menuCoords[0],menuCoords[1]);
                }

            } else {
                // Set selected object

                //Only a pokemon can be a slot 1 selected object
                if(mapObj instanceof Pokemon) {
                    Pokemon mapPkmn = (Pokemon)mapObj;
                    ArrayList<Tile> movable = level.getMoveableTiles(x,y,mapPkmn);
                    OrderedSet<Tile> movableSet = new OrderedSet<Tile>();
                    for(Tile t:movable){
                        movableSet.add(t);
                    }
                    cursor.setSelectedObject(mapPkmn,
                            movable,
                            level.getAttackableTiles(movableSet,2));
                }
            }

        }else {
            if(cursor.hasSelectedObject()) {
                // Nothing on map, but selected object
                // therefore, move to spot if possible

                MapObject cursMapObj = cursor.getSelectedObject();
                if(cursMapObj instanceof Pokemon) {
                    Tile targetTile = level.getTileAtPosition(pos);
                    Pokemon mapPkmn = (Pokemon)cursMapObj;

                    if(cursor.getMoveableTiles().contains(targetTile)) {

                        makeActionMenu(mapPkmn);
                        //GridPoint2 menuCoords = calcMenuCoords();
                        //menu = new Menu(this,menuCoords[0],menuCoords[1]);

                    }else {
                        cursor.cancel();
                    }

                    //log("TOP TILE: Water(" +targetTile.water+"); Foreground("+targetTile.foreground+")" );

                }
            }else {
                //Nothing on map and nothing selected. Do nothing.

            }
        }
    }

    public MapObject getCursorMapObj() {
        return cursor.getSelectedObject();
    }

    /*
     * Finalizes a move request of the selected pokemon to the
     * cursor's present position
     */
    public void commitMovePokemon(){
        Pokemon p = (Pokemon) cursor.getSelectedObject();
        int[] start = p.getPosition();
        int[] end = cursor.getPos();
        this.level.moveMapObj(p,start[0], start[1], end[0], end[1]);
        cursor.clearSelectedObject();
    }
}
