package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Player extends GameObject {
    private Bitmap[] image;
    private Animation animation = new Animation();
    private int startPosition;
    private int velocity = 13;
    private boolean keyDown = false;
    private boolean playing = false;
    private boolean fire = false;

    public Player(Bitmap sprite) {
        image = new Bitmap[1];
        image[0] =  Bitmap.createBitmap(sprite, 0, 0, sprite.getWidth(), sprite.getHeight());

        startPosition = GamePanel.bgHeight/2 - sprite.getHeight()/2;
        x = 100;
        y = startPosition;
            height = sprite.getHeight();
            width = sprite.getWidth();

        animation.setFrames(image);
        animation.setDelay(100);
    }

    public void setUp(boolean b) {
        keyDown = b;
    }

    public void setPlaying(boolean b) {
        playing = b;
    }

    public boolean getPlaying() {
        return playing;
    }

    public void setFire(boolean fire) {
        this.fire = fire;
    }

    public boolean getFire() {
        return fire;
    }


    public void update() {

        if(playing) {

            if(keyDown) {
                y-= velocity;
            } else {
                y+= velocity;
            }

            if(y < -200 || y > GamePanel.bgHeight + 200) {
                y = startPosition;
                playing = false;
            }

        } else{
            y = startPosition;
        }

        animation.update();
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(animation.getImage(),x,y,null);
    }
}
