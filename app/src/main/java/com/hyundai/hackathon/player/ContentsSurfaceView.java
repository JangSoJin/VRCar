package com.hyundai.hackathon.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.hyundai.hackathon.R;
import com.hyundai.hackathon.contents.Item;
import com.hyundai.hackathon.contents.OnFinishSearchListener;
import com.hyundai.hackathon.contents.Searcher;

/**
 * Created by Cho on 2016-08-20.
 */
public class ContentsSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    private Bitmap mBack;
    private SurfaceHolder mHolder;
    private DrawThread mThread;

    static final String apikey = "77f135712f929cf7f0b012f6d867afc4";

    public ContentsSurfaceView (Context context){
        super(context);

        setZOrderOnTop(true);    // necessary
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        mBack = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker, options);
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

    public void contentsDraw(String query, final Context context){
        double latitude = 37.461084; // 위도
        double longitude = 126.723380; // 경도
        int radius = 10000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
        int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개

        Log.v("testSearch","prev1");
        Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
        searcher.searchKeyword(context, query, latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {
            @Override
            public void onSuccess(List<Item> itemList) {
                for(Item i:itemList){
                    Log.v("testSearch",i.title);
                }
            }

            @Override
            public void onFail() {
                Toast.makeText(context,"API_KEY의 제한 트래픽이 초과되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        mThread.start();
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

        public void run(){
            Canvas canvas;
            synchronized(mHolder){
                canvas = mHolder.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.TRANSPARENT);
                    canvas.drawBitmap(mBack, 200, 200, null);
                }

                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}

