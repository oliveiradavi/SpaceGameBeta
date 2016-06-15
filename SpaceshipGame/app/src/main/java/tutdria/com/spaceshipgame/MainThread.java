package tutdria.com.spaceshipgame;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread {

    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private FPScounter fpScounter = new FPScounter();
    private boolean running;
    public static Canvas canvas;
    private int nfps;

    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    @Override
    public void run() {
        while (running){
            fpScounter.StartCounter();
            canvas = null;
            try{
                canvas =  this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    this.gamePanel.update();
                    this.gamePanel.draw(canvas);
                }
            }catch (Exception e){
            }
            finally {
                if(canvas!=null){
                    try{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }catch(Exception e){
                    }
                }
            }
            nfps = FPScounter.StopAndPost();
            this.gamePanel.fps = nfps;
        }


    }
    
    public void setRunning(boolean running) {
        this.running = running;
    }
}

