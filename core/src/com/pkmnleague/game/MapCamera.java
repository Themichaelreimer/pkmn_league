package com.pkmnleague.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class MapCamera {

    private Vector2 screenPixelDimensions;
    private GridPoint2 screenTileDimensions;
    private Vector3 targetCameraPosition; // current camera transform stored in cam
    private OrthographicCamera camera;

    public MapCamera(){

        this.screenPixelDimensions = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera = new OrthographicCamera();
        this.targetCameraPosition = new Vector3();
        this.camera.setToOrtho(false, this.screenPixelDimensions.x, this.screenPixelDimensions.y);
        this.camera.update();
        this.screenTileDimensions = calculateScreenDimensionsInTiles();

    }

    private GridPoint2 calculateScreenDimensionsInTiles() {
        GridPoint2 result = new GridPoint2();
        result.x = (int) (camera.viewportWidth / 16);
        result.y = (int) (camera.viewportHeight / 16);
        return result;
    }

    public Vector2 getViewPort() {
        return new Vector2(camera.viewportWidth, camera.viewportHeight);
    }

    public GridPoint2 getScreenTileDimensions(){
        return this.screenTileDimensions;
    }

    public OrthographicCamera getOrthoCam(){
        return this.camera;
    }

    /**
     * This function moves the target camera position that the camera will move towards every frame
     *
     * @param dx target x in pixels. Multiply by 16 for tile space
     * @param dy target y in pixels. Multiply by 16 for tile space
     */
    public void moveCamera(float dx, float dy) {
        // Moves camera by (dx,dy) pixels
        targetCameraPosition.x += dx;
        targetCameraPosition.y += dy;
    }

    public void moveCamera(int dx, int dy){
        // Moves camera by (dx,dy) tiles
        targetCameraPosition.x += 16*dx;
        targetCameraPosition.y += 16*dy;
    }

    public void moveCameraToPosition(Vector2 target){
        // Moves the camera to a given point in pixel coordinates
        float tx = target.x;
        float ty = target.y;
        this.targetCameraPosition = new Vector3(tx, ty,0);  // Vector3 because the default camera class we're wraping is Vector3
    }

    public void moveCameraToPosition(GridPoint2 target){
        // Moves the camera to a given point in tile coordinates
        this.targetCameraPosition = new Vector3(16f * target.x, 16f * target.y, 0);
    }

    public Vector3 getTargetCameraPositionInPixels(){
        // Current actual camera position in pixels
        return this.targetCameraPosition;
    }

    public GridPoint2 getTargetCameraPositionInTiles(){
        int x = (int) this.targetCameraPosition.x / 16;
        int y = (int) this.targetCameraPosition.y / 16;
        return new GridPoint2(x,y);
    }

    public void update(){
        this.moveCameraToTargetPos();
        this.camera.update();

        float x = camera.position.x / 16;
        float y = camera.position.y / 16;
        //System.out.printf("CurrentPosition: (%f,%f) - TargetPosition: (%f,%f)\n",x,y,targetCameraPosition.x,targetCameraPosition.y);
    }


    public int[] getCamPos(boolean inTiles) {
        int[] result = new int[2];
        if(inTiles) {
            result[0] = (int)(targetCameraPosition.x/16);
            result[1] = (int)(targetCameraPosition.y/16);
        }else {
            result[0] = (int)(targetCameraPosition.x);
            result[1] = (int)(targetCameraPosition.y);
        }

        return result;
    }

    /**
     * This method performs the animation of camera movement, and should only be called in render.
     */
    private void moveCameraToTargetPos() {

        float dist = this.targetCameraPosition.dst(camera.position);
        float maxDistPerFrame = dist/8;
        if(maxDistPerFrame<1.5f)
            maxDistPerFrame=1.5f;

        Vector3 target = targetCameraPosition.cpy();

        if(dist < maxDistPerFrame) {
            camera.position.x = targetCameraPosition.x;
            camera.position.y = targetCameraPosition.y;
        }else {
            Vector3 dv = target.sub(camera.position).nor().scl(maxDistPerFrame);
            camera.translate(dv);
        }

    }

}
