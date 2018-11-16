package com.example.homay.kanvasanimate;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;


public class Board extends View {
    //TODO Animate stuff here
boolean flagTail ;
Random random ;
    boolean flagReleaseAnimation;
    Integer ranDefaultX = 0, ranDefaultY = 0;
    ArrayList<PointF> arrayListPointFs;
Path pathSnake;

    //Custom listener
    CustomCanvasTouchListener customCanvasTouchListener;
    Paint paint = new Paint();
    int i, sizeOfArray,j;

    private String TAG = "TESTER";
    Context context;
    Canvas canvasGlobal;
    Paint paintGlobal;
    float globalX, globalY;
    Path globalPath, getGlobalPathCopy;
    boolean flagDraw = true, flagRelease = false;
    float globalScreenHeight, globalScreenWidth;

    float startX, startY, endX, endY;
    private boolean flagRandom;

    public Board(Context context) {
        this(context, null);

    }

    public Board(Context context, AttributeSet attrs) {

        super(context, attrs);
        this.context = context;
        pathSnake = new Path();
        flagTail = false;
        canvasGlobal = new Canvas();
        paintGlobal = new Paint();
        i = 0;
        j = 0;
        arrayListPointFs = new ArrayList<>();
        sizeOfArray = 0;
        flagReleaseAnimation = false;
        globalPath = new Path();
        getGlobalPathCopy = new Path();
        Log.i("Screen height", "" + globalScreenHeight + globalScreenWidth);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        measureDimension();

        paintGlobal.setColor(Color.GREEN);
        paintGlobal.setAntiAlias(true);
        paintGlobal.setStrokeCap(Paint.Cap.ROUND);
        paintGlobal.setStrokeWidth(8);
        paintGlobal.setStyle(Paint.Style.STROKE);

        if (flagDraw) { //Flag is set to make "TRACE" button work; It erases the canvas


            canvas.drawPath(globalPath, paintGlobal); //This draws the path made by user
        }
        flagDraw = true; //set the flag to true, because the draw will be ignored forever otherwise


        setLayout(canvas); //This draws the start and end rectangles, and possibly  other static elements in the canvas

        if (flagRelease) { //This flag sets the RELEASE button, if true path is drawn, else ignored.
            reDrawGlobalPath(canvas); //This redraws the copied path
        }
        flagRelease = false; //release is set to false, else the path will be forever drawn, and who wants that? Geez !


        //animate
        if (flagReleaseAnimation) {


            paint.setStrokeWidth(15);
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.STROKE);

            canvas.drawPoint(arrayListPointFs.get(i).x, arrayListPointFs.get(i).y, paint);
            pathSnake.lineTo(arrayListPointFs.get(i).x, arrayListPointFs.get(i).y);
            canvas.drawPath(pathSnake,paint);

            if (flagTail) {
                canvas.drawPoint(arrayListPointFs.get(j).x, arrayListPointFs.get(j).y, paint);

            }

       /*     if (flagRandom) {

                Integer randomized = new Random().nextInt((3-(-2))+1)-2;
                Integer randomized2 = new Random().nextInt((3-(-2))+1)-2;

                canvas.drawPoint(ranDefaultX+ranDefaultY,ranDefaultY+ranDefaultX, paint);
           ranDefaultX+=randomized;
           ranDefaultY+=randomized2;
postInvalidateDelayed(20);
            }*/


            if (i < sizeOfArray - 2) { // set end points
                i++;
                j--;

           postInvalidateDelayed(10); // set time here

            } else {
                flagReleaseAnimation = false;
            }


        }
//text added

    }

    private void reDrawGlobalPath(Canvas canvas) { //This draws the copied path (redraw)
        Canvas canvasA = canvas;
        Paint painter = new Paint();
        painter.setColor(Color.RED);
        painter.setAntiAlias(true);
        painter.setStyle(Paint.Style.STROKE);
        canvas.drawPath(getGlobalPathCopy, painter);

    }

    private void setLayout(Canvas canvas) {  //This puts layout on the screen, coz otherwise it will be all white. Duh !
        Canvas canvasLayout = canvas;
        Paint paintStartEnd = new Paint();

        paintStartEnd.setStyle(Paint.Style.FILL);
        paintStartEnd.setAntiAlias(true);
        paintStartEnd.setColor(Color.RED);

        //this draws two rectangles on the left and right side of the canvas, taking account the height and width of the screen
        //the rectangles are drawn on the middle of the width, at either sides, +- 50dp
        canvasLayout.drawRect(0, (globalScreenWidth / 2) - 50, 50, (globalScreenHeight / 2) + 50, paintStartEnd);
        canvasLayout.drawRect(globalScreenWidth - 50, (globalScreenWidth / 2) - 50, globalScreenWidth, (globalScreenHeight / 2) + 50, paintStartEnd);


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //touch event happens here
        //start and end points, as startX, startY, endX, endY etc are declared to use them to check if trace path starts and ends from the desired area
        //the start and end coordinates are checked at checkStartEnd() function

        switch (event.getAction()) {
            case (MotionEvent.ACTION_DOWN):
                float x_acDown = event.getX();
                float y_acDown = event.getY();

                startX = x_acDown;
                startY = y_acDown;
                globalX = x_acDown;
                globalY = y_acDown;
                pathSnake.moveTo(globalX, globalY);
                globalPath.moveTo(globalX, globalY);
                invalidate();


                return true;

            case (MotionEvent.ACTION_MOVE):
                float x_acMove = event.getX();
                float y_acMove = event.getY();
                globalX = x_acMove;
                globalY = y_acMove;
                PointF pointF = new PointF(globalX, globalY);
                arrayListPointFs.add(pointF);
                PointF pointFCurrent = new PointF(x_acMove, y_acMove);
                Log.i(TAG, "Points" + pointFCurrent.toString() + pointFCurrent.toString());
                globalPath.lineTo(globalX, globalY);
                invalidate();
                fireCanvasTouchListener();

                return true;


            case (MotionEvent.ACTION_UP):
                float x_acUp = event.getX();
                float y_acUp = event.getY();
                endX = x_acUp;
                endY = y_acUp;
                getGlobalPathCopy.addPath(globalPath);
                globalX = x_acUp;
                globalY = y_acUp;

                //globalPath.moveTo(x_acUp, y_acUp);

                Boolean stat = checkStartEnd(startX, startY, endX, endY);

                return true;

        }
        return false;

    }

    public void reset() {
        //this resets the whole canvas triggered by Trace button
        flagRelease = false; //set to false, else the copied path will be drawn
        globalPath.reset();
        flagReleaseAnimation = false;
        i=0;
        j=0;
        pathSnake.reset();
        getGlobalPathCopy.reset();
        arrayListPointFs.clear();
        //TODO Check if Path.reset() gives nullPointerException, when drawn
        flagDraw = false; //draw is set to false, as flag isn't to be drawn *May not require, or may give nullPointerException //do check
        invalidate();

        requestLayout(); //no idea why this code is even here.
    }

    public void measureDimension() {
        //measures dimension required for drawing layouts
        globalScreenHeight = getMeasuredHeight();
        globalScreenWidth = getMeasuredWidth();
    }

    public boolean checkStartEnd(float startX, float startY, float endX, float endY) {
//        checks start and end; if they are within the start and end box

        float x1 = startX;
        float y1 = startY;
        float x2 = endX;
        float y2 = endY;

        //TODO The variables could have been more adaptive to future change
        //This long ass condition checks if the start and end points are within the rectangle. Too long, I know. Got it right on first try though. Genius, I know. You know too now.

        if (x1 > 0 && x1 < 50 && y1 > ((globalScreenHeight / 2) - 50) && y1 < (globalScreenHeight + 50) && x2 > (globalScreenWidth - 50) && x2 < globalScreenWidth && y2 > ((globalScreenHeight / 2) - 50) && y2 < ((globalScreenHeight / 2) + 50)) {
            Toast.makeText(context, "Right", Toast.LENGTH_SHORT).show();

            return true;
        }

        return false;

    }

    //To redraw or to not redraw the copied path

    //Triggered by RELEASE button
    public void releasePath() {

        flagReleaseAnimation = true;
        flagDraw = false;
        flagRelease = true;
        globalPath.reset();
        sizeOfArray = arrayListPointFs.size();
        j = sizeOfArray-2;
        //TODO Animate stuff here

//END OF ANIMATE STUFF
        invalidate();
    }


    private void animatePath(final Canvas canvas) {


    }


//end of animatePath


    //TODO Interface stuff here


    public void setCustomCanvasTouchListener(CustomCanvasTouchListener customCanvasTouchListener) {
        this.customCanvasTouchListener = customCanvasTouchListener;
    }

    public void fireCanvasTouchListener() {
        customCanvasTouchListener.onCanvasTouch(globalX, globalY);
    }


    public void setTailStat(boolean isChecked, boolean random) {
        flagRandom = random;
        flagTail = isChecked;

    }
}
