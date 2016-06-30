package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Player extends GameObject {
    private Bitmap image;
    private int startPositionX;
    private int startPositionY;
    private int velocityX = 14;
    private int velocityY = 11;
    private boolean playing = false;
    private boolean fire = false;
    private boolean up, down, left, right;

    public Player(Bitmap sprite) {

        image =  Bitmap.createBitmap(sprite, 0, 0, sprite.getWidth(), sprite.getHeight());

        up = false;
        down = false;
        left = false;
        right = false;

        startPositionX = 100;
        startPositionY = GamePanel.bgHeight/2 - sprite.getHeight()/2;
        x = startPositionX;
        y = startPositionY;
        height = sprite.getHeight();
        width = sprite.getWidth();

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
                    if(y > 0) {
                        y-= velocityY;
                    }
                } else if(down){
                    if(y < GamePanel.bgHeight - height){
                        y+= velocityY;
                    }
                }
                if(left) {
                    if(x > 100) {
                        x -= velocityX;
                    }
                }
                else if(right) {
                    if(x < GamePanel.bgWidth - width - 100) {
                        x += velocityX;
                    }
                }

        }
        else{
            x = startPositionX;
            y = startPositionY;
        }
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y, null);
    }

    public void resetMove() {
        up = false;
        down = false;
        left = false;
        right = false;
    }
}
