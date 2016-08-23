package com.hyundai.hackathon.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hyundai.hackathon.R;
import com.hyundai.hackathon.contents.Item;
import com.hyundai.hackathon.contents.OnFinishSearchListener;
import com.hyundai.hackathon.contents.Searcher;
import com.hyundai.hackathon.util.Util;

/**
 * Created by Cho on 2016-08-20.
 */
public class ContentsSurfaceView extends SurfaceView implements
        SurfaceHolder.Callback{
    private Bitmap Bb;
    private Bitmap HBb;
    private Bitmap SBb;
    private Bitmap Yb;
    private Bitmap HYb;
    private Bitmap SYb;
    private Bitmap Ob;
    private Bitmap HOb;
    private Bitmap SOb;
    private Bitmap Sb;
    private Bitmap Mb;
    private Bitmap Hb;
    private SurfaceHolder mHolder;
    private DrawThread mThread;
    private ArrayList<Item> currentItem;
    private List<Item> AllItem;
    private int width;

    private double latitude = 37.461084; // 위도
    private double longitude = 126.723380; // 경도

    private int state = 0;
    private Item destination = null;
    private boolean destFlag = false;

    static final String apikey = "77f135712f929cf7f0b012f6d867afc4";

    public ContentsSurfaceView (Context context){
        super(context);

        Display dis = ((WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        width = dis.getWidth();
        currentItem = new ArrayList<Item>();
        Log.v("width",width+"");

        setZOrderOnTop(true);    // necessary
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bb = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_blue_marker_md, options);
        HBb = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_blue_marker_hd, options);
        SBb = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_blue_marker_sm, options);
        Yb = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_yellow_marker, options);
        HYb = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_yellow_marker_hd, options);
        SYb = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_yellow_marker_sm, options);
        Ob = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_orange_marker, options);
        HOb = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_orange_marker_hd, options);
        SOb = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_orange_marker_sm, options);
        Sb = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_marker_sm, options);
        Mb = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_marker_md, options);
        Hb = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_marker_hd, options);
        mHolder = getHolder();
        mHolder.setFormat(PixelFormat.TRANSPARENT);
        mHolder.addCallback(this);
    }


    public void surfaceCreated(SurfaceHolder holder){
        mThread = new DrawThread(mHolder);
    }

    public void surfaceDestroyed(SurfaceHolder holder){
        mThread.bExit = true;
        for (;;){
            try{
                mThread.join(); // Thread 종료 기다리기
                break;
            }
            catch (Exception e){;}
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
        if (mThread != null){
            mThread.SizeChange(width, height);
        }
    }


    public String clickReflex(MotionEvent e) {
        if(state==0) return null;
        if(e.getAction()==MotionEvent.ACTION_DOWN){
            float x = e.getX();
            float y = e.getY();
            for(Item i : currentItem){
                float upperx = i.x+120;
                float uppery = i.y+150;
                Log.v("what", upperx + " " + i.x + " " + uppery + " " + i.y + " " + x + " " + y);
                if(i.x<x && x<upperx && i.y<y && y<uppery){
                    if(!destFlag) destination=i;
                    if(state==1) {
                        return i.title+"/분류: "+i.category + "\n주소: "+i.address +
                                "\n연락처: "+i.phone + "\n거리: " +(int)i.distance + "m";
                    }
                    else if(state==2){
                        return i.title+"/분류: "+i.category + "\n주소: "+i.address +
                                "\n거리: " +(int)i.distance + "m";
                    }
                }
            }
        }
        return null;
    }

    public void moveSurface(int currentAngle){
        if(state == 0) return;
        if(currentItem!=null) currentItem.clear();
        for(Item i:AllItem){
            int angle = (int)Util.getAngle(latitude,longitude,i.latitude,i.longitude);

            if(angle<0) angle += 360;
            int upper = (currentAngle+60)%360;
            int lower = currentAngle-60;

            if(60>currentAngle){
                if(angle>=0 && upper>angle) {
                    i.angle = (angle - lower);
                    currentItem.add(i);
                }
                else if(angle>lower+360){
                    i.angle =(angle-lower-360);
                    currentItem.add(i);
                    Log.v("testSearch2",i.title + " " + currentAngle + " " + angle + " " +lower);
                }

            }

            else if(currentAngle>300){
                if(angle >=0 && upper>angle){
                    i.angle =(angle-lower+360);
                    currentItem.add(i);
                }
                else if(angle>lower){
                    i.angle =angle-lower;
                    Log.v("testSearch2",i.title + " " + currentAngle + " " + angle + " " +lower);
                    currentItem.add(i);
                }
           }

            else{
                if(lower<angle && upper>angle) {
                    i.angle = (angle-lower);
                    currentItem.add(i);
                }
            }

        }

        Collections.sort(currentItem);
        if(destination !=null){
            Item i = destination;
            int angle = (int)Util.getAngle(latitude,longitude,i.latitude,i.longitude);

            if(angle<0) angle += 360;
            int upper = (currentAngle+60)%360;
            int lower = currentAngle-60;

            if(60>currentAngle){
                if(angle>=0 && upper>angle) {
                    i.angle = (angle - lower);
                    currentItem.add(i);
                }
                else if(angle>lower+360){
                    i.angle =(angle-lower-360);
                    currentItem.add(i);
                    Log.v("testSearch2",i.title + " " + currentAngle + " " + angle + " " +lower);
                }

            }

            else if(currentAngle>300){
                if(angle >=0 && upper>angle){
                    i.angle =(angle-lower+360);
                    currentItem.add(i);
                }
                else if(angle>lower){
                    i.angle =angle-lower;
                    Log.v("testSearch2",i.title + " " + currentAngle + " " + angle + " " +lower);
                    currentItem.add(i);
                }
            }

            else{
                if(lower<angle && upper>angle) {
                    i.angle = (angle-lower);
                    currentItem.add(i);
                }
            }
        }
        Log.v("draw", currentItem.size()+" "+ AllItem.size() + " " + currentAngle);
        mThread.run(currentAngle);
    }


    public void contentsDraw(String query, final int currentAngle, final Context context){
        if(query.equals("")){
            Toast.makeText(context,"다시 입력하세요",Toast.LENGTH_SHORT).show();
            return;
        }
        state = 1;

        int radius = 500; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
        Log.v("radius",radius+"");
        int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개

        Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
        searcher.searchKeyword(context, query, latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {
            @Override
            public void onSuccess(List<Item> itemList) {
                if(AllItem!=null)AllItem.clear();
                AllItem = itemList;
                for(Item i : AllItem) Log.v("inputest",i.title);
                moveSurface(currentAngle);
            }

            @Override
            public void onFail() {
                Toast.makeText(context,"API_KEY의 제한 트래픽이 초과되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void evDraw(List<Item> evList,  final int currentAngle, Context context) {
        if (evList == null) {
            Toast.makeText(context, "다시 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        state = 2;
        if(AllItem!=null) AllItem.clear();
        AllItem = evList;
        Log.v("evDraw",AllItem.size()+"");
        moveSurface(currentAngle);
    }

    public boolean setDestFlag(boolean f,String s, int currentAngle){
        if(f) {
            destFlag = f;
            moveSurface(currentAngle);
            return true;
        }
        else {
            if(destination.title.equals(s)){
                destFlag = f;
                moveSurface(currentAngle);
                return true;
            }
        }
        return false;
    }

    class DrawThread extends Thread{
        boolean bExit;
        int mWidth, mHeight;
        SurfaceHolder mHolder;

        DrawThread(SurfaceHolder Holder){
            mHolder = Holder;
            bExit = false;
        }

        public void SizeChange(int Width, int Height){
            mWidth = Width;
            mHeight= Height;
        }

        public void run(int base){
            Canvas canvas;
            synchronized(mHolder){
                canvas = mHolder.lockCanvas();
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                if (canvas != null) {
                    Paint pnt = new Paint();
                    // 기본 문자열 출력. 안티 알리아싱을 적용했다.
                    pnt.setAntiAlias(true);
                    pnt.setColor(Color.BLACK);
                    pnt.setTextSize(60);

                    for (Item i : currentItem) {
                        i.x = width - ((float)i.angle/120)*width;

                        if(destFlag && i.latitude==destination.latitude && i.longitude ==destination.longitude){
                            if(i.distance <300){
                                i.y = 700;
                                canvas.drawBitmap(HBb,i.x,i.y,null);
                            }
                            else{
                                i.y = 800;
                                canvas.drawBitmap(Bb,i.x,i.y,null);
                            }
                        }

                        else if(state == 1){
                            if(i.distance <300){
                                i.y = 700;
                                canvas.drawBitmap(Hb,i.x,i.y,null);
                            }
                            else{
                                i.y = 800;
                                canvas.drawBitmap(Mb,i.x,i.y,null);
                            }

                        }
                        else if(state == 2) {
                            if(i.category.equals("완속")){
                                if(i.distance <300){
                                    i.y = 700;
                                    canvas.drawBitmap(HYb,i.x,i.y,null);
                                }
                                else{
                                    i.y = 800;
                                    canvas.drawBitmap(Yb,i.x,i.y,null);
                                }

                            }
                            else{
                                if(i.distance <300){
                                    i.y = 700;
                                    canvas.drawBitmap(HOb,i.x,i.y,null);
                                }
                                else{
                                    i.y = 800;
                                    canvas.drawBitmap(Ob,i.x,i.y,null);
                                }
                            }
                        }
                        canvas.drawText((int)i.distance+"m",i.x,i.y,pnt);
                    }
                }

                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}

