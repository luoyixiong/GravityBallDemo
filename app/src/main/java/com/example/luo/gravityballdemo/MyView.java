package com.example.luo.gravityballdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Luo on 2016/9/4.
 */
public class MyView extends SurfaceView implements SensorEventListener,SurfaceHolder.Callback{
    //小球和背景图片
    private Bitmap ball,bg;
    //手机屏的像素长宽
    int width,height;
    //屏幕减去小球长宽后的像素长宽
    int realwidth,realheight;
    //小球在x，y轴上的坐标,速度
    float x,y,vx=0,vy=0;
    //
    float lastX,lastY;
    //x,y,z轴上的加速度
    float gx,gy,gz;
    //画布
    private Canvas mycanvas;
    //画笔
    private Paint mypaint;
    boolean running = true;

    private SurfaceHolder myviewHolder;
    private SensorManager mysensorManager;
    private Sensor sensor;

    public MyView(Context context, SensorManager sensorManager, int width, int height) {
        super(context);
        this.width =width;
        this.height = height;
        mysensorManager = sensorManager;
        //为MyView注册监听器
        sensor = mysensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mysensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_GAME);

        mycanvas = new Canvas();
        mypaint = new Paint();
        myviewHolder = this.getHolder();
        myviewHolder.addCallback(this);
        mypaint.setTextSize(50);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        gz =event.values[2];
        gx =event.values[0]-(float)1;
        gy =event.values[1]-(float)1.5;


        vx = vx - gx/2;
        vy = vy+ gy/2;


        x = x+vx/2;
        y = y+vy/2;
        if (x < 0){
            x = 0;
            if (lastX==x){
                vx=0;
            }else {
                vx = -vx*3/4;
            }
        }else if (x>realwidth){
            x = realwidth;
            if (lastX==x){
                vx=0;
            }else {
                vx = -vx*3/4;
            }
        }
        if (y<0){
            y = 0;
            if (lastY==y){
                vy=0;
            }else {
                vy = -vy*3/4;
            }
        }else if (y>realheight){
            y = realheight;
            if (lastY==y){
                vy=0;
            }else {
                vy = -vy*3/4;
            }
        }

        lastX = x;
        lastY = y;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        x = width /2;
        y = height/2;
        lastX = x;
        lastY = y;
        ball = BitmapFactory.decodeResource(this.getResources(),R.drawable.ball);
        bg = BitmapFactory.decodeResource(this.getResources(),R.drawable.back);
        realwidth = width - ball.getWidth();
        realheight = height - ball.getHeight();
        mythread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        running = false;
    }

    private Thread mythread = new Thread(){
        @Override
        public void run() {
            super.run();
            while (running){
                long starttime = System.currentTimeMillis();
                synchronized (myviewHolder) {
                    mycanvas = myviewHolder.lockCanvas();
                    draw();
                    myviewHolder.unlockCanvasAndPost(mycanvas);

                }
                long endtime = System.currentTimeMillis();
                int difftime =(int) (endtime - starttime);
                while (difftime<20){
                    difftime = (int)(System.currentTimeMillis()-starttime);
                    Thread.yield();
                }
            }
        }
    };

    private void draw(){
        mycanvas.drawBitmap(bg,0,0,mypaint);
        mycanvas.drawBitmap(ball,x,y,mypaint);
        mycanvas.drawText("x坐标："+x,0,50,mypaint);
        mycanvas.drawText("y坐标："+y,0,100,mypaint);
        mycanvas.drawText("x轴速度："+vx,0,150,mypaint);
        mycanvas.drawText("y轴速度："+vy,0,200,mypaint);
        mycanvas.drawText("x轴加速度："+gx,0,250,mypaint);
        mycanvas.drawText("y轴加速度："+gy,0,300,mypaint);
        mycanvas.drawText("z轴加速度："+gz,0,350,mypaint);
    }
}
