package com.example.itrs.prey;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by itrs on 3/5/2018.
 */

public class GameView extends SurfaceView implements Runnable {

    volatile boolean playing;
    private Thread gameThread = null;
    private Player player;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private ArrayList<Star> stars = new ArrayList<Star>();

    private Enemy[] enemies;

    private int enemyCount = 4;

    private Boom boom;
    public GameView(Context context, int ScreenX, int ScreenY) {
        super(context);

        player = new Player(context, ScreenX, ScreenY);
        surfaceHolder = getHolder();
        paint = new Paint();

        int starNums = 100;
        for (int i = 0; i < starNums; i++) {
            Star s = new Star(ScreenX, ScreenY);
            stars.add(s);
        }

        enemies = new Enemy[enemyCount];
        for (int i = 0; i < enemyCount; i++) {
            enemies[i] = new Enemy(context, ScreenX, ScreenY);
        }

        boom = new Boom(context);
    }
    public void run(){
        while (playing)
        {
            update();
            draw();
            control();
        }
    }

    public void update() {
        player.update();

        boom.setX(-250);
        boom.setY(-250);

        for (Star s : stars) {
            s.update(player.getSpeed());
        }

        for(int i=0; i<enemyCount; i++){
            enemies[i].update(player.getSpeed());

            //if collision occurrs with player
            if (Rect.intersects(player.getDetectCollision(), enemies[i].getDetectCollision()))
            {
                enemies[i].setX(-200);
            }
        }
    }
    public void draw()
    {
      if(surfaceHolder.getSurface().isValid())
      {
          canvas = surfaceHolder.lockCanvas();
          canvas.drawColor(Color.BLACK);

          paint.setColor(Color.WHITE);

          for (Star s : stars) {
              paint.setStrokeWidth(s.getStarWidth());
              canvas.drawPoint(s.getX(), s.getY(), paint);
          }

          canvas.drawBitmap(player.getBitmap(),player.getX(),player.getY(),paint);

          for (int i = 0; i < enemyCount; i++) {
              canvas.drawBitmap(enemies[i].getBitmap(), enemies[i].getX(), enemies[i].getY(), paint
              );

          }

          canvas.drawBitmap(
                  boom.getBitmap(),
                  boom.getX(),
                  boom.getY(),
                  paint
          );

          surfaceHolder.unlockCanvasAndPost(canvas);
      }
    }
    public void control()
    { try {
        gameThread.sleep(17);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    }

    public void pause() {
        playing = false;
        try {

            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                break;
        }
        return true;
    }

}
