package tutdria.com.spaceshipgame;


import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Item extends GameObject {

    Bitmap image;

    public Item(Bitmap sprite) {
        image = sprite;
        x = -50;
    }

    public void update() {

        if(x > 0) {
            x -= 6;
        } else{
            x = -50;
        }
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y, null);
    }
}
