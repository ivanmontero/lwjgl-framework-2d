package game.scenes;

import game.Main;
import game.Scenes;
import game.SettingManager;
import game.util.BodyData;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;

import static game.Render.*;

public class PhysicsTest extends Scene {

    private World world;
    private HashSet<Body> bodies, bodiesToRemove, rainToRemove, rain;
    private int currentBody = 0, rainDelta;
    private final int BOX_SIZE = 50, RAIN_RATE = 30;
    private boolean isRaining;
    private Random rand;
    // 0 - ball, 1 - box

    @Override
    public void init() {
        world = new World(new Vec2(0, 9.8f), SettingManager.getBoolean("physicsDoSleep"));
        bodies = new HashSet<Body>();
        bodiesToRemove = new HashSet<Body>();
        rain = new HashSet<Body>();
        rainToRemove = new HashSet<Body>();
        rand = new Random();
        createBodies();
    }

    @Override
    public void entering(){
        Mouse.setGrabbed(false);
    }

    @Override
    public void update(int delta) {
        //world.step(1/60f, 8, 3);
        //long time = (Sys.getTime() * 1000) / Sys.getTimerResolution();
        world.step(delta/1000f, 8, 3);
        //System.out.println(((Sys.getTime() * 1000) / Sys.getTimerResolution()) - time);
        while(Keyboard.next()){
            if(Keyboard.getEventKeyState()){
                switch(Keyboard.getEventKey()){
                    case Keyboard.KEY_TAB:
                        currentBody = ++currentBody % 2;
                        break;
                    case Keyboard.KEY_BACK:
                        reset();
                        break;
                    case Keyboard.KEY_R:
                        isRaining = !isRaining;
                        break;
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
                }
            }
        }
        if(Mouse.isButtonDown(0)){
            switch (currentBody){
                case 0:
                    BodyDef ballDef = new BodyDef();
                    ballDef.position.set(getB2DVector(getMouseX(), getMouseY()));
                    ballDef.type = BodyType.DYNAMIC;
                    CircleShape ballShape = new CircleShape();
                    ballShape.m_radius = toB2D(10);
                    Body ball = world.createBody(ballDef);
                    ball.setUserData(new BodyData(BodyData.CIRCLE, 10, Color.WHITE));
                    FixtureDef ballFixture = new FixtureDef();
                    ballFixture.density = 1f;
                    ballFixture.restitution = .75f;
                    ballFixture.shape = ballShape;
                    ball.createFixture(ballFixture);
                    bodies.add(ball);
                    break;
                case 1:
                    BodyDef boxDef = new BodyDef();
                    boxDef.position.set(getB2DVector(getMouseX(), getMouseY()));
                    boxDef.type = BodyType.DYNAMIC;
                    PolygonShape boxShape = new PolygonShape();
                    boxShape.setAsBox(toB2D(10), toB2D(10));
                    Body box = world.createBody(boxDef);
                    box.setUserData(new BodyData(BodyData.BOX, 10, 10, Color.WHITE));
                    FixtureDef boxFixture = new FixtureDef();
                    boxFixture.density = 1f;
                    boxFixture.restitution = .75f;
                    boxFixture.shape = boxShape;
                    box.createFixture(boxFixture);
                    bodies.add(box);
                    break;
            }
        }

        if(isRaining) {
            rainDelta += delta;
            if(rainDelta > RAIN_RATE) {
                while(rainDelta > RAIN_RATE) {
                    BodyDef ballDef = new BodyDef();
                    ballDef.position.set(getB2DVector(rand.nextInt(getWidth()), -50));
                    ballDef.type = BodyType.DYNAMIC;
                    CircleShape ballShape = new CircleShape();
                    ballShape.m_radius = toB2D(1.5f);
                    Body ball = world.createBody(ballDef);
                    BodyData bd = new BodyData(BodyData.CIRCLE, 1.5f, Color.BLUE);
                    bd.lifespan = 10;
                    ball.setUserData(bd);
                    FixtureDef ballFixture = new FixtureDef();
                    ballFixture.density = .01f;
                    ballFixture.restitution = .25f;
                    ballFixture.shape = ballShape;
                    ball.createFixture(ballFixture);
                    rain.add(ball);
                    rainDelta -= RAIN_RATE;
                }
            }
        }

        for(Body b : rain){
            BodyData bd = (BodyData) b.getUserData();
            bd.update(delta);
            if(bd.age > bd.lifespan){
                rainToRemove.add(b);
                world.destroyBody(b);
            }
        }

        for(Body b : bodies){
            if(b.getPosition().y > toB2D(getHeight() + getHeight()/4)){
                bodiesToRemove.add(b);
                world.destroyBody(b);
            }
        }
        bodies.removeAll(bodiesToRemove);
        rain.removeAll(rainToRemove);
        bodiesToRemove.clear();
    }

    @Override
    public void render() {
        for(Body b : bodies){
            setCircleSegments(50);
            fillBody(b);
        }
        for(Body b : rain){
            setCircleSegments(20);
            fillBody(b);
        }
    }

    public void createBodies(){
        BodyDef floorDef = new BodyDef();
        floorDef.type = BodyType.STATIC;
        floorDef.position = getB2DVector((int) fov.getWidth()/2, (int) fov.getHeight() + 50);
        PolygonShape floorShape = new PolygonShape();
        floorShape.setAsBox(toB2D((int) fov.getWidth()/2), toB2D(50));
        Body floor = world.createBody(floorDef);
        floor.setUserData(new BodyData(BodyData.BOX, getWidth(), 0, "floor"));
        FixtureDef floorFixture = new FixtureDef();
        floorFixture.density = 1f;
        floorFixture.restitution = .25f;
        floorFixture.shape = floorShape;
        floor.createFixture(floorFixture);
        bodies.add(floor);
        for(int i = 20; i > 0; i--){
            //box_size + gap
            for(int j = fov.width/2 - ((BOX_SIZE + 2)*i)/2; j < fov.width/2 + ((BOX_SIZE + 2)*i)/2; j += (BOX_SIZE + 2)){
                BodyDef boxDef = new BodyDef();
                boxDef.position.set(getB2DVector(j + (BOX_SIZE + 2)/2, fov.height - ((20 - i) * BOX_SIZE + BOX_SIZE/2)));
                boxDef.type = BodyType.DYNAMIC;
                PolygonShape boxShape = new PolygonShape();
                boxShape.setAsBox(toB2D(BOX_SIZE/2), toB2D(BOX_SIZE/2));
                Body box = world.createBody(boxDef);
                box.setUserData(new BodyData(BodyData.BOX, BOX_SIZE/2, BOX_SIZE/2, Color.WHITE));
                FixtureDef boxFixture = new FixtureDef();
                boxFixture.density = 1.5f;
                boxFixture.restitution = .0f;
                boxFixture.shape = boxShape;
                box.createFixture(boxFixture);
                bodies.add(box);
            }
        }
    }

    public void reset(){
        for(Body b : bodies){
            world.destroyBody(b);
        }
        bodies.clear();
        for(Body b : rain){
            world.destroyBody(b);
        }
        rain.clear();
        createBodies();
    }

}
