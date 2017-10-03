package game.scenes;

import java.awt.*;

import static game.Render.*;                //NEEDED
import static game.ResourceManager.*;       //NEEDED
import static game.Main.*;                  //optional

public abstract class Scene {

    protected Rectangle fov;

    public Scene(){}

    public void initAll(){
        this.fov = new Rectangle(getDefaultFOV());
        initResources();
        init();
        manageFOV(false);
        resetFOV();
    }

    public void initResources(){}

    abstract public void init();

    abstract public void update(int delta);

    abstract public void render();

    public void transition(boolean entering){
        if(entering){
            manageFOV(true);
            entering();
        } else {
            manageFOV(false);
            exiting();
        }
    }

    public void manageFOV(boolean entering){
        if(entering){
            setFOV(this.fov);
        } else {
            this.fov.setBounds(getFOV());
        }
    }

    public void entering(){}

    public void exiting(){}
}
