package game;

import game.scenes.Scene;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

/**
 * Known issues:
 *  - lines around text
 *  - fullscreen artifacts
 *
 *  TO-DO list:
 *  - add sound compatibility
 *  - save files
 *  - store settings in separate file
 *  - finish settings manager
 *  - clean imports
 *  - move FOV function
 *  - add multithreading <- massive overhaul... (questionable decision) ( (update + physics) |-> render -> repeat ) NO
 *  - add transition
 *
 *  Finished implementations:
 *  - packages
 *  - settings (save/load)
 *  - relative and raw mouse coordinates
 *  - added physics (jBox2d) support
 *  - transparent texture rendering
 **/

public class Main {

    private static Map<Scenes, Scene> scenes;
    private static Scenes currentScene, previousScene;
    private static long fps, lastFPS, lastFrame, currentFPS;
    private static boolean isFullscreen, initialized;

    public static void init() {
        SettingManager.init();
        try {
            setFullscreen(SettingManager.getBoolean("fullscreen"));
            if (SettingManager.getBoolean("antialiasing")) {
                Display.create(new PixelFormat(8, 0, 0, SettingManager.getInt("antialiasingSamples")));
            } else {
                Display.create();
            }
            System.out.println("[INFO] Window successfully created");
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to create window");
            e.printStackTrace();
            System.exit(-1);
        }

        Render.init();
        ResourceManager.init();
        scenes = new EnumMap<Scenes, Scene>(Scenes.class);

        for (Scenes s : Scenes.values()) {
            scenes.put(s, s.getScene());
            scenes.get(s).initAll();
            scenes.get(s).exiting();
            System.out.println("[INFO] Scene " + s + " successfully loaded");
        }

        if (scenes.isEmpty()) {
            System.err.print("[ERROR] No scenes were initialized");
            System.exit(-1);
        } else {
            System.out.println("[INFO] All scenes and resources successfully loaded");
        }

        currentScene = SettingManager.getInitialScene();
        scenes.get(currentScene).manageFOV(true);
        previousScene = SettingManager.getInitialScene();
        System.out.println("[INFO] Initial scene set to " + SettingManager.getInitialScene());
        lastFPS = (Sys.getTime() * 1000) / Sys.getTimerResolution();
        initialized = true;
    }

    public static void loop() {
        System.out.println("[INFO] Entering main game loop");
        while (!Display.isCloseRequested()) {
            Render.clear();

            update(getDelta());
            render();

            updateFPS();

            Display.update();
            Display.sync(60);
        }
    }

    public static void update(int delta) {
        scenes.get(currentScene).update(delta);
    }

    public static void render() {
        scenes.get(currentScene).render();
    }

    public static void updateFPS() {
        if (((Sys.getTime() * 1000) / Sys.getTimerResolution()) - lastFPS > 1000) {
            Display.setTitle("FPS: " + fps);
            currentFPS = fps;
            fps = 0;
            lastFPS += 1000;
        }
        fps++;
    }

    public static void exit() {
        SettingManager.save();
        System.out.println("[INFO] Exiting");
        Display.destroy();
        System.exit(0);
    }

    public static int getDelta() {
        long time = (Sys.getTime() * 1000) / Sys.getTimerResolution();
        int delta = (int) (time - lastFrame);
        lastFrame = time;
        return delta;
    }

    public static Scene getCurrentScene() {
        return scenes.get(currentScene);
    }

    public static Scene getScene(Scenes scene) {
        return scenes.get(scene);
    }

    public static Scene getPreviousScene() {
        return scenes.get(previousScene);
    }

    public static int getScreenHeight() {
        return Display.getHeight();
    }

    public static int getScreenWidth() {
        return Display.getWidth();
    }

    public static int getCurrentFPS(){
        return (int) currentFPS;
    }

    public static void setDisplayMode(int width, int height, boolean fullscreen) {
        if ((Display.getDisplayMode().getWidth() == width) && (Display.getDisplayMode().getHeight() == height) &&
                (Display.isFullscreen() == fullscreen)) {
            return;
        }
        try {
            DisplayMode targetDisplayMode = null;
            if (fullscreen) {
                DisplayMode[] allModes = Display.getAvailableDisplayModes();
                int freq = 0;
                for (int i = 0; i < allModes.length; i++) {
                    DisplayMode current = allModes[i];
                    if ((current.getWidth() == width) && (current.getHeight() == height)) {
                        if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
                            if ((targetDisplayMode == null) ||
                                    (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
                                targetDisplayMode = current;
                                freq = targetDisplayMode.getFrequency();
                            }
                        }
                        if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
                                (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
                            targetDisplayMode = current;
                            break;
                        }
                    }
                }
            } else {
                targetDisplayMode = new DisplayMode(width, height);
            }

            if (targetDisplayMode == null) {
                System.err.println("[ERROR] Failed to find display mode: x=" + width + " y=" + height
                        + " fullscreen=" + fullscreen);
                return;
            } else {
                System.out.println("[INFO] Display mode set to: " + targetDisplayMode.toString()
                        + " fullscreen=" + fullscreen);
            }

            Display.setDisplayMode(targetDisplayMode);
            Display.setFullscreen(fullscreen);
            isFullscreen = fullscreen;
        } catch (Exception e) {
            System.err.println("[ERROR] Unable to setup display mode: " + width + " y=" + height
                    + " fullscreen=" + fullscreen);
            e.printStackTrace();
        }
    }

    public static void setFullscreen(boolean fullscreen) {
        if (fullscreen) {
            setDisplayMode(
                    (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                    (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(),
                    fullscreen);
            Display.setVSyncEnabled(true);
            if(initialized)
                Render.setViewport(Display.getWidth(), Display.getHeight());
        } else {
            setDisplayMode(SettingManager.getInt("windowedWidth"), SettingManager.getInt("windowedHeight"), fullscreen);
            if(initialized)
                Render.setViewport(SettingManager.getInt("windowedWidth"), SettingManager.getInt("windowedHeight"));
        }
        isFullscreen = fullscreen;
    }

    public static boolean isFullscreen() {
        return isFullscreen;
    }

    public static void enterScene(Scenes scene) {
        if(currentScene.ID != scene.ID) {
            previousScene = currentScene;
            currentScene = scene;
            scenes.get(previousScene).transition(false);
            scenes.get(currentScene).transition(true);
            System.out.println("[INFO] Scene switched from " + previousScene + " to " + currentScene);
        }
    }

    public static void enterPreviousScene() {
        enterScene(previousScene);
    }

    public static void main(String[] args) {
        init();
        loop();
        exit();
    }
}