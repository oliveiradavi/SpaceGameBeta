package tutdria.com.spaceshipgame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    public static final int bgWidth = 1280;
    public static final int bgHeight = 720;
    public static int screenWidth;
    public static int screenHeight;
    public int fps;
    private MainThread thread;
    private Background background;
    private FPS fpsObject;
    private Player player;
    private ArrayList<Meteor> meteors;
    private ArrayList <Laser> lasers;
    private long meteorsStartTime;
    private long laserStartTime;
    private Random rand = new Random();
    private int random = 0;
    private boolean threadRunning = false;
    private boolean fire = false;
    private int x[] = new int [10];

    public GamePanel(Context context) {
        super(context);

        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        //make gamePanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

            screenWidth = getWidth();
            screenHeight = getHeight();
            background = new Background(BitmapFactory.decodeResource(getResources(),R.drawable.space));
            fpsObject = new FPS();
            player = new Player(BitmapFactory.decodeResource(getResources(),R.drawable.player));
            meteors = new ArrayList<Meteor>();
            lasers = new ArrayList<Laser>();
            meteorsStartTime = System.nanoTime();
            laserStartTime = System.nanoTime();

        thread =  new MainThread(getHolder(),this);
        if(!threadRunning) {
            if (thread.getState() == Thread.State.NEW) {
                thread.setRunning(true);
                thread.start();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        boolean retry = true;

        while(retry)
        {
            try{thread.setRunning(false);
                thread.join();
            }catch(InterruptedException e){e.printStackTrace();}
            retry = false;
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);
        int action = event.getActionMasked();

        switch (action) {

            case MotionEvent.ACTION_DOWN: {
                x[0] = (int) event.getX();
                player.setPlaying(true);

                buttonPressed(x[0]);
                System.out.println("The index is: "+index);
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                x[index] = (int)event.getX(index);
                player.setPlaying(true);
                buttonPressed(x[index]);
                break;
            }

            case MotionEvent.ACTION_UP: {
                if(x[index] < screenWidth/2) {
                    player.setUp(false);
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
            // when order of touch and release is the same
                if(x[index] < screenWidth/2) {
                    player.setUp(false);
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP + ((1 << MotionEvent.ACTION_POINTER_INDEX_SHIFT)): {
                            // for any order of two pointers
                if(x[index] < screenWidth/2) {
                    player.setUp(false);
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP + ((2 << MotionEvent.ACTION_POINTER_INDEX_SHIFT)): {
                if(x[index] < screenWidth/2) {
                    player.setUp(false);
                }
                break;
            }

        }
        return true;
    }

    public void update() {

        if(player.getPlaying()){
            background.update();
        }
        fpsObject.setFPS(fps);
        player.update();
        updateMeteors();
        updateLasers();
    }

    private void updateLasers() {
        if(player.getPlaying()) {
            for(int i = 0; i<lasers.size();i++) {
                lasers.get(i).update();
                if(lasers.get(i).getX() > screenWidth+lasers.get(i).getWidth()) {
                    lasers.remove(i);
                }
            }

            for(int i=0;i<lasers.size();i++) {
                for(int j=0;j<meteors.size();j++) {
                    if(collision(lasers.get(i),meteors.get(j))) {
                        lasers.remove(i);
                        meteors.remove(j);
                        break;
                    }
                }
            }
        }
        else{
            for(int i=0;i<lasers.size();i++) {
                lasers.remove(i);
            }
        }
    }

    private void updateMeteors() {
        if(player.getPlaying()) {
            long elapsed = (System.nanoTime() - meteorsStartTime)/1000000;
            Random random = new Random();

            int condition = random.nextInt(5)+1;

            if(elapsed > random.nextInt(400)+200){
                switch (condition) {
                    case 1:{
                        meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(),R.drawable.mt6)));
                        break;
                    }
                    case 2: {
                        meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(),R.drawable.mt2)));
                        break;
                    }
                    case 3: {
                        meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(),R.drawable.mt3)));
                        break;
                    }
                    case 4: {
                        meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(),R.drawable.mt4)));
                        break;
                    }
                    case 5: {
                        meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(),R.drawable.mt5)));
                        break;
                    }
                    case 6: {
                        meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(),R.drawable.mt6)));
                        break;
                    }
                    default: {
                        meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(),R.drawable.mt1)));
                        break;
                    }
                }
                meteorsStartTime = System.nanoTime();
            }

            for(int i = 0; i<meteors.size();i++) {
                meteors.get(i).update();
                if(meteors.get(i).getX()<-meteors.get(i).getWidth()) {
                    meteors.remove(i);
                }
            }

            for(int i = 0; i <meteors.size(); i++) {
                if(collision(meteors.get(i),player)) {
                    meteors.remove(i);
                    resetGame();
                    break;
                }
            }
        } else {
            for(int i=0; i<meteors.size();i++) {
                meteors.remove(i);
            }
        }
    }

    private void resetGame() {
        player.setPlaying(false);
        fire = false;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void draw(Canvas canvas) {

        final float scaleFactorX = getWidth() / (bgWidth * 1.f);
        final float scaleFactorY = getHeight() / (bgHeight * 1.f);

        if (canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            background.draw(canvas);
            player.draw(canvas);

            for(Meteor m: meteors) {
                m.draw(canvas);
            }

            for(Laser l: lasers) {
                l.draw(canvas);
            }


            fpsObject.draw(canvas);
            canvas.restoreToCount(savedState);
        }
    }

    public void buttonPressed(int n) {
            if(n > screenWidth/2){
                useLaser();
                //System.out.println("Button Pressed!");
            } else {
                player.setUp(true);
            }
    }

    private void useLaser() {
        fire = true;
        if(player.getPlaying()) {
            long elapsed = (System.nanoTime() - laserStartTime)/1000000;
            if(elapsed > 300) {
                lasers.add(new Laser(BitmapFactory.decodeResource(getResources(),R.drawable.laser),
                        player.getX()+player.getWidth()/2,player.getY()+player.getHeight()/2));
                laserStartTime = System.nanoTime();
            }
        }
    }

    public boolean collision(GameObject a, GameObject b) {
        if(Rect.intersects(a.getRectangle(), b.getRectangle())) {
            return true;
        }
        return false;
    }
}
