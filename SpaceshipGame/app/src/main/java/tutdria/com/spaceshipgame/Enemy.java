package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Enemy extends GameObject {
    Bitmap image;
    private int velocity = 8;
    private boolean item = false;
    private boolean fire = false;
    private long createdTime;

    public Enemy(Bitmap sprite, int y) {

        createdTime = System.nanoTime()/1000000 + 500;
        image = sprite;

        width = sprite.getWidth();
        height = sprite.getHeight();

        x = GamePanel.bgWidth;
        this.y = y;
    }

    public Enemy(Bitmap sprite, int y, boolean b) {

        createdTime = System.nanoTime()/1000000 + 1000;
        image = sprite;

        width = sprite.getWidth();
        height = sprite.getHeight();

        x = GamePanel.bgWidth;
        this.y = y;

        item = b;
    }

    public Enemy(Bitmap sprite, int y, int x) {

        createdTime = System.nanoTime()/1000000 + 1000;
        image = sprite;

        width = sprite.getWidth();
        height = sprite.getHeight();

        this.x = x;
        this.y = y;
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
        x-= velocity;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
    }
}
