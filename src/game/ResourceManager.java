package game;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.lwjgl.Sys;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.awt.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ResourceManager {

    /**
     * ALL resources are pulled relative to THIS class
     * res/[filepath]
     **/

    //TODO: Derive font function (Change existing font size, etc)

    private static Map<String, Texture> textures;
    private static Map<String, UnicodeFont> fonts;
    private static Map<String, Font> awtFonts;
    private static Map<String, Texture[]> textureSets;
    private static boolean textAA;

    private static final float DEFAULT_FONT_SIZE = 20.0f;

    public static void init(){
        textures = new HashMap<String, Texture>();
        fonts = new HashMap<String, UnicodeFont>();
        awtFonts = new HashMap<String, Font>();
        textureSets = new HashMap<String, Texture[]>();
        textAA = SettingManager.getBoolean("textAntialiasing");
        setUpPNGLoader();
        loadResourcesFromJson();
        loadResources();
    }

    public static void loadResourcesFromJson(){
        try{
            BufferedReader file = new BufferedReader(
                    new InputStreamReader(ResourceManager.class.getResourceAsStream("Resources.json")));
            JSONParser parser = new JSONParser();
            JSONObject resourceList = (JSONObject) parser.parse(file);
            for(Object typeObj : resourceList.keySet()){
                String type = (String) typeObj;
                JSONObject resourcesOfType = (JSONObject) resourceList.get(type);
                for(Object entryObj : resourcesOfType.keySet()) {
                    String entry = (String) entryObj;
                    switch (type) {
                        case "textures":
                            addTexture(entry, (String) resourcesOfType.get(entry));
                            break;
                        case "textureSets":
                            JSONArray texArray = (JSONArray) resourcesOfType.get(entry);
                            String[] textures = new String[texArray.size()];
                            for(int i = 0; i < texArray.size(); i++){
                                textures[i] = (String) texArray.get(i);
                            }
                            addTextureSet(entry, textures);
                            break;
                        case "fonts":
                            addFont(entry, (String) resourcesOfType.get(entry));
                            break;
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void loadResources(){
        //addFont("8bit", "res/fonts/8BitFont.ttf", 20);
        addFont("text", new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        addTexture("crosshair", "res/textures/crosshair.png");
    }

    public static void addTexture(String name, String filePath) {
        if (textures.get(name) != null) {
            System.err.println("[CAUTION] Overriding existing texture: name=\"" + name + "\"");
        }
        try {
            String type = filePath.substring(filePath.indexOf('.') + 1, filePath.length());
            textures.put(name, TextureLoader.getTexture(type, ResourceManager.class.getResourceAsStream(filePath)));
            if (textures.get(name) != null) {
                System.out.println("[INFO] Texture \"" + name + "\" successfully loaded from: " + filePath);
            } else {
                System.err.println("[WARNING] Failed to load texture \"" + name + "\" from: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addTextures(String[] names, String[] filePaths) {
        if (names.length == filePaths.length) {
            for (int i = 0; i < names.length; i++) {
                if (textures.get(names[i]) != null) {
                    System.err.println("[CAUTION] Overriding existing texture: name=\"" + names[i] + "\"");
                }
                try {
                    String type = filePaths[i].substring(filePaths[i].indexOf('.') + 1, filePaths[i].length());
                    textures.put(names[i], TextureLoader.getTexture(type, ResourceManager.class.getResourceAsStream(filePaths[i])));
                    if (textures.get(names[i]) != null) {
                        System.out.println("[INFO] Texture \"" + names[i] + "\" successfully loaded from: " + filePaths[i]);
                    } else {
                        System.err.println("[WARNING] Failed to load texture \"" + names[i] + "\" from: " + filePaths[i]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.err.println("[ERROR] Not enough matching identifiers/textures to load");
        }
    }

    public static void addTextures(String name, String[] filePaths) {
        for (int i = 0; i < filePaths.length; i++) {
            if (textures.get((name + i)) != null) {
                System.err.println("[CAUTION] Overriding existing texture: name=\"" + (name + i) + "\"");
            }
            try {
                String type = filePaths[i].substring(filePaths[i].indexOf('.') + 1, filePaths[i].length());
                textures.put((name + i), TextureLoader.getTexture(type, ResourceManager.class.getResourceAsStream(filePaths[i])));
                if (textures.get((name + i)) != null) {
                    System.out.println("[INFO] Texture \"" + (name + i) + "\" successfully loaded from: " + filePaths[i]);
                } else {
                    System.err.println("[WARNING] Failed to load texture \"" + (name + i) + "\" from: " + filePaths[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void addTextureSet(String setName, String[] filePaths) {
        Texture[] textureSet = new Texture[filePaths.length];
        if (textureSets.get(setName) != null) {
            System.err.println("[CAUTION] Overriding existing texture set: name=\"" + setName + "\"");
        }
        for (int i = 0; i < filePaths.length; i++) {
            try {
                String type = filePaths[i].substring(filePaths[i].indexOf('.') + 1, filePaths[i].length());
                textureSet[i] = TextureLoader.getTexture(type, ResourceManager.class.getResourceAsStream(filePaths[i]));
                if (textureSet[i] != null) {
                    System.out.println("[INFO] Texture #" + i + " from texture set \"" + setName
                            + "\" successfully loaded from: " + filePaths[i]);
                } else {
                    System.err.println("[WARNING] Failed to load texture #" + i + " from texture set \"" + setName
                            + "\" from: " + filePaths[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        textureSets.put(setName, textureSet);
        if (textureSets.get(setName) == null) {
            System.err.println("[WARNING] Failed to load texture set \"" + setName);
        }
    }

    public static void addFont(String name, Font font) {
        if (fonts.get(name) != null || awtFonts.get(name) != null) {
            System.err.println("[CAUTION] Overriding existing font: name=\"" + name + "\"");
        }
        try {
            awtFonts.put(name, font);
            UnicodeFont uFont = new UnicodeFont(font);
            uFont.addAsciiGlyphs();
            uFont.getEffects().add(new ColorEffect());
            uFont.loadGlyphs();
            fonts.put(name, uFont);
            if (fonts.get(name) != null || awtFonts.get(name) != null) {
                System.out.println("[INFO] Font \"" + name + "\" successfully loaded from AWT: " + font.getFontName());
            } else {
                System.err.println("[WARNING] Failed to load font \"" + name + "\" from AWT: " + font.getFontName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addFont(String name, String filePath) {
        if (fonts.get(name) != null || awtFonts.get(name) != null) {
            System.err.println("[CAUTION] Overriding existing font: name=\"" + name + "\"");
        }
        try {
            awtFonts.put(name, Font.createFont(Font.TRUETYPE_FONT,
                    ResourceManager.class.getResourceAsStream(filePath)).deriveFont(DEFAULT_FONT_SIZE));
            UnicodeFont uFont = new UnicodeFont(Font.createFont(Font.TRUETYPE_FONT,
                    ResourceManager.class.getResourceAsStream(filePath)).deriveFont(DEFAULT_FONT_SIZE));
            uFont.addAsciiGlyphs();
            uFont.getEffects().add(new ColorEffect());
            uFont.loadGlyphs();
            fonts.put(name, uFont);
            if (fonts.get(name) != null || awtFonts.get(name) != null) {
                System.out.println("[INFO] Font \"" + name + "\" successfully loaded from: " + filePath);
            } else {
                System.err.println("[WARNING] Failed to load font \"" + name + "\" from: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addFont(String name, String filePath, int size) {
        if (fonts.get(name) != null || awtFonts.get(name) != null) {
            System.err.println("[CAUTION] Overriding existing font: name=\"" + name + "\"");
        }
        try {
            awtFonts.put(name, Font.createFont(Font.TRUETYPE_FONT,
                    ResourceManager.class.getResourceAsStream(filePath)).deriveFont((float) size));
            UnicodeFont uFont = new UnicodeFont(Font.createFont(Font.TRUETYPE_FONT,
                    ResourceManager.class.getResourceAsStream(filePath)).deriveFont((float) size));
            uFont.addAsciiGlyphs();
            uFont.getEffects().add(new ColorEffect());
            uFont.loadGlyphs();
            fonts.put(name, uFont);
            if (fonts.get(name) != null || awtFonts.get(name) != null) {
                System.out.println("[INFO] Font \"" + name + "\" successfully loaded from: " + filePath);
            } else {
                System.err.println("[WARNING] Failed to load font \"" + name + "\" from: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addFont(String name, String filePath, int style, int size) {
        if (fonts.get(name) != null || awtFonts.get(name) != null) {
            System.err.println("[CAUTION] Overriding existing font: name=\"" + name + "\"");
        }
        try {
            awtFonts.put(name, Font.createFont(Font.TRUETYPE_FONT,
                    ResourceManager.class.getResourceAsStream(filePath)).deriveFont(style, (float) size));
            UnicodeFont uFont = new UnicodeFont(Font.createFont(Font.TRUETYPE_FONT,
                    ResourceManager.class.getResourceAsStream(filePath)).deriveFont(style, (float) size));
            uFont.addAsciiGlyphs();
            uFont.getEffects().add(new ColorEffect());
            uFont.loadGlyphs();
            fonts.put(name, uFont);
            if (fonts.get(name) != null || awtFonts.get(name) != null) {
                System.out.println("[INFO] Font \"" + name + "\" successfully loaded from: " + filePath);
            } else {
                System.err.println("[WARNING] Failed to load font \"" + name + "\" from: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addDerivedFont(String loadedFontName, int size, String newFontName){
        if(awtFonts.get(loadedFontName) == null){
            System.err.println("[ERROR] Could not derive font: Font " + loadedFontName + " not found/loaded");
            return;
        }
        if(fonts.get(newFontName) != null || awtFonts.get(newFontName) != null){
            System.err.println("[CAUTION] Overriding existing font: name=\"" + newFontName + "\"");
        }
        try {
            awtFonts.put(newFontName, awtFonts.get(loadedFontName).deriveFont((float) size));
            UnicodeFont uFont = new UnicodeFont(awtFonts.get(loadedFontName).deriveFont((float) size));
            uFont.addAsciiGlyphs();
            uFont.getEffects().add(new ColorEffect());
            uFont.loadGlyphs();
            fonts.put(newFontName, uFont);
        } catch (Exception e){
            e.printStackTrace();
        }
        if (fonts.get(newFontName) != null || awtFonts.get(newFontName) != null) {
            System.out.println("[INFO] Font \"" + newFontName + "\" successfully derived from: " + loadedFontName);
        } else {
            System.err.println("[WARNING] Failed to derive font \"" + newFontName + "\" from: " + loadedFontName);
        }
    }

    public static void setUpPNGLoader(){
        PrintStream stdOut = System.out;
        try {
            System.setOut(new PrintStream(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                }
            }));
            TextureLoader.getTexture("png", ResourceManager.class.getResourceAsStream(""));
        } catch (Exception e){}
        System.setOut(stdOut);
    }

    public static Texture getTexture(String name) {
        return textures.get(name);
    }

    public static UnicodeFont getFont(String name) {
        return fonts.get(name);
    }

    public static Font getAWTFont(String name){
        return awtFonts.get(name);
    }

    public static Texture[] getTextureSet(String setName) {
        return textureSets.get(setName);
    }

}
