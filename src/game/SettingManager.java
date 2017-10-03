package game;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class SettingManager {

    //TODO: Finish settings manager

    private static File file;
    private static Scanner input;
    private static PrintStream output;
    private static Map<String, String> settings = new LinkedHashMap<String, String>();
    private static Map<String, String> defaultSettings = new LinkedHashMap<String, String>();
    private static Scenes initialScene = Scenes.SPRITE_TEST;

    private static final boolean USE_FILE_SETTINGS = true;
    private static final boolean USE_LOG_FILE = false;
    private static final  boolean DEVELOPER_MODE = true;

    private static String settingsFilePath = "settings.dat";
    private static String os, nativesPath;

    /*
    public SettingManager(String filePath){
        setUpLogger();
        getDefaults();
        setUpNatives();
        settingsFilePath = filePath;
        System.out.println("[INFO] Settings file set to: " + filePath);
        System.out.println("[INFO] Using external file settings: " + USE_FILE_SETTINGS);
        if(USE_FILE_SETTINGS){
            loadFromFile();
        } else {
            System.err.println("[WARNING] Using external file settings set to false. Loading defaults");
            loadDefaults();
        }
    }
    */

    public static void init(){
        setUpLogger();
        getDefaults();
        setUpNatives();
        System.out.println("[INFO] Using external file settings: " + USE_FILE_SETTINGS);
        if(USE_FILE_SETTINGS) {
            loadFromFile();
        } else {
            loadDefaults();
        }
        validate();
    }

    public static void getDefaults(){
        defaultSettings.put("fullscreen", "false");
        defaultSettings.put("fullResolutionFullscreen", "true");
        defaultSettings.put("windowedWidth", "1280");
        defaultSettings.put("windowedHeight", "720");
        defaultSettings.put("initFOVWidth", "2560");
        defaultSettings.put("initFOVHeight", "1440");
        defaultSettings.put("antialiasing", "true");
        defaultSettings.put("antialiasingSamples","8");
        defaultSettings.put("textAntialiasing", "true");
        defaultSettings.put("physicsMeterToPixels", "25");
        defaultSettings.put("physicsDoSleep", "true");
    }

    public static void setUpNatives(){
        os = System.getProperty("os.name").toLowerCase();
        System.out.println("[INFO] Operating system: " + os);
        if(DEVELOPER_MODE) {
            if (os.contains("win")) {
                nativesPath = "native/windows";
            } else if (os.contains("mac")) {
                nativesPath = "native/macosx";
            } else {
                System.err.println("[ERROR] Operating system incompatible");
                System.exit(-1);
            }
            System.setProperty("org.lwjgl.librarypath", new File(nativesPath).getAbsolutePath());
            System.out.println("[INFO] Native files successfully loaded");
        }
    }

    public static void loadFromFile(){
        file = new File(settingsFilePath);
        if(!file.exists()){
            System.err.println("[WARNING] Settings file " + settingsFilePath + " not found. Creating new default" +
                    " settings file");
            saveDefault();
        } else {
            try {
                input = new Scanner(file);
                while(input.hasNextLine()){
                    String line = input.nextLine();
                    if(line.indexOf('=') == -1)
                        continue;
                    String settingName = line.substring(0, line.indexOf('='));
                    String value = line.substring(line.indexOf('=') + 1, line.length());
                    settings.put(settingName, value);
                }
                System.out.println("[INFO] Settings successfully loaded from: " + settingsFilePath);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void loadDefaults(){
        settings.putAll(defaultSettings);
    }

    public static void validate(){
        for(String s : defaultSettings.keySet()){
            if(settings.get(s) == null){
                settings.put(s, defaultSettings.get(s));
            }
        }
    }

    public static void saveDefault(){
        file = new File(settingsFilePath);
        try{
            if (!file.exists()){
                file.createNewFile();
            }
            output = new PrintStream(file);
        } catch (IOException e){
            e.printStackTrace();
        }
        for(String s : defaultSettings.keySet()){
            output.println(s + "=" + defaultSettings.get(s));
        }
    }

    public static void save(){
        if(USE_FILE_SETTINGS) {
            file = new File(settingsFilePath);
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                output = new PrintStream(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (String s : settings.keySet()) {
                output.println(s + "=" + settings.get(s));
            }
            System.out.println("[INFO] Settings successfully saved to: " + settingsFilePath);
        }
    }

    public static void put(String settingName, String value){
        settings.put(settingName, value);
    }

    public static String get(String settingName){
        if(!settingExists(settingName)) {
            if(defaultSettingExists(settingName)){
                settings.put(settingName, defaultSettings.get(settingName));
                return settings.get(settingName);
            }
            System.err.println("[ERROR] Setting \"" + settingName + "\" does not exist");
            System.exit(-1);
        }
        return settings.get(settingName);
    }

    public static int getInt(String settingName){
        if(!settingExists(settingName)) {
            if(defaultSettingExists(settingName)){
                settings.put(settingName, defaultSettings.get(settingName));
                return Integer.parseInt(settings.get(settingName));
            }
            System.err.println("[ERROR] Setting \"" + settingName + "\" does not exist");
            System.exit(-1);
        }
        return Integer.parseInt(settings.get(settingName));
    }

    public static Boolean getBoolean(String settingName){
        if(!settingExists(settingName)) {
            if(defaultSettingExists(settingName)){
                settings.put(settingName, defaultSettings.get(settingName));
                return Boolean.parseBoolean(settings.get(settingName));
            }
            System.err.println("[ERROR] Setting \"" + settingName + "\" does not exist");
            System.exit(-1);
        }
        return Boolean.parseBoolean(settings.get(settingName));
    }

    public static Scenes getInitialScene(){
        return initialScene;
    }

    public static void setUpLogger(){
        if(USE_LOG_FILE) {
            File logger = new File("log.txt");
            try {
                if (!logger.exists()) {
                    logger.createNewFile();
                }
                PrintStream out = new PrintStream(logger);
                System.setOut(out);
                System.setErr(out);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static boolean settingExists(String settingName){
        return settings.get(settingName) != null;
    }

    public static boolean defaultSettingExists(String settingName){
        return defaultSettings.get(settingName) != null;
    }

}
