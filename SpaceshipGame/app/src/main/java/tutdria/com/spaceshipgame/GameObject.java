package tutdria.com.spaceshipgame;

import android.graphics.Rect;

public abstract class GameObject {
    protected int x,y,width,height;
    protected Rect rectangle;

    public void setX(int x) { this.x = x; };
    public void setY(int y) { this.y = y; }
    public void setWidth(int w ) { width = w; }
    public void setHeight(int h) { height = h; };
    public int getX() {
        return x;
    }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return  height; };

    public Rect getRectangle() {
        return new Rect(x, y, x+width, y+height);
    }
}
