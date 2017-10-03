package game;

import game.util.BodyData;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.*;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.Texture;

import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

// -Djava.library.path="native/[os name here]"

//TODO: Rotating textures

public class Render {

    private static int b2dToPixel;
    private static float pixelToB2d;
    private static Color currentColor, previousColor;
    private static UnicodeFont currentFont;
    private static ColorEffect colorEffect;
    private static int circleSegments;
    private static Rectangle fov, defaultFov;
    private static boolean textureColorReset;

    public static void init(){
        currentFont = new UnicodeFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        colorEffect = new ColorEffect();
        currentColor = Color.WHITE;
        previousColor = Color.WHITE;
        circleSegments = 90;
        fov = new Rectangle(0, 0, SettingManager.getInt("initFOVWidth"), SettingManager.getInt("initFOVHeight"));
        defaultFov = new Rectangle(0, 0, SettingManager.getInt("initFOVWidth"), SettingManager.getInt("initFOVHeight"));
        b2dToPixel = SettingManager.getInt("physicsMeterToPixels");
        pixelToB2d = 1.0f/(float) b2dToPixel;
        textureColorReset = true;

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, SettingManager.getInt("initFOVWidth"), SettingManager.getInt("initFOVHeight"), 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glViewport(0, 0, Main.getScreenWidth(), Main.getScreenHeight());

        System.out.println("[INFO] Window FOV set to: " + SettingManager.getInt("initFOVWidth")
                + " x " + SettingManager.getInt("initFOVHeight"));
        System.out.println("[INFO] OpenGL successfully initialized");
    }

    /**************************************** - Normal Utility Functions - *******************************************/

    public static void clear(){
        glClear(GL_COLOR_BUFFER_BIT);
        glLoadIdentity();
    }

    public static void pushMatrix(){
        glPushMatrix();
    }

    public static void popMatrix(){
        glPopMatrix();
    }

    public static void rotate(float angle, float cx, float cy){
        glTranslatef(cx, cy, 0);
        glRotatef(angle, 0.0f, 0.0f, 1.0f);
        glTranslatef(-cx, -cy, 0);
    }

