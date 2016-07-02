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
    private ArrayList <Enemy> enemies;
    private ArrayList <Explosion> explosions;
    private Item item;
    private long meteorsStartTime;
    private long laserStartTime;
    private boolean threadRunning = false;
    boolean still = false;
    private long gameStartTime;
    private boolean enemiesAlive = false;
    private boolean created = false;
    private boolean stopMeteors = false;
    private int enemiesRemoved = 0;
    private long timeUntilNext;
    private int enemySwitch = 0;
    private int numberOfEnemies;
    private int pointerIndex = 0;
    private static float scaleFactorX;
    private static float scaleFactorY;

    public GamePanel(Context context) {
        super(context);
        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        //make gamePanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        screenWidth = bgWidth;
        screenHeight = bgHeight;
        scaleFactorX = getWidth() / (bgWidth * 1.f);
        scaleFactorY = getHeight() / (bgHeight * 1.f);
        background = new Background(BitmapFactory.decodeResource(getResources(),R.drawable.blue));
        player = new Player(BitmapFactory.decodeResource(getResources(),R.drawable.player));
        fire = new Fire(BitmapFactory.decodeResource(getResources(),R.drawable.firespr));
        joystick = new Joystick(BitmapFactory.decodeResource(getResources(),R.drawable.joy));
        meteors = new ArrayList<Meteor>();
        lasers = new ArrayList<Laser>();
        enemies = new ArrayList<Enemy>();
        item = new Item(BitmapFactory.decodeResource(getResources(),R.drawable.powerup1));
        explosions = new ArrayList<Explosion>();
        meteorsStartTime = System.nanoTime();
        laserStartTime = System.nanoTime();
        score = 0;
        timeUntilNext = 0;

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
                if(!player.getPlaying()) {
                    gameStartTime = System.nanoTime();
                }
                player.setPlaying(true);

                buttonPressed(index);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                x[index] = (int) event.getX(index);
                y[index] = (int) event.getY(index);
                buttonPressed(index);

                try {
                    x[pointerIndex] = (int) event.getX(pointerIndex);
                    y[pointerIndex] = (int) event.getY(pointerIndex);
                    buttonPressed(pointerIndex);
                } catch(Exception e){}

                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                x[index] = (int)event.getX(index);
                y[index] = (int) event.getY(index);
                pointerIndex = index;
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
        }
        return true;
    }

    public void update() {
        if(player.getPlaying()){
            if(!enemiesAlive) {
                long timeToCreateEnemies = (System.nanoTime() - gameStartTime)/1000000;
                if(timeToCreateEnemies > timeUntilNext) {
                    stopMeteors = true;

                    if(timeToCreateEnemies > timeUntilNext + 1000) {
                        enemiesAlive = true;
                    }
                }
            }

            background.update();
            player.update();
            fire.update(player.getX() - fire.getWidth(), player.getY() + player.getHeight() / 2);
            updateMeteors();
            updateLasers();
            item.update();

            for(int i=0;i<explosions.size();i++) {
                if(explosions.get(i).getPlayed()) {
                    explosions.remove(i);
                } else {
                    explosions.get(i).update();
                }
            }

            if(collision(player,item)) {
                item.setX(-50);
            }

            if(enemiesAlive) {
                updateEnemies();
            }

            if(still) {
                player.resetMove();
            }
        } else{
            resetGame();
        }
    }

    private void updateEnemies() {

        if(!created) {
            int totalY = bgHeight;
            switch (enemySwitch) {
                case 0: {
                    numberOfEnemies = 4;
                    enemySwitch++;
                    created = true;
                    enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(),R.drawable.enemy),(int)(30.5*scaleFactorY*2)));
                    enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(),R.drawable.enemy),(int)(30.5*scaleFactorY*4)));
                    enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(),R.drawable.enemy),totalY - (int)(30.5*scaleFactorY*5)));
                    enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(),R.drawable.enemy),totalY - (int)(30.5*scaleFactorY*3)));
                    timeUntilNext = 0;
                    break;
                }

                case 1: {
                    numberOfEnemies = 3;
                    enemySwitch++;
                    created = true;
                    enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(),R.drawable.enemy),(int)(30.5*scaleFactorY*1.5)));
                    enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(),R.drawable.enemy2),totalY/2 - (int)(30.5*scaleFactorY/1.5),true));
                    enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(),R.drawable.enemy),totalY - (int)(30.5*scaleFactorY*2.5)));
                    timeUntilNext = 0;
                    break;
                }

                case 2: {
                    numberOfEnemies = 8;
                    enemySwitch = 0;
                    created = true;
                    enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(),R.drawable.enemy),(int)(30.5*scaleFactorY*1.5),bgWidth + 65*0));
                    enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(),R.drawable.enemy),(int)(30.5*scaleFactorY*1.5),bgWidth + 65*1));
                    enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(),R.drawable.enemy),(int)(30.5*scaleFactorY*1.5),bgWidth + 65*2));
                    enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(),R.drawable.enemy),(int)(30.5*scaleFactorY*1.5),bgWidth + 65*3));

                    enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(),R.drawable.enemy),totalY - (int)(30.5*scaleFactorY*2.5),bgWidth + 65*0));
                    enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(),R.drawable.enemy),totalY - (int)(30.5*scaleFactorY*2.5),bgWidth + 65*1));
                    enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(),R.drawable.enemy),totalY - (int)(30.5*scaleFactorY*2.5),bgWidth + 65*2));
                    enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(),R.drawable.enemy),totalY - (int)(30.5*scaleFactorY*2.5),bgWidth + 65*3));

                    timeUntilNext = 10000;
                    break;
                }
            }
        }

        if(created) {
            if(player.getPlaying()) {

                for (int i = 0; i < enemies.size(); i++) {

                    enemies.get(i).update();
                    if (enemies.get(i).getX() < -enemies.get(i).getWidth() - 50) {
                        enemies.remove(i);
                        enemiesRemoved++;
                    }
                    else if(collision(enemies.get(i),player)) {
                        enemies.remove(i);
                        enemiesRemoved++;
                        //resetGame();
                    }
                }

                for(int i=0;i<enemies.size();i++) {
                    for(int j=0;j< lasers.size();j++) {
                        if(collision(lasers.get(j),enemies.get(i))) {
                            int enemyX = enemies.get(i).getX();
                            int enemyY = enemies.get(i).getY();

                            if(enemies.get(i).getItem()) {
                                item.setX(enemyX);
                                item.setY(enemyY);
                            }
                            explosions.add(new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion2), enemyX - enemies.get(i).getWidth()/3, enemyY - enemies.get(i).getHeight()/3));

                            lasers.remove(j);
                            enemies.remove(i);

                            score += 100;
                            enemiesRemoved++;
                        }
                    }
                }

            } else {
                for (int i = 0; i < enemies.size(); i++) {
                    enemies.remove(i);
                    enemiesRemoved++;
                }
            }
        }

        if(enemiesRemoved >= numberOfEnemies) {
            enemiesRemoved = 0;
            enemiesAlive = false;
            created = false;
            stopMeteors = false;
            gameStartTime = System.nanoTime();
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

            if (player.getPlaying()) {
                long elapsed = (System.nanoTime() - meteorsStartTime) / 1000000;
                Random random = new Random();

                int condition = random.nextInt(5) + 1;

                if(!stopMeteors) {
                    if (elapsed > random.nextInt(400) + 200) {
                        switch (condition) {
                            case 1: {
                                meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(), R.drawable.mt1), 50));
                                break;
                            }
                            case 2: {
                                meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(), R.drawable.mt2), 60));
                                break;
                            }
                            case 3: {
                                meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(), R.drawable.mt3), 70));
                                break;
                            }
                            case 4: {
                                meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(), R.drawable.mt4), 80));
                                break;
                            }
                            case 5: {
                                meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(), R.drawable.mt5), 90));
                                break;
                            }
                            case 6: {
                                meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(), R.drawable.mt6), 100));
                                break;
                            }
                            default: {
                                meteors.add(new Meteor(BitmapFactory.decodeResource(getResources(), R.drawable.mt1), 50));
                                break;
                            }
                        }
                        meteorsStartTime = System.nanoTime();
                    }
                }

                for (int i = 0; i < meteors.size(); i++) {
                    meteors.get(i).update();
                    if (meteors.get(i).getX() < -meteors.get(i).getWidth()) {
                        meteors.remove(i);
                    }
                }

                for (int i = 0; i < meteors.size(); i++) {
                    if (collision(meteors.get(i), player)) {
                        meteors.remove(i);
                        //resetGame();
                        break;
                    }
                }
            } else {
                for (int i = 0; i < meteors.size(); i++) {
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
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            background.draw(canvas);
            player.draw(canvas);
            fire.draw(canvas);
            item.draw(canvas);

            for(Enemy e: enemies) {
                e.draw(canvas);
            }

            for(Meteor m: meteors) {
                m.draw(canvas);
            }

            for(Laser l: lasers) {
                l.draw(canvas);
            }

            for(Explosion e: explosions) {
                e.draw(canvas);
            }

            joystick.draw(canvas);
            fpsDraw(canvas);
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

        int scaledX = (int) (joystick.getX()*scaleFactorX);
        int scaledY = (int) (joystick.getY()*scaleFactorY);
        int scaledWidth = (int)(joystick.getWidth()*scaleFactorX);
        int scaledHeight = (int) (joystick.getHeight()*scaleFactorY);

            if(x[n] > screenWidth*scaleFactorX/2){
                player.setFire(true);
                useLaser();
              //  System.out.println("Button Pressed!");
            } else {
                if(x[n] > scaledX && x[n] < scaledX+scaledWidth && y[n] > scaledY && y[n] < scaledY+scaledHeight) {
                 //   System.out.println("Joystick selected");
                    still = true;
                    if(x[n] > scaledX + (scaledWidth/1.5)) {
                      //  System.out.println("RIGHT Button");
                        player.setRight(true);
                        player.setLeft(false);
                        still = false;
                    } else if(x[n] < scaledX + (scaledWidth/3)) {
                     //   System.out.println("LEFT Button");
                        player.setLeft(true);
                        player.setRight(false);
                        still = false;
                    }

                    if(y[n] < scaledY + (scaledHeight/3)) {
                      //  System.out.println("UP Button");
                        player.setUp(true);
                        player.setDown(false);
                        still = false;
                    } else if(y[n] > scaledY + (scaledHeight/1.5)){
                        player.setDown(true);
                        player.setUp(false);
                        still = false;
                     //   System.out.println("DOWN Button");
                    }
                }
            }
    }

    private void useLaser() {
            if(player.getPlaying()) {
                long elapsed = (System.nanoTime() - laserStartTime)/1000000;
                if(elapsed > 350){
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
