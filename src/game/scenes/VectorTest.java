package game.scenes;

import game.Main;
import game.Scenes;
import game.util.Vector;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;

import static game.Render.*;

public class VectorTest extends Scene {

    private Random r = new Random();
    private HashSet<Button> buttons;
    private HashSet<Bubble> bubbles;

    @Override
    public void init() {
        bubbles = new HashSet<Bubble>();
        buttons = new HashSet<Button>();
        for(int i = 0; i < 750; i++){
            bubbles.add(new Bubble(new Point(r.nextInt(getWidth()), r.nextInt(getHeight()))));
        }
    }

    @Override
    public void update(int delta) {
        if(Keyboard.isKeyDown(Keyboard.KEY_C)){
            bubbles.add(new Bubble(new Point(r.nextInt(getWidth()), r.nextInt(getHeight()))));
        }
        for(Bubble b : bubbles){
            b.update(delta);
        }
        while(Keyboard.next()){
            if(Keyboard.getEventKeyState()){
                switch(Keyboard.getEventKey()){
                    case Keyboard.KEY_1:
                        Main.enterScene(Scenes.SPRITE_TEST);
                        break;
                    case Keyboard.KEY_2:
                        Main.enterScene(Scenes.PHYSICS_TEST);
                        break;
                    case Keyboard.KEY_3:
                        Main.enterScene(Scenes.VECTOR_TEST);
                        break;
                    case Keyboard.KEY_BACK:
                        bubbles.clear();
                        for(int i = 0; i < 750; i++){
                            bubbles.add(new Bubble(
                                    new Point(r.nextInt(getWidth()), r.nextInt(getHeight()))));
                        }
                        break;
                    case Keyboard.KEY_ESCAPE:
                        Main.enterScene(Scenes.PAUSE_MENU);
                        break;
                }
            }
        }
    }

    @Override
    public void render() {
        setCircleSegments(40);
        for(Bubble b : bubbles){
            b.draw();
        }
    }

    private class Bubble{
        private Point position;
        private int radius;
        private Color color;
        private Vector direction;
        private double displacement, speed, distance;

        private final int MAX_RADIUS = 7;

        public Bubble(Point point){
            this.position = point;
            radius = r.nextInt(MAX_RADIUS);
            color = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
            direction = new Vector();
            speed = (r.nextInt(100) + 5) * .000003;
            distance = r.nextInt(getWidth()/2);
        }

        public void update(int delta){
            if(Mouse.isButtonDown(0)) {
                direction.set(getMouseX() - position.x, getMouseY() - position.y);
            } else {
                direction.set(position.x - getMouseX(), position.y - getMouseY());
            }
            if(direction.length() < distance) {
                displacement += speed * delta;
            } else {
                if(displacement > 0)
                    displacement -= speed*2 * delta;
                else
                    displacement = 0;
            }
            if(direction.length() != 0 || !Mouse.isButtonDown(0)) {
                position = Vector.toPoint(direction.normalize().mul(delta * displacement * (1/(direction.length() * .001)))
                        .add(position));
            } else if (Mouse.isButtonDown(0)){
                position = Vector.toPoint(direction.normalize().mul(delta * displacement * (.01/(direction.length() * 1000)))
                        .add(position));
            }
            if(position.x > getWidth())
                position.x = getWidth();
            if(position.x < getX())
                position.x = getX();
            if(position.y > getHeight())
                position.y = getHeight();
            if(position.y < getY())
                position.y = getY();
        }

        public void draw(){
            setColor(color);
            cFillCircle(position.x, position.y, radius);
        }

    }
}
