package game.util;

import org.newdawn.slick.opengl.Texture;

public class Animation {

    private long animationTime, lifetime;
    private Texture[] frames;
    private int[] frameTimes;
    private int currentFrame;
    private boolean isOneTime, isDone;

    public Animation(Texture[] frames, int[] frameTimes) {
        if (frames.length == frameTimes.length) {
            this.frames = frames;
            this.frameTimes = frameTimes;
        } else {
            System.err.println("[ERROR] Not enough frames/times for animation");
        }
        currentFrame = 0;
    }

    public Animation(Texture[] frames, int frameTime) {
        this.frames = frames;
        frameTimes = new int[frames.length];
        for(int i = 0; i < frames.length; i++){
            frameTimes[i] = frameTime;
        }
        currentFrame = 0;
    }

    public Animation(Texture[] frames) {
        this.frames = frames;
        currentFrame = 0;
    }

    public void setOneTime(boolean isOneTime){
        this.isOneTime = isOneTime;
    }

    public void update(int delta){
        animationTime += delta;
        if(frameTimes != null) {
            if(!isDone) {
                if (frameTimes[currentFrame] < animationTime) {
                    currentFrame++;
                    if (currentFrame >= frameTimes.length) {
                        if(isOneTime){
                            isDone = true;
                            currentFrame = frames.length - 1;
                        } else {
                            currentFrame = 0;
                        }
                    }
                    animationTime = 0;
                }
            }
        }
        lifetime += delta;
    }

    public void setFrameTimes(int milli){
        for(int i = 0; i < frameTimes.length; i++){
            frameTimes[i] = milli;
        }
    }

    public void setFrame(int index){
        if(index <= frames.length - 1){
            if(currentFrame != index){
                animationTime = 0;
            }
            currentFrame = index;
        } else {
            System.err.println("[ERROR] Frame index out of bounds");
        }
    }

    public boolean isDone(){
        return isDone;
    }

    public Texture getCurrentFrame(){
        return frames[currentFrame];
    }

    public int getCurrentFrameTime(){
        return (int) animationTime;
    }

    public long getLifetime(){
        return lifetime;
    }
}
