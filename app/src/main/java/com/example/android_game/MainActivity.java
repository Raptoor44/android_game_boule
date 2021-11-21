package com.example.android_game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private ImageView brique;
    private RelativeLayout fenetrePrincipale;
    private List<ImageView> list_brique;

    private ImageView bande_noir;
    private GestureDetectorCompat gestureDetector;

    private ImageView boule;

    private float xx;
    private float yy = -5;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService retour = Executors.newScheduledThreadPool(1);

    private boolean game = true;


    //Rect

    private Rect rc1;
    private Rect rc2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //int variable :
        list_brique = new ArrayList<ImageView>();


        this.fenetrePrincipale = findViewById(R.id.fenetre_principale);

        this.gestureDetector = new GestureDetectorCompat(this, this);
        this.gestureDetector.setOnDoubleTapListener(this);

        this.init_bandenoir();


        //INIT THREAD
        scheduler.scheduleAtFixedRate(runnable, 30, 30, TimeUnit.MILLISECONDS);

        //INIT RECT
        rc1 = new Rect();
        rc2 = new Rect();

        int x;
        int y;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 5; j++) {
                x = i * 90;
                y = j * 90;

                this.creer_brique(x, y);
            }
        }
        this.init_boule();
    }

    private Runnable retour_boule = new Runnable() {
        @Override
        public void run() {
            boule.setY(boule.getY() - yy);
            boolean touche_bande_noir = false;
            touche_bande_noir = hitCheck_bande_noir(bande_noir);
            Log.e("TEst", "  " + boule.getX() + " + " + boule.getY());
            if (touche_bande_noir == true) {
                Log.e("GIOOOOOMMMMMMEEEE", "GIOOOOOMMMMMMEEEE");
                scheduler.scheduleAtFixedRate(runnable, 30, 30, TimeUnit.MILLISECONDS);
                scheduler.execute(runnable);
                retour.shutdown();
            }
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Insert custom code here

            // Repeat every 2 seconds


            boule.setY(boule.getY() + yy);
            for (ImageView brique_for : list_brique) {
                boolean hit_brique_boule;

                hit_brique_boule = hitCheck_brique(brique_for);

                if (hit_brique_boule == true) {


                    boule.setY(boule.getY() + 50);

                    retour.scheduleAtFixedRate(retour_boule, 30, 30, TimeUnit.MILLISECONDS);
                    retour.execute(retour_boule);

                    list_brique.remove(brique_for);
                    fenetrePrincipale.removeView(brique_for);

                    Log.e("co balles", "  " + boule.getX() + " + " + boule.getY());
                    Log.e("co de la brique", "  " + brique_for.getX() + " + " + brique_for.getY());
                    scheduler.shutdown();

                }



/*
                for (int i = (int) brique_for.getX(); i <= (int) brique_for.getX() + 30; i++) {
                    for (int j = (int) brique_for.getX(); j <= (int) brique_for.getX() + 30; j++) {
                        if (i == boule.getX() && j == boule.getY() ){
                            yy = 5;
                            Log.e("PAS TOUCHE", "touche ALERTE");
                        }else{
                            yy = -5;
                            Log.e("PAS TOUCHE", "");
                        }
                    }

                }

*/


            }

        }
    };


    private void init_boule() {
        this.boule = new ImageView(this);
        this.boule.setBackgroundResource(R.drawable.carre_bleu_ciel);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(50, 50);
        this.boule.setLayoutParams(params);

        this.boule.setX(500);
        this.boule.setY(1300);

        this.fenetrePrincipale.addView(boule);


    }

    private void init_bandenoir() {
        this.bande_noir = new ImageView(this);
        this.bande_noir.setBackgroundResource(R.drawable.bande_noir);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(100, 20);
        this.bande_noir.setLayoutParams(params);

        this.bande_noir.setX(500);
        this.bande_noir.setY(1500);

        this.fenetrePrincipale.addView(bande_noir);
    }


    private void creer_brique(int x, int y) {

        brique = new ImageView(this);
        brique.setBackgroundResource(R.drawable.brique);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(80, 80);
        brique.setLayoutParams(params);

        brique.setX(x);
        brique.setY(y);

        fenetrePrincipale.addView(brique);
        list_brique.add(brique);
    }

    public boolean hitCheck_brique(ImageView b) {


        int x1 = (int) b.getX();
        int width1 = b.getWidth();
        int x2 = (int) boule.getX();
        int width2 = boule.getWidth();
        int y1 = (int) b.getY();
        int height1 = b.getWidth();
        int y2 = (int) boule.getY();
        int height2 = boule.getHeight();


        int right1 = x1 + width1;
        int right2 = x2 + width2;
        int bottom1 = y1 + height1;
        int bottom2 = y2 + height2;

        // Check if top-left point is in box

        boolean isValid = false;
        if (right2 >= x1 && right2 <= right1 && bottom2 >= y2 && bottom2 <= bottom1) {

            isValid = true;
        }
        if (right2 >= x1 && right2 <= right1 && bottom2 >= y2 && bottom2 <= bottom1) {

            isValid = true;
        }


        // Check if bottom-right point is in box

        return isValid;
    }

    public boolean hitCheck_bande_noir(ImageView b) {

/*
        int x1 = (int) b.getX();
        int width1 = b.getWidth();
        int x2 = (int) boule.getX();
        int width2 = boule.getWidth();
        int y1 = (int) b.getY();
        int height1 = b.getWidth();
        int y2 = (int) boule.getY();
        int height2 = boule.getHeight();


        int right1 = x1 + width1;
        int right2 = x2 + width2;
        int bottom1 = y1 + height1;
        int bottom2 = y2 + height2;

        // Check if top-left point is in box

        boolean isValid = false;
        if (!(right2 >= x1 && right2 <= right1 && bottom2 >= y2 && bottom2 <= bottom1)) {

            isValid = true;
        }

*/
        boolean isValid = false;

        Log.e("Co de rc1  ", "left : " + rc1.left + " right  : " + rc1.right + " haut : " + rc1.top + " bas : " + rc1.bottom);
        Log.e("Co de rc2  ", "left : " + rc2.left + " right : " + rc2.right + " haut : " + rc2.top + " bas : " + rc2.bottom);

        b.getDrawingRect(rc1);

        boule.getDrawingRect(rc2);

        Log.e("Co de rc1  ", "left : " + rc1.left + " right  : " + rc1.right + " haut : " + rc1.top + " bas : " + rc1.bottom);
        Log.e("Co de rc2  ", "left : " + rc2.left + " right : " + rc2.right + " haut : " + rc2.top + " bas : " + rc2.bottom);
   /*

        if(Rect.intersects(rc1, rc2)){
                isValid = true;
        }

*/
        // Check if bottom-right point is in box

        return isValid;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        int x = 0;
        x = (int) e.getX();
        this.bande_noir.setX(x);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
