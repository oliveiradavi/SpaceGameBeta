package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Player extends GameObject {
    private Bitmap[] image;
    private Animation animation = new Animation();
    private int startPosition;
    private int velocity = 10;
    private boolean keyDown = false;
    private boolean playing = false;
    private boolean fire = false;
    private boolean up, down, left, right;

    public Player(Bitmap sprite) {
        image = new Bitmap[1];
        image[0] =  Bitmap.createBitmap(sprite, 0, 0, sprite.getWidth(), sprite.getHeight());


        up = false;
        down = false;
        left = false;
        right = false;

        startPosition = GamePanel.bgHeight/2 - sprite.getHeight()/2;
        x = 100;
        y = startPosition;
            height = sprite.getHeight();
            width = sprite.getWidth();

        animation.setFrames(image);
        animation.setDelay(100);
    }

    public void setUp(boolean b) {
        up = b;
    }

    public void setDown(boolean b) {
        down = b;
    }

    public void setLeft(boolean b) {
        left = b;
    }

    public void setRight(boolean b) {
        right  = b;
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

            if(up) {
                y-= velocity;
            } else if(down){
                y+= velocity;
            }

            if(left) {
                x -= velocity;
            } else if(right) {
                x += velocity;
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
