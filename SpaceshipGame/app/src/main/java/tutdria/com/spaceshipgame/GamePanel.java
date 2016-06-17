package tutdria.com.spaceshipgame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    public int score;
    private int x[] = new int [10];
    private int y[] = new int [10];
    private MainThread thread;
    private Background background;
    private Player player;
    private Fire fire;
    private Joystick joystick;
    private ArrayList<Meteor> meteors;
    private ArrayList <Laser> lasers;
    private long meteorsStartTime;
    private long laserStartTime;
    private boolean threadRunning = false;
    boolean still = false;

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
        player = new Player(BitmapFactory.decodeResource(getResources(),R.drawable.player));
        fire = new Fire(BitmapFactory.decodeResource(getResources(),R.drawable.firespr));
        joystick = new Joystick(BitmapFactory.decodeResource(getResources(),R.drawable.joy2));
        meteors = new ArrayList<Meteor>();
        lasers = new ArrayList<Laser>();
        meteorsStartTime = System.nanoTime();
        laserStartTime = System.nanoTime();
        score = 0;

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

        while(retry) {
            try{thread.setRunning(false);
                thread.join();
            }catch(InterruptedException e){e.printStackTrace();}
            retry = false;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        int index = event.getActionIndex();
        int action = event.getActionMasked();

        switch (action) {

            case MotionEvent.ACTION_DOWN: {
                x[index] = (int) event.getX(index);
                y[index] = (int) event.getY(index);
                player.setPlaying(true);
                buttonPressed(index);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                x[index] = (int) event.getX(index);
                y[index] = (int) event.getY(index);
                buttonPressed(index);
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                x[index] = (int)event.getX(index);
                y[index] = (int) event.getY(index);
                player.setPlaying(true);
                buttonPressed(index);
                break;
            }

            case MotionEvent.ACTION_UP: {
                player.setUp(false);
                player.setFire(false);
                player.setDown(false);
                player.setLeft(false);
                player.setRight(false);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
            // when order of touch and release is the same
                if (x[index] < screenWidth/2) {
                    player.setUp(false);
                    player.setDown(false);
                    player.setLeft(false);
                    player.setRight(false);
                }else{
                    player.setFire(false);
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_UP + ((1 << MotionEvent.ACTION_POINTER_INDEX_SHIFT)): {
                            // for any order of two pointers
                if(x[index] < screenWidth/2) {
                    player.setUp(false);
                    player.setDown(false);
                    player.setLeft(false);
                    player.setRight(false);
                }else{
                    player.setFire(false);
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_UP + ((2 << MotionEvent.ACTION_POINTER_INDEX_SHIFT)): {
                if(x[index] < screenWidth/2) {
                    player.setUp(false);
                    player.setDown(false);
                    player.setLeft(false);
                    player.setRight(false);
                }else{
                    player.setFire(false);
                }
                break;
            }
        }
        return true;
    }

    public void update() {

        if(player.getPlaying()){
            background.update();
            player.update();
            fire.update(player.getX() - fire.getWidth(),player.getY() + player.getHeight()/2);
            updateMeteors();
            updateLasers();
            if(still) {
                player.resetMove();
            }
        }
        else{
            resetGame();
        }
    }

    private void updateLasers() {

        if(player.getFire()) {
            useLaser();
        }
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
                        score+= meteors.get(j).getScore();
                        lasers.remove(i);
                        meteors.remove(j);

                        break;
                    }
                }
            }
        }
        else {
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
                        meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(),R.drawable.mt1),50));
                        break;
                    }
                    case 2: {
                        meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(),R.drawable.mt2),60));
                        break;
                    }
                    case 3: {
                        meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(),R.drawable.mt3),70));
                        break;
                    }
                    case 4: {
                        meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(),R.drawable.mt4),80));
                        break;
                    }
                    case 5: {
                        meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(),R.drawable.mt5),90));
                        break;
                    }
                    case 6: {
                        meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(),R.drawable.mt6),100));
                        break;
                    }
                    default: {
                        meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(),R.drawable.mt1),50));
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
                   // resetGame();
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
        player.setFire(false);
        score = 0;
        updateMeteors();
        updateLasers();
        fire.reset();
        player.resetMove();
        player.update();
    }

    private void pauseGame() {
        //TODO
        //It pauses the game, is does not unpause yet
        player.setPlaying(false);
        player.setFire(false);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        final float scaleFactorX = getWidth() / (bgWidth * 1.f);
        final float scaleFactorY = getHeight() / (bgHeight * 1.f);

        if (canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            background.draw(canvas);
            player.draw(canvas);
            fire.draw(canvas);

            for(Meteor m: meteors) {
                m.draw(canvas);
            }

            for(Laser l: lasers) {
                l.draw(canvas);
            }

            joystick.draw(canvas);
           // fpsDraw(canvas);
            scoreDraw(canvas);
            canvas.restoreToCount(savedState);
        }
    }

    private void fpsDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(60);
        canvas.drawText(Integer.toString(fps), GamePanel.bgWidth-90, 60, paint);
    }

    private void scoreDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(50);
        canvas.drawText("Score: " + Integer.toString(score), 30, 50, paint);
    }

    public void buttonPressed(int n) {
            System.out.println("My x is: "+x[n]);
        System.out.println("My y is: "+y[n]);
            if(x[n] > screenWidth/2){
                player.setFire(true);
                useLaser();
                //System.out.println("Button Pressed!");
            } else {
                if(x[n] > 70 && x[n] < 480 && y[n] > 630 && y[n] < 1070) {
                    System.out.println("Joystick selected");
                    still = true;
                    if(x[n] > 385) {
                        System.out.println("RIGHT Button");
                        player.setRight(true);
                        player.setLeft(false);
                        still = false;
                    } else if(x[n] < 175) {
                        System.out.println("LEFT Button");
                        player.setLeft(true);
                        player.setRight(false);
                        still = false;
                    }

                    if(y[n] < 745) {
                        System.out.println("UP Button");
                        player.setUp(true);
                        player.setDown(false);
                        still = false;
                    } else if(y[n] > 965){
                        player.setDown(true);
                        player.setUp(false);
                        still = false;
                        System.out.println("DOWN Button");
                    }
                }
            }
    }

    private void useLaser() {
            if(player.getPlaying()) {
                long elapsed = (System.nanoTime() - laserStartTime)/1000000;
                if(elapsed > 250) {
                    lasers.add(new Laser(BitmapFactory.decodeResource(getResources(), R.drawable.laser),
                            player.getX()+player.getWidth(),player.getY()+player.getHeight()/2));
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
