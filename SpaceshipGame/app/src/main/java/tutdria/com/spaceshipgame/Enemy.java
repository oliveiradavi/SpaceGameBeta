package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Enemy extends GameObject {
    Bitmap image;
    private int velocityX = 15;
    private int velocityY = 4;
    private final boolean up = true;
    private final boolean down = false;
    private boolean item = false;
    private boolean fire = false;
    private long createdTime;
    private boolean move;
    private int xDistance = 0;
    private long startTime;
    private long elapsedTime;
    private boolean timeToDie = false;

    public Enemy(Bitmap sprite, int y) {

        createdTime = System.nanoTime()/1000000 + 1000;
        image = sprite;

        width = sprite.getWidth();
        height = sprite.getHeight();

        x = GamePanel.bgWidth;
        this.y = y;

        if(y>GamePanel.bgHeight/2) {
            move = up;
        } else{
            move = down;
        }

        startTime = System.nanoTime();
    }

    public Enemy(Bitmap sprite, int y, boolean b) {

        createdTime = System.nanoTime()/1000000 + 1000;
        image = sprite;

        width = sprite.getWidth();
        height = sprite.getHeight();

        x = GamePanel.bgWidth;
        this.y = y;

        if(y>GamePanel.bgHeight/2) {
            move = up;
        } else{
            move = down;
        }

        item = b;

        startTime = System.nanoTime();
    }

    public Enemy(Bitmap sprite, int y, int x) {

        createdTime = System.nanoTime()/1000000 + 1000;
        image = sprite;

        width = sprite.getWidth();
        height = sprite.getHeight();

        this.x = x;
        this.y = y;

        xDistance = x - GamePanel.bgWidth;

        if(y>GamePanel.bgHeight/2) {
            move = up;
        } else{
            move = down;
        }

        startTime = System.nanoTime();
    }

    public void setFire(boolean b) {
        fire = b;
    }

    public boolean getFire() {
        return fire;
    }

    public long getTime() {
        return createdTime;
    }

    public void resetTime() {
        createdTime = System.nanoTime()/1000000;
    }

    public boolean getItem() {
        return item;
    }

    public void update() {

        elapsedTime = System.nanoTime()/1000000 - startTime/1000000;
        if(elapsedTime > 1500) {
            fire = true;
            startTime = System.nanoTime();
        }

        if(GamePanel.bgWidth - xDistance > GamePanel.bgWidth-GamePanel.bgWidth/3) {
            x-= velocityX;
            xDistance += velocityX;
        }
        else{
            if(move == up) {
                y-=velocityY;
            } else{
                y+=velocityY;
            }

            if(!timeToDie) {
                if(y<0) {
                    move = down;
                    timeToDie = true;
                } else if(y>GamePanel.bgHeight) {
                    move = up;
                    timeToDie = true;
                }
            }
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
    }
}
