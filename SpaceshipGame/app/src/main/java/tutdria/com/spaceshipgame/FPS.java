package tutdria.com.spaceshipgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class FPS extends GameObject {

    private int fps;

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(80);
        canvas.drawText("" + getFPS(), GamePanel.bgWidth-90, 80, paint);
    }

    public void setFPS(int fps) {
        this.fps = fps;
    }

    public int getFPS() {
        return fps;
    }
}