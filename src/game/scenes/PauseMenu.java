package game.scenes;

import game.Main;
import game.Scenes;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.BufferedReader;
import java.util.HashSet;

import static game.Render.*;
import static game.ResourceManager.*;

public class PauseMenu extends Scene {
    private HashSet<Button> buttons;

    private static final int BUTTON_WIDTH = 350, BUTTON_HEIGHT = 50;

    @Override
    public void initResources(){
        addDerivedFont("8bit", 30, "button");
    }

    @Override
    public void init() {
        buttons = new HashSet<>();
        buttons.add(new Button(getX() + getWidth()/2 - BUTTON_WIDTH/2, getY() + (getHeight()/10)*3 - BUTTON_HEIGHT/2,
                BUTTON_WIDTH, BUTTON_HEIGHT, "Resume"){
            @Override
            public void clicked(){
                Main.enterPreviousScene();
            }
        });
        buttons.add(new Button(getX() + getWidth()/2 - BUTTON_WIDTH/2, getY() + (getHeight()/10)*4 - BUTTON_HEIGHT/2,
                BUTTON_WIDTH, BUTTON_HEIGHT){
            @Override
            public void clicked(){
                Main.setFullscreen(!Main.isFullscreen());
            }
            @Override
            public void update(int delta){
                isHighlighted = bounds.contains(getMousePoint());
                if(Main.isFullscreen()){
                    text = "Windowed";
                } else {
                    text = "Fullscreen";
                }
            }
        });
        buttons.add(new Button(getX() + getWidth()/2 - BUTTON_WIDTH/2, getY() + (getHeight()/10)*5 - BUTTON_HEIGHT/2,
                BUTTON_WIDTH, BUTTON_HEIGHT, "Exit"){
            @Override
            public void clicked(){
                Main.exit();
            }
        });
    }

    @Override
    public void update(int delta) {
        for(Button b : buttons){
            b.update(delta);
        }
        while(Mouse.next()){
            if(Mouse.getEventButtonState()){
                if(Mouse.getEventButton() == 0){
                    for(Button b : buttons){
                        if(b.getBounds().contains(getMousePoint())){
                            b.clicked();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void render() {
        Main.getPreviousScene().render();
        setColor(35, 35, 35, 140);
        fillRect(0, 0, getWidth(), getHeight());
        for(Button b : buttons){
            b.draw();
        }
    }

    private class Button{
        protected float x, y, width, height;
        protected String text;
        protected Rectangle bounds;
        protected boolean isHighlighted;

        public Button(int x, int y, int width, int height, String text){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.text = text;
            bounds = new Rectangle(x, y, width, height);
        }

        public Button(int x, int y, int width, int height){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.text = "";
            bounds = new Rectangle(x, y, width, height);
        }

        public void update(int delta){
            isHighlighted = bounds.contains(getMousePoint());
        }

        public void draw(){
            if(isHighlighted){
                setColor(Color.WHITE);
                fillRect(x, y, width, height);
                setColor(Color.BLACK);
                drawRect(x, y, width, height);
                setFont(getFont("button"));
                drawString(text, x + width/2 - getCurrentFont().getWidth(text)/2,
                        y + height/2 - (getCurrentFont().getAscent() + getCurrentFont().getDescent())/2);
            } else {
                setColor(Color.BLACK);
                fillRect(x, y, width, height);
                setColor(Color.WHITE);
                drawRect(x, y, width, height);
                setFont(getFont("button"));
                drawString(text, x + width/2 - getCurrentFont().getWidth(text)/2,
                        y + height/2 - (getCurrentFont().getAscent() + getCurrentFont().getDescent())/2);
            }
        }

        public void clicked(){}

        public Rectangle getBounds(){
            return bounds;
        }

    }
}
