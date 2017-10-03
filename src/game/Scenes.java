package game;

import game.scenes.*;

public enum Scenes {

    PAUSE_MENU(0, new PauseMenu()),
    SPRITE_TEST(1, new SpriteTest()),
    PHYSICS_TEST(2, new PhysicsTest()),
    VECTOR_TEST(3, new VectorTest());

    public final int ID;
    private Scene scene;

    Scenes(int id, Scene scene){
        this.ID = id;
        this.scene = scene;
    }

    public Scene getScene(){
        return scene;
    }

}
