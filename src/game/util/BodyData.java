package game.util;

import java.awt.*;

public class BodyData {

    public static final int BOX = 0, CIRCLE = 1;

    public String name;
    public Color color;
    public float halfWidth, halfHeight, radius;
    public int type;
    public double age, lifespan;

    public BodyData(int type, float halfWidth, float halfHeight){
        this.type = type;
        if(type == BOX){
            this.halfWidth = halfWidth;
            this.halfHeight = halfHeight;
        } else if (type == CIRCLE){
            this.radius = halfWidth;
            System.out.println("[INFO] Circle size set to halfWidth");
        }
    }

    public BodyData(int type, float halfWidth, float halfHeight, String name){
        this.type = type;
        if(type == BOX){
            this.halfWidth = halfWidth;
            this.halfHeight = halfHeight;
        } else if (type == CIRCLE){
            this.radius = halfWidth;
            System.out.println("[INFO] Circle size set to halfWidth");
        }
        this.name = name;
    }

    public BodyData(int type, float halfWidth, float halfHeight, Color color){
        this.type = type;
        if(type == BOX){
            this.halfWidth = halfWidth;
            this.halfHeight = halfHeight;
        } else if (type == CIRCLE){
            this.radius = halfWidth;
        }
        this.color = color;
    }

    public BodyData(int type, float halfWidth, float halfHeight, Color color, String name){
        this.type = type;
        if(type == BOX){
            this.halfWidth = halfWidth;
            this.halfHeight = halfHeight;
        } else if (type == CIRCLE){
            this.radius = halfWidth;
        }
        this.color = color;
        this.name = name;
    }

    public BodyData(int type, float size){
        this.type = type;
        switch(type){
            case BOX:
                this.halfWidth = size;
                this.halfHeight = size;
                break;
            case CIRCLE:
                this.radius = size;
                break;
            default:
                System.err.println("[ERROR] Invalid body type");
                break;
        }
    }

    public BodyData(int type, float size, Color color){
        this(type, size);
        this.color = color;
    }

    public BodyData(int type, float size, Color color, String name){
        this(type, size, color);
        this.name = name;
    }

    public void update(int delta){
        age += delta * .001;
    }

    /*
    public long getLifetime(){
        return lifetime;
    }

    public int getHalfWidth() {
        return halfWidth;
    }

    public Color getColor() {
        return color;
    }

    public int getHalfHeight() {
        return halfHeight;
    }

    public int getRadius() {
        return radius;
    }

    public int getType() {
        return type;
    }

    public String getName(){
        return name;
    }
    */

}
