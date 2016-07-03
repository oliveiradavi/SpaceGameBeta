package tutdria.com.spaceshipgame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    private GamePanel gamePanel;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        gamePanel = new GamePanel(this);
      //setContentView(R.layout.layout);
        setContentView(gamePanel);
    }

    public void startGame(View v) {
        setContentView(gamePanel);
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("It stopped!!!!!");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("Restarting!!!!!!");
    }
}
