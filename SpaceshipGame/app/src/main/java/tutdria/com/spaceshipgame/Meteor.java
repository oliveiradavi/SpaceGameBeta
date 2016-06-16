package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

public class Meteor extends GameObject {
    private Bitmap[] image;
    private Random rand;
    private Animation animation;
    private int score;

    public Meteor(Bitmap sprite, int score) {

        this.score = score;
        animation = new Animation();
        rand = new Random();
        image = new Bitmap[1];
        image[0] =  Bitmap.createBitmap(sprite, 0, 0, sprite.getWidth(), sprite.getHeight());

        width = image[0].getWidth();
        height = image[0].getHeight();

        x = GamePanel.screenWidth+width;
        y = rand.nextInt(GamePanel.screenHeight+height-height)+height;

        animation.setFrames(image);
        animation.setDelay(100);
    }

    public void update() {
        x -= 15;
        animation.update();
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(animation.getImage(),x,y,null);
    }

    public int getScore() {
        return score;
    }
}
