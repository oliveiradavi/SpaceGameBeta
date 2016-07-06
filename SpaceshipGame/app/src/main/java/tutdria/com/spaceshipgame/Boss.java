package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Boss extends GameObject {
    Bitmap image;
    private int velocityX = 10;
    private int velocityY = 5;
    private int hp = 100;
    private boolean fire;
    private long startTime;
    private long elapsedTime;
    private int behavior = 0;
    private int fireRate;
    private boolean up = true;

    public Boss(Bitmap sprite) {

        image = sprite;
        width = image.getWidth();
        height = image.getHeight();

        x = GamePanel.bgWidth;
        y = GamePanel.bgHeight/2 - width/2;

        startTime = System.nanoTime() / 1000000;

    }

    public void receiveDamge (int dmg) {
        hp -= dmg;
        System.out.println("HP: "+hp);
    }

    public int getHP() {
        return hp;
    }

    public void update() {

        switch (behavior) {
            case 0: {
                if(x > GamePanel.bgWidth*0.8){
                    x-= velocityX;
                } else{
                    behavior++;
                }
                break;
            }

            case 1: {

                fireRate = 800;
                fireUpdate();

                if (hp<=75) {
                    behavior++;
                }

                break;
            }

            case 2: {

                fireRate = 500;
                velocityY = 7;
                fireUpdate();

                if(up) {
                    y -= velocityY;
                } else {
                    y+= velocityY;
                }

                if(y < -width/2) {
                    up = false;
                }

                if(y > GamePanel.bgHeight - height/2) {
                    up = true;
                }

                if(hp <=35) {
                    behavior++;
                }

                break;
            }

            case 3: {

                fireRate = 300;
                fireUpdate();

                if(up) {
                    y -= velocityY;
                } else {
                    y+= velocityY;
                }

                if(y < 0) {
                    up = false;
                }

                if(y > GamePanel.bgHeight - height) {
                    up = true;
                }
                break;
            }
        }
    }

    private void fireUpdate() {

        elapsedTime = System.nanoTime()/1000000 - startTime;

        if(elapsedTime > fireRate) {
            fire = true;
            startTime = System.nanoTime() / 1000000;
        }
    }

    public int getBehavior() {
        return behavior;
    }

    public void setFire(boolean b) {
        fire = b;
    }

    public boolean getFire() {
        return fire;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
    }
}