    public static void setClearColor(Color c){
        glClearColor(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

    //fonts MUST be loaded before being used -> performance
    public static void setFont(UnicodeFont font){
        if(currentFont != font) {
            currentFont = font;
        }
    }

    public static void setColor(Color c){
        setColor(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

    public static void setColor(int r, int g, int b){
        setColor(r, g, b, 255);
    }


    public static void setColor(int r, int g, int b, int a){
        previousColor = currentColor;
        glColor4f(r/255.0f, g/255.0f, b/255.0f, a/255.0f);
        currentColor = new Color(r, g, b, a);
    }

    public static void setCircleSegments(int segments){
        circleSegments = segments;
    }

    public static void setFOV(int x, int y, int width, int height){
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(x, x + width, y + height, y, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        fov.setBounds(x, y, width, height);
    }

    public static void setFOV(Rectangle r){
        setFOV(r.x, r.y, r.width, r.height);
    }

    public static void moveFOV(int x, int y){
        setFOV(getX() + x, getY() + y, getWidth(), getHeight());
    }

    public static void setViewport(int width, int height){
        glViewport(0, 0, width, height);
    }

    public static void setTextureColorReset(boolean reset){
        textureColorReset = reset;
    }

    public static void translate(int x, int y) {
        glTranslatef(x, y, 0.0f);
    }

    public static void resetFOV(){
        fov.setBounds(defaultFov);
    }

    /******************************************* - Base Shape Functions - ********************************************/

    public static void drawRect(float x, float y, float width, float height, boolean filled, float angle){
        glPushMatrix();
        glTranslatef(x + width/2, y + height/2, 0.0f);
        glRotatef(angle, 0.0f, 0.0f, 1.0f);
        if(filled)
            glBegin(GL_QUADS);
        else
            glBegin(GL_LINE_LOOP);
        glVertex2f(-width/2, -height/2);
        glVertex2f(width/2, -height/2);
        glVertex2f(width/2, height/2);
        glVertex2f(-width/2, height/2);
        glEnd();
        glPopMatrix();
    }

    public static void drawCircle(float x, float y, float diameter, boolean filled){
        float radius = diameter/2;
        float cx = x + radius;
        float cy = y + radius;
        glPushMatrix();
        glTranslatef(cx, cy, 0.0f);
        if(filled)
            glBegin(GL_TRIANGLE_FAN);
        else
            glBegin(GL_LINE_LOOP);
        for (int i = 0; i < circleSegments; i++){
            double theta = 2.0f * Math.PI * i / circleSegments;
            float px = (float) (radius * Math.cos(theta));
            float py = (float) (radius * Math.sin(theta));
            glVertex2f(px, py);
        }
        glEnd();
        glPopMatrix();
    }

    public static void drawPolygon(Point[] vertices, boolean filled){
        if(filled)
            glBegin(GL_POLYGON);
        else
            glBegin(GL_LINE_LOOP);
        for(Point p : vertices){
            glVertex2f((float) p.getX(), (float) p.getY());
        }
        glEnd();
    }

    /**************************************** - Primitive Draw Functions - ********************************************/

    public static void drawCircle(float x, float y, float diameter){
        drawCircle(x, y, diameter, false);
    }

    public static void drawRect(Rectangle r){
        drawRect(r.x, r.y, r.width, r.height, false, 0);
    }

    public static void drawRect(Rectangle r, float angle){
        drawRect(r.x, r.y, r.width, r.height, false, angle);
    }

    public static void drawLine(float x1, float y1, float x2, float y2){
        glBegin(GL_LINES);
        glVertex2f(x1, y1);
        glVertex2f(x2, y2);
        glEnd();
    }

    public static void drawRect(float x, float y, float width, float height){
        drawRect(x, y, width, height, false, 0);
    }

    public static void drawRect(float x, float y, float width, float height, float angle){
        drawRect(x, y, width, height, false, angle);
    }

    public static void drawPoint(float x, float y){
        glBegin(GL_POINTS);
        glVertex2f(x, y);
        glEnd();
    }

    public static void drawPolygon(Point[] vertices){
        drawPolygon(vertices, false);
    }

    /************************************ - Center Primitive Draw Functions - *****************************************/

    public static void cDrawCircle(float cx, float cy, float radius){
        drawCircle(cx - radius, cy - radius, radius*2, false);
    }

    public static void cDrawRect(float x, float y, float halfWidth, float halfHeight){
        drawRect(x - halfWidth, y - halfHeight, halfWidth*2, halfHeight*2, false, 0);
    }

    public static void cDrawRect(float x, float y, float halfWidth, float halfHeight, float angle){
        drawRect(x - halfWidth, y - halfHeight, halfWidth*2, halfHeight*2, false, angle);
    }

    /**************************************** - Primitive Fill Functions - ********************************************/

    public static void fillCircle(float x, float y, float diameter){
        drawCircle(x, y, diameter, true);
    }

    public static void fillRect(float x, float y, float width, float height){
        drawRect(x, y, width, height, true, 0);
    }

    public static void fillRect(float x, float y, float width, float height, float angle){
        drawRect(x, y, width, height, true, angle);
    }

    public static void fillPolygon(Point[] vertices){
        drawPolygon(vertices, true);
    }

    /************************************ - Center Primitive Fill Functions - *****************************************/

    public static void cFillCircle(float cx, float cy, float radius){
        drawCircle(cx - radius, cy - radius, radius*2, true);
    }

    public static void cFillRect(float x, float y, float halfWidth, float halfHeight){
        drawRect(x - halfWidth, y - halfHeight, halfWidth*2, halfHeight*2, true, 0);
    }

    public static void cFillRect(float x, float y, float halfWidth, float halfHeight, float angle){
        drawRect(x - halfWidth, y - halfHeight, halfWidth*2, halfHeight*2, true, angle);
    }

    /****************************************** - Base Texture Function - *********************************************/

    public static void drawTexture(Texture tex, float x, float y, float width, float height, boolean verticalFlip,
                                   boolean horizontalFlip, float angle){
        if(verticalFlip && horizontalFlip)
            System.err.println("[ERROR] Vertical and horizontal flip combined not yet supported"); //TODO
        if(textureColorReset)
            glColor3f(1.0f, 1.0f, 1.0f);
        glEnable(GL_TEXTURE_2D);
        glPushMatrix();
        glTranslatef(x + width/2, y + height/2, 0.0f);
        glRotatef(angle, 0.0f, 0.0f, 1.0f);
        tex.bind();
        glBegin(GL_QUADS);
        if(verticalFlip || horizontalFlip) {
            if (horizontalFlip) {
                glTexCoord2f(tex.getWidth(), 0);
                glVertex2f(-width/2, -height/2);
                glTexCoord2f(0, 0);
                glVertex2f(width/2, -height/2);
                glTexCoord2f(0, tex.getHeight());
                glVertex2f(width/2, height/2);
                glTexCoord2f(tex.getWidth(), tex.getHeight());
                glVertex2f(-width/2, height/2);
            } else {
                glTexCoord2f(0, tex.getHeight());
                glVertex2f(-width/2, -height/2);
                glTexCoord2f(tex.getWidth(), tex.getHeight());
                glVertex2f(width/2, -height/2);
                glTexCoord2f(tex.getWidth(), 0);
                glVertex2f(width/2, height/2);
                glTexCoord2f(0, 0);
                glVertex2f(-width/2, height/2);
            }
        } else {
            glTexCoord2f(0,0);
            glVertex2f(-width/2, -height/2);
            glTexCoord2f(tex.getWidth(),0);
            glVertex2f(width/2, -height/2);
            glTexCoord2f(tex.getWidth(),tex.getHeight());
            glVertex2f(width/2, height/2);
            glTexCoord2f(0,tex.getHeight());
            glVertex2f(-width/2 , height/2);
        }
        glEnd();
        glPopMatrix();
        glDisable(GL_TEXTURE_2D);
        if(textureColorReset)
            setColor(currentColor);
    }

    /****************************************** - Texture Draw Functions - ********************************************/

    public static void drawTexture(Texture tex, float x, float y){
        drawTexture(tex, x, y, tex.getTextureWidth(), tex.getTextureHeight(), false, false, 0);
    }

    public static void drawTexture(Texture tex, float x, float y, float angle){
        drawTexture(tex, x, y, tex.getTextureWidth(), tex.getTextureHeight(), false, false, angle);
    }

    public static void drawTexture(Texture tex, float x, float y, float width, float height){
        drawTexture(tex, x, y, width, height, false, false, 0);
    }

    public static void drawTexture(Texture tex, float x, float y, float width, float height, float angle){
        drawTexture(tex, x, y, width, height, false, false, angle);
    }

    public static void drawFlipTexture(Texture tex, float x, float y, boolean horizontal){
        drawTexture(tex, x, y, tex.getTextureWidth(), tex.getTextureHeight(), !horizontal, horizontal, 0);
    }

    public static void drawFlipTexture(Texture tex, float x, float y, boolean horizontal, float angle){
        drawTexture(tex, x, y, tex.getTextureWidth(), tex.getTextureHeight(), !horizontal, horizontal, angle);
    }

    public static void drawFlipTexture(Texture tex, float x, float y, float width, float height, boolean horizontal){
        drawTexture(tex, x, y, width, height, !horizontal, horizontal, 0);
    }

    public static void drawFlipTexture(Texture tex, float x, float y, float width, float height, boolean horizontal,
                                       float angle){
        drawTexture(tex, x, y, width, height, !horizontal, horizontal, angle);
    }

    public static void drawString(String text, float x, float y){
        glEnable(GL_TEXTURE_2D);
        currentFont.drawString(x, y, text, toSlickColor(currentColor));
        glDisable(GL_TEXTURE_2D);
    }

    /************************************* - Center Texture Draw Functions - *****************************************/

    public static void cDrawTexture(Texture tex, float cx, float cy){
        drawTexture(tex, cx - tex.getTextureWidth()/2, cy - tex.getTextureHeight()/2, tex.getTextureWidth(),
                tex.getTextureHeight(), false, false, 0);
    }

    public static void cDrawTexture(Texture tex, float cx, float cy, float angle){
        drawTexture(tex, cx - tex.getTextureWidth()/2, cy - tex.getTextureHeight()/2, tex.getTextureWidth(),
                tex.getTextureHeight(), false, false, angle);
    }

    public static void cDrawTexture(Texture tex, float cx, float cy, float halfWidth, float halfHeight){
        drawTexture(tex, cx - halfWidth, cy - halfHeight, halfWidth*2, halfHeight*2, false, false, 0);
    }

    public static void cDrawTexture(Texture tex, float cx, float cy, float halfWidth, float halfHeight, float angle){
        drawTexture(tex, cx - halfWidth, cy - halfHeight, halfWidth*2, halfHeight*2, false, false, angle);
    }

    public static void cDrawFlipTexture(Texture tex, float cx, float cy, boolean horizontal){
        drawTexture(tex, cx - tex.getTextureWidth()/2, cy - tex.getTextureHeight()/2, tex.getTextureWidth(),
                tex.getTextureHeight(), !horizontal, horizontal, 0);
    }

    public static void cDrawFlipTexture(Texture tex, float cx, float cy, boolean horizontal, float angle){
        drawTexture(tex, cx - tex.getTextureWidth()/2, cy - tex.getTextureHeight()/2, tex.getTextureWidth(),
                tex.getTextureHeight(), !horizontal, horizontal, angle);
    }

    public static void cDrawFlipTexture(Texture tex, float cx, float cy, float halfWidth, float halfHeight,
                                        boolean horizontal){
        drawTexture(tex, cx - halfWidth, cy - halfHeight, halfWidth*2, halfHeight*2, !horizontal, horizontal, 0);
    }

    public static void cDrawFlipTexture(Texture tex, float cx, float cy, float halfWidth, float halfHeight,
                                        boolean horizontal, float angle){
        drawTexture(tex, cx - halfWidth, cy - halfHeight, halfWidth*2, halfHeight*2, !horizontal, horizontal, angle);
    }

    /****************************************** - Physics Draw Functions - ********************************************/
    // Must be given PIXEL parameters, handles the converting to B2D

    public static void drawBody(Body body, boolean filled) {
        BodyData bd;
        if (body.getUserData() != null && body.getUserData() instanceof BodyData){
            bd = (BodyData) body.getUserData();
        } else {
            System.err.println("[ERROR] Could not find body data for body");
            return;
        }
        if(bd.color != null){
            setColor(bd.color);
        }
        Vec2 position = body.getPosition().mul(b2dToPixel);
        glPushMatrix();
        glTranslatef(position.x, position.y, 0);
        glRotated(Math.toDegrees(body.getAngle()), 0, 0, 1);
        switch (bd.type){
            case BodyData.BOX:
                if(filled)
                    glBegin(GL_QUADS);
                else
                    glBegin(GL_LINE_LOOP);
                glVertex2f(-bd.halfWidth , -bd.halfHeight);
                glVertex2f(bd.halfWidth , -bd.halfHeight);
                glVertex2f(bd.halfWidth , bd.halfHeight);
                glVertex2f(-bd.halfWidth, bd.halfHeight);
                glEnd();
                break;
            case  BodyData.CIRCLE:
                if(filled)
                    glBegin(GL_TRIANGLE_FAN);
                else
                    glBegin(GL_LINE_LOOP);
                for (int i = 0; i < circleSegments; i++){
                    double theta = 2.0f * Math.PI * i / circleSegments;
                    float px = (float) (bd.radius * Math.cos(theta));
                    float py = (float) (bd.radius * Math.sin(theta));
                    glVertex2f(px, py);
                }
                glEnd();
                break;
        }
        glPopMatrix();
        if(bd.color != null){
            setColor(previousColor);
        }
    }

    public static void drawBody(Body body) {
        drawBody(body, false);
    }

    public static void fillBody(Body body) {
        drawBody(body, true);
    }

    /************************************ - Render-Specific Utility Functions - ***************************************/

    public static Rectangle getFOV(){
        return fov;
    }

    public static Rectangle getDefaultFOV(){
        return defaultFov;
    }

    public static int getX(){
        return (int) fov.getX();
    }

    public static int getY(){
        return (int) fov.getY();
    }

    public static int getWidth(){
        return (int) fov.getWidth();
    }

    public static int getHeight(){
        return (int) fov.getHeight();
    }

    //LWJGL decided the bottom left hand corner should be (0,0)
    public static int getRawMouseX(){
        return Mouse.getX();
    }

    public static int getRawMouseY(){
        return Display.getHeight() - Mouse.getY();
    }

    /*
    public int getRawMouseDX(){
        return Mouse.getDX();
    }

    public int getRawMouseDY(){
        return -Mouse.getDY();
    }

    public int getMouseDX(){
        return (int)((float) getRawMouseDX() * ((float) fov.getWidth() / (float) Display.getWidth()));
    }

    public int getMouseDY(){
        return (int)((float) getRawMouseDY() * ((float) fov.getHeight() / (float) Display.getHeight()));
    }
    */

    public static int getMouseX(){
        return (int) (fov.getX() + ((float)fov.getWidth() * ((float) getRawMouseX() / (float) Display.getWidth() )));
    }

    public static int getMouseY(){
        return (int) (fov.getY() + ((float)fov.getHeight() * ((float) getRawMouseY() / (float) Display.getHeight() )));
    }

    public static Point getMousePoint(){
        return new Point(getMouseX(), getMouseY());
    }

    public static Color getCurrentColor() {
        return currentColor;
    }

    public static Color getPreviousColor() {
        return previousColor;
    }

    public static UnicodeFont getCurrentFont(){
        return currentFont;
    }

    public static int toPixel(float b2dSize){
        return (int) b2dSize * b2dToPixel;
    }

    public static float toB2D(float pixelSize){
        return pixelSize * pixelToB2d;
    }

    public static Vec2 getB2DVector(int pixelX, int pixelY){
        return new Vec2(pixelX, pixelY).mul(pixelToB2d);
    }

    public static float toPositiveAngle(float theta){
        return (theta + 360) % 360;
    }

    private static org.newdawn.slick.Color toSlickColor(Color color){
        return new org.newdawn.slick.Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

}