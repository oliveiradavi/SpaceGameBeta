package tutdria.com.spaceshipgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
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
    private ArrayList <EnemyLaser> enemyLasers;
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
    private boolean continueGame = true;
    private int enemiesRemoved;
    private long timeUntilNext;
    private int enemySwitch;
    private int numberOfEnemies;
    private int pointerIndex;
    private static float scaleFactorX;
    private static float scaleFactorY;
    private int laserType;
    private int lives;
    private boolean gameStarted;
    private boolean invencibility;
    private long invencibilityTime;

    public GamePanel(Context context) {
        super(context);
        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        //make gamePanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        gameStarted = false;
        initialize();

        thread =  new MainThread(getHolder(),this);
        if(!threadRunning) {
            if (thread.getState() == Thread.State.NEW) {
                thread.setRunning(true);
                thread.start();
            }
        }
    }

    public void initialize(){
        screenWidth = bgWidth;
        screenHeight = bgHeight;
        scaleFactorX = getWidth() / (bgWidth * 1.f);
        scaleFactorY = getHeight() / (bgHeight * 1.f);
        background = new Background(BitmapFactory.decodeResource(getResources(),R.drawable.blue));
        player = new Player(BitmapFactory.decodeResource(getResources(),R.drawable.player));
        fire = new Fire(BitmapFactory.decodeResource(getResources(),R.drawable.firespr));
        joystick = new Joystick(BitmapFactory.decodeResource(getResources(),R.drawable.joy2));
        meteors = new ArrayList<Meteor>();
        lasers = new ArrayList<Laser>();
        enemyLasers = new ArrayList<EnemyLaser>();
        enemies = new ArrayList<Enemy>();
        item = new Item(BitmapFactory.decodeResource(getResources(),R.drawable.powerup1));
        explosions = new ArrayList<Explosion>();
        laserType = 0;
        meteorsStartTime = System.nanoTime();
        laserStartTime = System.nanoTime();
        score = 0;
        timeUntilNext = 0;
        lives = 5;
        laserType = 0;
        enemiesRemoved = 0;
        enemySwitch = 0;
        pointerIndex = 0;
        enemiesAlive = false;
        created = false;
        stopMeteors = false;
        invencibility = false;
        player.setImage(BitmapFactory.decodeResource(getResources(),R.drawable.player));
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

                if(!gameStarted) {
                    gameStarted = true;
                }

                if(!player.getPlaying()) {
                    player.setPlaying(true);
                    lives = 5;
                    continueGame = true;
                    gameStartTime = System.nanoTime();
                }

                x[index] = (int) event.getX(index);
                y[index] = (int) event.getY(index);
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

            if(invencibility) {
                long elapsed = System.nanoTime()/1000000 - invencibilityTime;
                if(elapsed > 3000) {
                    invencibility = false;
                    player.setImage(BitmapFactory.decodeResource(getResources(),R.drawable.player));
                }
            }

            player.update();
            fire.update(player.getX() - fire.getWidth(), player.getY() + player.getHeight() / 2);
            updateMeteors();
            updateLasers();
            updateEnemyLasers();
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
                laserType = 1;
            }

            if(enemiesAlive) {
                updateEnemies();
            }

            if(still) {
                player.resetMove();
            }
        } else{
           // resetGame();
        }
    }

    private void updateEnemies() {

        if(player.getPlaying()) {
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
                        if (enemies.get(i).getY() < -50 ||  enemies.get(i).getY() > bgHeight + 50) {
                            enemies.remove(i);
                            enemiesRemoved++;
                        } else if(collision(enemies.get(i),player)) {
                            if(!invencibility) {
                                int enemyX = enemies.get(i).getX();
                                int enemyY = enemies.get(i).getY();
                                explosions.add(new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion2), enemyX - enemies.get(i).getWidth() / 2, enemyY - enemies.get(i).getHeight()/3));
                                enemies.remove(i);
                                enemiesRemoved++;
                                resetGame();
                                break;
                            }
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
                                explosions.add(new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion2), enemyX - enemies.get(i).getWidth() / 2, enemyY - enemies.get(i).getHeight()/3));

                                lasers.remove(j);
                                enemies.remove(i);

                                score += 100;
                                enemiesRemoved++;
                                break;
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
        }else {

            for(int i=0;i<enemies.size();i++){
                enemies.remove(i);
            }

            enemySwitch = 0;
            enemiesAlive = false;
            enemiesRemoved = 0;
            numberOfEnemies = 0;
            stopMeteors = true;
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

    private void updateEnemyLasers() {

        Bitmap image = BitmapFactory.decodeResource(getResources(),R.drawable.laserenemy);
        for(int i=0;i<enemies.size();i++) {
            if(enemies.get(i).getFire()) {
                enemyLasers.add(new EnemyLaser(image, enemies.get(i).getX() - enemies.get(i).getWidth()/2, enemies.get(i).getY() + enemies.get(i).getHeight()/2 - image.getHeight()/2));
                enemies.get(i).setFire(false);
            }
        }

        for(int i=0;i<enemyLasers.size();i++) {
            enemyLasers.get(i).update();

            if(collision(enemyLasers.get(i),player)) {
                if(!invencibility) {
                    enemyLasers.remove(i);
                    resetGame();
                    break;
                }
            }

            if(enemyLasers.get(i).getX() < -10) {
                enemyLasers.remove(i);
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
                        if(!invencibility) {
                            meteors.remove(i);
                            resetGame();
                            break;
                        }
                    }
                }
            } else {
                for (int i = 0; i < meteors.size(); i++) {
                    meteors.remove(i);
                }
            }

    }

    private void resetGame() {

        if(player.getPlaying()) {
            lives--;
        }

        if(lives < 1) {

            //removeEnemies()
            enemies.clear();

            //remove Lasers()
            lasers.clear();

            //remove Meteros()
            meteors.clear();

            //remove EnemyLasers(){
            enemyLasers.clear();

            item.setX(-50);

            initialize();
            continueGame = false;

        } else{
            player.resetPosition();
            player.resetMove();
            laserType = 0;
            invencibility = true;
            player.setImage(BitmapFactory.decodeResource(getResources(),R.drawable.playerinvencible));
            invencibilityTime = System.nanoTime()/1000000;
        }
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

            for(EnemyLaser el: enemyLasers) {
                el.draw(canvas);
            }

            joystick.draw(canvas);
            fpsDraw(canvas);
            scoreDraw(canvas);
            livesDraw(canvas);

            if(!gameStarted) {
                drawStartGame(canvas);
            }

            if(!continueGame) {
                drawContinue(canvas);
            }

            canvas.restoreToCount(savedState);
        }
    }

    private void fpsDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(60);
     //   canvas.drawText(Integer.toString(fps), GamePanel.bgWidth - 90, GamePanel.bgHeight - 60, paint);
    }

    private void scoreDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(40);
        paint.setTypeface(Typeface.create("Helvetica", Typeface.NORMAL));


        String formatted = String.format("%06d", score);

        canvas.drawText(formatted,  GamePanel.bgWidth - 140, 50, paint);
    }

    private void livesDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(35);
        paint.setTypeface(Typeface.create("Helvetica", Typeface.NORMAL));

        Bitmap image = BitmapFactory.decodeResource(getResources(),R.drawable.playerlife);
        canvas.drawBitmap(image, 20, 20,null);
        canvas.drawText("x 0" + Integer.toString(lives), 25 + image.getWidth(), 20 + image.getHeight(), paint);
    }

    private void drawContinue(Canvas canvas) {
        //TODO
        //Change for an actual Continue screen
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(50);

        canvas.drawText("Game Over!", bgWidth*scaleFactorX/3, bgHeight*scaleFactorY/2, paint);
        canvas.drawText("TOUCH to play again", bgWidth*scaleFactorX/3, bgHeight*scaleFactorY/2 + 50, paint);
    }

    private void drawStartGame(Canvas canvas) {
        //TODO
        //Change for an actual Start Game screen
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(50);
        canvas.drawText("TOUCH to start", bgWidth * scaleFactorX / 4, bgHeight * scaleFactorY / 2, paint);
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

                if(laserType == 0) {
                    if(elapsed > 200){
                        lasers.add(new Laser(BitmapFactory.decodeResource(getResources(), R.drawable.laser),
                                player.getX()+player.getWidth(),player.getY()+player.getHeight()/2));
                        laserStartTime = System.nanoTime();
                    }
                } else {
                    if(elapsed > 150){
                        int halfOfTheHeightOfTheLaserSprite = 23;
                        lasers.add(new Laser(BitmapFactory.decodeResource(getResources(), R.drawable.laser2),
                                player.getX()+player.getWidth(),player.getY()+player.getHeight()/2 - halfOfTheHeightOfTheLaserSprite));
                        laserStartTime = System.nanoTime();
                    }
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
