package game.util;

import game.Render;
import game.ResourceManager;

import java.awt.*;

public abstract class Entity implements Updatable, Drawable {

    protected int x, y, width, height;
    protected Rectangle hitbox;

    public Entity(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hitbox = new Rectangle(x, y, width, height);
    }

    abstract public void update(int delta);

    abstract public void draw();

    public void updateHitbox(){
        hitbox.setBounds(x, y, width, height);
    }

    public void setLocation(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void setLocation(Point point){
        this.x = (int) point.getX();
        this.y = (int) point.getY();
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Point getLocation(){
        return new Point(this.x, this. y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public boolean intersects(Entity other){
        return hitbox.intersects(other.getHitbox());
    }

}
