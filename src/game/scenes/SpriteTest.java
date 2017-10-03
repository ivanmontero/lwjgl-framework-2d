package game.scenes;

import game.Main;
import game.Scenes;
import game.util.Animation;
import game.util.Vector;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.Texture;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.HashSet;
import java.util.Iterator;

import static game.Render.*;
import static game.ResourceManager.*;

public class SpriteTest extends Scene {

    private Zombie zombie;
    private Ship ship;
    private Alien alien;
    private HashSet<Explosion> explosions;
    private String currentExplosion;
    private float angleOfMouseFromCenter, followAngle;

    @Override
    public void initResources(){
        addDerivedFont("8bit", 40, "8bit40");
        addFont("regText", new Font(Font.SANS_SERIF, Font.PLAIN, 40));
    }

    @Override
    public void init(){
        zombie = new Zombie(100, 100);
        ship = new Ship(2000, 1000);
        alien = new Alien(1500, 500);
        explosions = new HashSet<>();
        currentExplosion = "explosion1";
        //Mouse.setGrabbed(false);
    }

    @Override
    public void update(int delta) {
        zombie.update(delta);
        ship.update(delta);
        alien.update(delta);
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
                    case Keyboard.KEY_ESCAPE:
                        Main.enterScene(Scenes.PAUSE_MENU);
                        break;
                    case Keyboard.KEY_TAB:
                        switch (currentExplosion){
                            case "explosion1":
                                currentExplosion = "explosion2";
                                break;
                            case "explosion2":
                                currentExplosion = "explosion3";
                                break;
                            case "explosion3":
                                currentExplosion = "explosion4";
                                break;
                            case "explosion4":
                                currentExplosion = "explosion5";
                                break;
                            case "explosion5":
                                currentExplosion = "explosion1";
                                break;
                        }
                        break;
                    case Keyboard.KEY_SPACE:
                        explosions.add(new Explosion(getMouseX(), getMouseY(), getTextureSet(currentExplosion)));
                        break;
                }
            }
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD8))
            moveFOV(0, -5);
        if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD2))
            moveFOV(0, 5);
        if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4))
            moveFOV(-5, 0);
        if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD6))
            moveFOV(5, 0);
        /*
        while (Mouse.next()){
            if(Mouse.getEventButtonState()){
                if(Mouse.getEventButton() == 1){
                    explosions.add(new Explosion(getMouseX(), getMouseY(), getTextureSet(currentExplosion)));
                }
            }
        }
        */
        Iterator<Explosion> exIter = explosions.iterator();
        while(exIter.hasNext()){
            Explosion ex = exIter.next();
            ex.update(delta);
            if(ex.isDone()){
                exIter.remove();
            }
        }
        angleOfMouseFromCenter = toPositiveAngle(
                (float) Math.toDegrees(Math.atan2(getMouseY() - (getHeight()/2), getMouseX() - (getWidth()/2))));
        //getting angle measure from zero
        if(!(followAngle <= angleOfMouseFromCenter + .5f && followAngle >= angleOfMouseFromCenter - .5f)){
            float deltaAngle;
            if(followAngle < angleOfMouseFromCenter){
                deltaAngle = angleOfMouseFromCenter - followAngle;
                if(deltaAngle > 180)
                    followAngle -= .5f;
                else
                    followAngle += .5f;
            } else if (followAngle > angleOfMouseFromCenter){
                deltaAngle = followAngle - angleOfMouseFromCenter;
                if(deltaAngle < 180)
                    followAngle -= .5f;
                else
                    followAngle += .5f;
            }
        } else {
            followAngle = angleOfMouseFromCenter;
        }
        if(followAngle < 0)
            followAngle = 360;
        if(followAngle > 360)
            followAngle = 0;
    }

    @Override
    public void render() {
        // Angular distance test
        setColor(Color.DARK_GRAY);
        setCircleSegments(120);
        cDrawCircle(getX() + getWidth()/2, getY() + getHeight()/2, (getHeight()/8)*3);
        drawLine(getX() + getWidth()/2, getY() + getHeight()/2,
                getX() + getWidth()/2 + (getHeight()/8)*3, getY() + getHeight()/2);
        setColor(Color.GREEN);
        drawLine(getX() + getWidth()/2, getY() + getHeight()/2,
                getX() + getWidth()/2 + (int) (((getHeight()/8)*3) * Math.cos(Math.toRadians(angleOfMouseFromCenter))),
                getY() + getHeight()/2 + (int) (((getHeight()/8)*3) * Math.sin(Math.toRadians(angleOfMouseFromCenter))));

        setColor(Color.RED);
        drawLine(getX() + getWidth()/2, getY() + getHeight()/2,
                getX() + getWidth()/2 + (int) (((getHeight()/8)*3) * Math.cos(Math.toRadians(followAngle))),
                getY() + getHeight()/2 + (int) (((getHeight()/8)*3) * Math.sin(Math.toRadians(followAngle))));
        setFont(getFont("regText"));
        float deltaAngle = Math.abs(angleOfMouseFromCenter - followAngle);
        if(deltaAngle > 180)
            deltaAngle = 360 - deltaAngle;
        drawString(Float.toString(deltaAngle),
                getX() + getWidth()/2 - getCurrentFont().getWidth(Float.toString(deltaAngle))/2,
                getY() + getHeight() - getHeight()/8);


        // Random stuff
        setColor(Color.GREEN);
        cFillRect(getWidth()/2, getHeight()/2, 25, 25, angleOfMouseFromCenter);
        setColor(Color.MAGENTA);
        setFont(getFont("8bit40"));
        drawString("hello", 250, 500);
        setFont(getFont("text"));
        drawString("goodbye", 500, 250);
        setTextureColorReset(false);
        setColor(255, 255, 255, 100);
        cDrawTexture(getTexture("bread"), 750, 750);
        setTextureColorReset(true);
        setColor(Color.MAGENTA);
        drawCircle(0, 0, 50);
        setColor(Color.CYAN);
        drawLine(420, 10, 700, 200);
        setColor(Color.RED);
        if(ship.intersects(zombie.getHitbox())) {
            setColor(Color.GREEN);
        } else {
            setColor(Color.RED);
        }
        drawRect(zombie.getHitbox());
        // entities
        zombie.draw();
        ship.draw();
        alien.draw();
        cDrawTexture(getTexture("crosshair"), getMouseX(), getMouseY(), 30, 30);
        for(Explosion e : explosions){
            e.draw();
        }
    }

    private class Zombie{
        private double speed;
        private Animation current, left;
        private int direction, width, height, x, y;

        public Zombie(int x, int y){
            left = new Animation(getTextureSet("zombieLeft"),
                    new int[]{200, 200, 200, 200});
            this.x = x;
            this.y = y;
            current = left;
            speed = .2;
            direction = 3;
            width = 50;
            height = 100;
        }

        public void update(int delta){
            if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                current = left;
                if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                    current.setFrameTimes(75);
                    x += (int) (delta * speed) * 3;
                } else {
                    current.setFrameTimes(200);
                    x += (int) (delta * speed);
                }
                current.update(delta);
                direction = 1;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                current = left;
                if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                    current.setFrameTimes(75);
                    x -= (int) (delta * speed) * 3;
                } else {
                    current.setFrameTimes(200);
                    x -= (int) (delta * speed);
                }
                current.update(delta);
                direction = 3;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_UP))
                y -= (int) (delta * speed);
            if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))
                y += (int) (delta * speed);
        }

        public void draw(){
            if(direction == 1){
                cDrawFlipTexture(current.getCurrentFrame(),
                        x + width/2, y + height/2, width/2, height/2, true);
            } else {
                cDrawTexture(current.getCurrentFrame(), x + width/2, y + height/2, width/2, height/2);
            }
        }

        public Rectangle getHitbox(){
            return new Rectangle(x, y, width, height);
        }
    }

    private class Ship{
        private Point position; // in middle
        private double speed, maxSpeed, angleDir;
        private int width, height;
        private Animation sprite;
        private Vector direction;
        private AffineTransform trans;

        public Ship(int x, int y){
            this.position = new Point(x, y);
            maxSpeed = .5;
            sprite = new Animation(getTextureSet("ship"));
            this.direction = new Vector();
            width = 50;
            height = 100;
            trans = new AffineTransform();
        }

        public void update(int delta){
            /*
            //keyboard
            if(Keyboard.isKeyDown(Keyboard.KEY_D)){
                angleDir += delta * .004;
            } else if (Keyboard.isKeyDown(Keyboard.KEY_A)){
                angleDir -= delta * .004;
            }
            direction.set(Math.cos(angleDir), Math.sin(angleDir));
            if(Keyboard.isKeyDown(Keyboard.KEY_W)){
                sprite.setFrame(1);
                speed += delta * .002;
                if(speed > maxSpeed){
                    speed = maxSpeed;
                }
            } else {
                if(speed > 0){
                    speed -= delta * .0005;
                } else {
                    speed = 0;
                }
                sprite.setFrame(0);
            }
            position = Vector.toPoint(direction.mul((delta * speed)).add(position));
            */

            //mouse
            this.direction.set(getMouseX() - position.x, getMouseY() - position.y);
            if(Mouse.isButtonDown(0) && direction.length() > 100){
                sprite.setFrame(1);
                speed += delta * .002;
                if(speed > maxSpeed){
                    speed = maxSpeed;
                }
            } else {
                if(speed > 0){
                    speed -= delta * .0005;
                } else {
                    speed = 0;
                }
                sprite.setFrame(0);
            }
            if(direction.length() != 0) {
                position = Vector.toPoint(direction.normalize().mul(speed * delta).add(position));
            }
        }

        public void draw(){
            /*
            pushMatrix();
                rotate(direction.getAngle() + 90, position.x, position.y);
                cDrawTexture(sprite.getCurrentFrame(), (float) position.x, (float) position.y, width/2, height/2 );
            popMatrix();
            */
            cDrawTexture(sprite.getCurrentFrame(), (float) position.x, (float) position.y, width/2, height/2,
                    direction.getAngle() + 90);
        }

        public Shape getHitbox(){
            trans.setToIdentity();
            trans.rotate(Math.toRadians(direction.getAngle() + 90), position.x, position.y);
            return trans.createTransformedShape(new Rectangle(position.x - width/2, position.y - height/2, width, height));
        }

        public boolean intersects(Rectangle r){
            return getHitbox().intersects(r);
        }
    }

    private class Alien{
        private double speed;
        private Animation current, left;
        private int direction, width, height, x, y;

        public Alien(int x, int y){
            left = new Animation(getTextureSet("greenAlienLeft"), 20);
            this.x = x;
            this.y = y;
            current = left;
            speed = .3;
            direction = 3;
            width = 50;
            height = 80;
        }

        public void update(int delta){
            if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
                current = left;
                if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                    current.setFrameTimes(30);
                    x += (int) (delta * speed) * 3;
                } else {
                    current.setFrameTimes(70);
                    x += (int) (delta * speed);
                }
                current.update(delta);
                direction = 1;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
                current = left;
                if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                    current.setFrameTimes(30);
                    x -= (int) (delta * speed) * 3;
                } else {
                    current.setFrameTimes(70);
                    x -= (int) (delta * speed);
                }
                current.update(delta);
                direction = 3;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_W))
                y -= (int) (delta * speed);
            if(Keyboard.isKeyDown(Keyboard.KEY_S))
                y += (int) (delta * speed);
        }

        public void draw(){
            if(direction != 1){
                cDrawFlipTexture(current.getCurrentFrame(),
                        x + width/2, y + height/2, width/2, height/2, true);
            } else {
                cDrawTexture(current.getCurrentFrame(), x + width/2, y + height/2, width/2, height/2);
            }
        }

        public Rectangle getHitbox(){
            return new Rectangle(x, y, width, height);
        }
    }

    private class Explosion{
        protected Animation anim;
        protected int x, y, halfSize;

        public Explosion(int x, int y, Texture[] textureSet){
            anim = new Animation(textureSet, 16);
            anim.setOneTime(true);
            this.x = x;
            this.y = y;
            halfSize = 500;
        }

        public void update(int delta){
            anim.update(delta);
        }

        public boolean isDone(){
            return anim.isDone();
        }

        public void draw(){
            //cDrawTexture(anim.getCurrentFrame(), x, y - 425, 500, 500);
            cDrawTexture(anim.getCurrentFrame(), x, y, 500, 500);
        }

    }

}
