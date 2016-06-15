package tutdria.com.spaceshipgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class Laser extends GameObject {

    private Random rand = new Random();
    int r;
    private int velocity = 30;
    boolean active = false;

    public void update() {
         x+= velocity;
         width += velocity;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x,y,width,height,paint);
    }

    public void setActive(boolean b) {
        active = b;
    }
    public boolean getActive() {
        return active;
    }
    public void reset() { x = 0; y = 0; width = 0; height = 0; }



}
