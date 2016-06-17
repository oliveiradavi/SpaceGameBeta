package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Joystick extends GameObject {
    Bitmap image;

    public Joystick(Bitmap sprite) {
        image = sprite;

        x = 50;
        y = GamePanel.bgHeight - sprite.getHeight();
    }

    public void draw(Canvas canvas) {canvas.drawBitmap(image,x,y,null);}

}
