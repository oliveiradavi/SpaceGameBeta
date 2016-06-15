package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Background {

    private Bitmap image;
    private int x, y;

    public Background(Bitmap res)
    {
        image = res;
    }
    public void update(){
        x -= 7;
        if(x<-GamePanel.bgWidth) {
            x=0;
        }
    }
    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y,null);
        if(x<0) {
            canvas.drawBitmap(image, x+GamePanel.bgWidth, y, null);
        }
    }
}
