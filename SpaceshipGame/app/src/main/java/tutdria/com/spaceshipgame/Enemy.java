package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Enemy extends GameObject {
    Bitmap image;
    private int velocityX = 12;
    private int velocityY = 5;
    private final boolean up = true;
    private final boolean down = false;
    private boolean item = false;
    private boolean fire = false;
    private long createdTime;
    private boolean move;
    private long startTime;
    private long elapsedTime;
    private boolean timeToRun = false;
    private int initialY;
    private int lineX;

    public Enemy(Bitmap sprite, int y, int line) {

        createdTime = System.nanoTime()/1000000 + 1000;
        image = sprite;

        width = sprite.getWidth();
        height = sprite.getHeight();

        x = GamePanel.bgWidth;
        this.y = y;
        initialY = y;

        if(y>GamePanel.bgHeight/2) {
            move = up;
        } else{
            move = down;
        }

        switch (line) {
            case 0: {
                this.lineX = (int)(GamePanel.bgWidth*0.55);
                break;
            }

            case 1: {
                this.lineX = (int)(GamePanel.bgWidth*0.65);
                break;
            }

            case 2: {
                this.lineX = (int)(GamePanel.bgWidth*0.75);
                break;
            }

            case 3: {
                this.lineX = (int)(GamePanel.bgWidth*0.85);
                break;
            }
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
        initialY = y;

        if(y>GamePanel.bgHeight/2) {
            move = up;
        } else{
            move = down;
        }

        item = b;
        if(item) {
            lineX = (int)(GamePanel.bgWidth*0.55);
        }

        startTime = System.nanoTime();
    }

    public Enemy(Bitmap sprite, int y, int x, int line) {

        createdTime = System.nanoTime()/1000000 + 1000;
        image = sprite;

        width = sprite.getWidth();
        height = sprite.getHeight();

        this.x = x;
        this.y = y;
        initialY = y;

        switch (line) {
            case 0: {
                this.lineX = (int)(GamePanel.bgWidth*0.55);
                break;
            }

            case 1: {
                this.lineX = (int)(GamePanel.bgWidth*0.65);
                break;
            }

            case 2: {
                this.lineX = (int)(GamePanel.bgWidth*0.75);
                break;
            }

            case 3: {
                this.lineX = (int)(GamePanel.bgWidth*0.85);
                break;
            }
        }

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

        if (x < GamePanel.bgWidth) {
            elapsedTime = System.nanoTime() / 1000000 - startTime / 1000000;
            if (elapsedTime > 1500) {
                fire = true;
                startTime = System.nanoTime();
            }
        }

        if(item) {
            x-= velocityX;
        } else{
            if (x > lineX) {
                x -= velocityX;
            } else {
                if (move == up) {
                    y -= velocityY;
                } else {
                    y += velocityY;
                }

                if(!timeToRun) {
                    if (y < 0 || y > GamePanel.bgHeight - image.getHeight()) {
                        timeToRun = true;

                        if(move == up) {
                            move = down;
                        } else{
                            move = up;
                        }
                    }
                } else{
                    x-=velocityX;
                }
            }
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
    }
}
