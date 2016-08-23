package com.hyundai.hackathon.player;

import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asha.vrlib.MDVRLibrary;
import com.hyundai.hackathon.R;
import com.hyundai.hackathon.contents.Item;
import com.hyundai.hackathon.util.NetworkModel;
import com.hyundai.hackathon.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Player360Activity extends AppCompatActivity implements
        MediaPlayer.OnPreparedListener ,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnErrorListener,
        SeekBar.OnSeekBarChangeListener,View.OnClickListener
{
    public final String TAG = "Player";

    //설정 변수
    private int UPDATETIME=30;

    MDVRLibrary mdvrLibrary;


    private String mSource;
    private MediaPlayer mediaPlayer;

    private Handler mUpdateHandler;


    //상태 변수들
    private boolean isPrepared;
    private boolean isPlayed=false;
    private boolean controlDisable=false;
    private int previousPos=0;
    private boolean  seekBarEnalbe = true;

    //뷰 변수들.
    private View ControlView;
    private ProgressBar progressBar;
    private SeekBar seekBar;
    private TextView btnRetry,txtPosition,txtDuration;
    private ImageButton btnPlayPause,btnSkipNext,btnRestart;

    //Contents Surface 변수
    private ContentsSurfaceView contentsSurfaceView;
    private ImageButton locationButton, cvButton;

    private int accumulateX = 0;
    private String value = "";

    private double latitude = 37.461084; // 위도
    private double longitude = 126.723380; // 경도


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀바 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//StatusBar제거

        setContentView(R.layout.activity_player360);

        FrameLayout contentsFrame = new FrameLayout(this);
        contentsFrame.setBackgroundColor(Color.TRANSPARENT);
        contentsSurfaceView = new ContentsSurfaceView(this.getApplicationContext());
        FrameLayout.LayoutParams surfaceViewParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );

        locationButton = new ImageButton(this);
        FrameLayout.LayoutParams locationButtonParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );

        locationButton.setBackgroundResource(R.drawable.ic_add_location_black_48dp);
        locationButtonParams.gravity = Gravity.RIGHT;
        locationButtonParams.setMargins( 0,70,30,0);

        cvButton = new ImageButton(this);
        FrameLayout.LayoutParams cvButtonParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );

        cvButton.setBackgroundResource(R.drawable.ic_ev_station_black_48dp);
        cvButtonParams.gravity = Gravity.RIGHT;
        cvButtonParams.setMargins( 0,70,240,0);

        contentsFrame.addView(locationButton, locationButtonParams);
        contentsFrame.addView(cvButton, cvButtonParams);
        contentsFrame.addView(contentsSurfaceView,surfaceViewParams);
        addContentView(contentsFrame,surfaceViewParams);

        locationButton.setOnClickListener(this);
        cvButton.setOnClickListener(this);

        UIinit();
        getMediaSource();
        //플레이어에 플레이할 주소를 삽입
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mdvrLibrary=createVRLibrary();
        mdvrLibrary.initY(10);
    }


    //액비티비 생명주기 관련

    @Override
    protected void onRestart() {
        super.onRestart();
        if(mediaPlayer==null || mdvrLibrary==null){
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isPlayed && mediaPlayer!=null){
            start();
            mediaPlayer.seekTo(previousPos);
            isPlayed=false;
        }
        if(mdvrLibrary!=null) mdvrLibrary.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isPlaying()) {
            isPlayed = true;
            previousPos=mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isPlaying()) isPlayed=true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null){
            release();
        }
        if(mdvrLibrary!=null)mdvrLibrary.onDestroy();
    }

    private void UIinit(){
        ControlView = findViewById(R.id.player_controls);
        //ControlView.setVisibility(View.INVISIBLE);

        progressBar = (ProgressBar)findViewById(R.id.player_progressbar);
        seekBar =(SeekBar)ControlView.findViewById(R.id.seeker);
        seekBar.setOnSeekBarChangeListener(this);

        btnRetry =(TextView)ControlView.findViewById(R.id.btnRetry);
        btnRetry.setVisibility(View.INVISIBLE);
        btnRetry.setOnClickListener(this);
        txtPosition=(TextView)ControlView.findViewById(R.id.position);
        txtDuration=(TextView)ControlView.findViewById(R.id.duration);
        txtDuration.setText("hi");

        btnPlayPause =(ImageButton)ControlView.findViewById(R.id.btnPlayPause);
        btnPlayPause.setOnClickListener(this);
        btnSkipNext=(ImageButton)ControlView.findViewById(R.id.btnSkipNext);
        btnSkipNext.setOnClickListener(this);
        btnRestart=(ImageButton)ControlView.findViewById(R.id.btnRestart);
        btnRestart.setOnClickListener(this);
    }

    //화면전환 이벤트
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        String s = contentsSurfaceView.clickReflex(event);
        if(s==null)return mdvrLibrary.handleTouchEvent(event) || super.onTouchEvent(event);
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final String[] content = s.split("/");
            alert.setTitle(content[0]);
            alert.setMessage(content[1]);


            alert.setPositiveButton("목적지", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    contentsSurfaceView.setDestFlag(true,null,accumulateX);
                }
            });
            alert.setNeutralButton("목적지 취소",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (!contentsSurfaceView.setDestFlag(false, content[0],accumulateX)) {
                                Toast.makeText(Player360Activity.this, "현재 목적지가 아닙니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            alert.setNegativeButton("닫기",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });

            alert.show();
        }
        return true;
    }

    //Set UI Events
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnRetry) {
        } else if (v.getId() == R.id.btnPlayPause) {
            if (isPlaying()) pause();
            else {
                hideControl();
                start();
            }
        } else if (v.getId() == R.id.btnSkipNext) {
            //TODO : long touch 처리 + seek 처리
        } else if (v.getId() == R.id.btnRestart) {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(0);
                if (!isPlaying()) mediaPlayer.start();
            }
        } else if (v == locationButton) { //view가 alert 이면 팝업실행 즉 버튼을 누르면 팝업창이 뜨는 조건
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("위치검색");
            alert.setMessage("키워드로 검색하세요.");

            // Set an EditText view to get user input
            final EditText input = new EditText(this);
            input.setGravity(Gravity.CENTER);
            alert.setView(input);

            alert.setPositiveButton("검색", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    value = input.getText().toString();
                    contentsSurfaceView.contentsDraw(value.toString(), accumulateX, getApplicationContext());
                    // Do something with value!
                }
            });


            alert.setNegativeButton("닫기",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });

            alert.show();
        }
        else if(v == cvButton) {
            NetworkModel.getInstance().getTest("https://hackathon-jangsojin.c9users.io", 0, 0, new NetworkModel.OnResultListener<String>() {
                @Override
                public void onSuccess(String result) {
                    List<Item> itemList = new ArrayList<Item>();
                    try {
                        Log.v("jsontest", result);
                        JSONObject reader = new JSONObject(result);
                        JSONObject channel = reader.getJSONObject("channel");
                        JSONArray objects = channel.getJSONArray("evItem");
                        for (int i = 0; i < objects.length(); i++) {
                            JSONObject object = objects.getJSONObject(i);
                            Item item = new Item();
                            item.title = object.getString("location");
                            item.category = object.getString("speed");
                            item.address = object.getString("address");
                            item.latitude = object.getDouble("longitude");
                            item.longitude = object.getDouble("latitude");
                            item.distance = (int)Util.calDistance(latitude,longitude,item.latitude,item.longitude);
                            if(item.distance<2000) itemList.add(item);
                            Log.v("jsontest", item.distance +" "+ item.latitude+ " " + item.longitude);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    contentsSurfaceView.evDraw(itemList, accumulateX, getApplicationContext());
                }

                @Override
                public void onError(int code) {

                }
            });
        }
    }

    //인텐트로 데이터 주소를 받아오는 함수
    private void getMediaSource(){
        try {
            mSource = getIntent().getExtras().getString("url");
            if(mSource==null){
                Log.e(TAG,"URI Null Error");
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "URI Loading Error");
            finish();
        }
    }

    //재생 관련 함수들
    private void prepare(){
        if(mSource==null || mediaPlayer==null ||isPrepared) return;
        mediaPlayer.prepareAsync();
    }
    private void start(){
        if(mediaPlayer==null) return;
        mediaPlayer.start();
        //TODO : 컨트롤뷰 상태 예외 추가
        if(ControlView!=null){
            hideControl();
        }
        if(mUpdateHandler==null){
            mUpdateHandler = new Handler();
        }
        mUpdateHandler.post(UpdateHandler);
        btnPlayPause.setImageResource(R.drawable.ic_pause);
    }
    private void pause(){
        if(mediaPlayer==null || !isPlaying()) return;
        mediaPlayer.pause();
        if(mUpdateHandler!=null) mUpdateHandler.removeCallbacks(UpdateHandler);
        btnPlayPause.setImageResource(R.drawable.ic_play_arrow);
    }


    private void reset(){
        if(mediaPlayer!=null){
            isPrepared=false;
            mediaPlayer.reset();
            isPrepared=false;
        }
    }

    private void release(){
        isPrepared=false;

        if(mediaPlayer!=null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if(mUpdateHandler!=null){
            mUpdateHandler.removeCallbacks(UpdateHandler);
            mUpdateHandler=null;
        }
    }


    private Boolean isPlaying(){
        return mediaPlayer!=null && mediaPlayer.isPlaying();
    }
    protected MDVRLibrary createVRLibrary() {
        return MDVRLibrary.with(this)
                .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
                .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_TOUCH)
                .asVideo(new MDVRLibrary.IOnSurfaceReadyCallback() {
                    @Override
                    public void onSurfaceReady(Surface surface) {
                        if(mediaPlayer==null) {
                            Toast.makeText(getApplicationContext(),"setSurface Error",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mediaPlayer.setSurface(surface);
                        try {
                            mediaPlayer.setDataSource(getApplicationContext(),Uri.parse(mSource));
                            prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                })
                .ifNotSupport(new MDVRLibrary.INotSupportCallback() {
                    @Override
                    public void onNotSupport(int mode) {
                        String tip = mode == MDVRLibrary.INTERACTIVE_MODE_MOTION
                                ? "onNotSupport:MOTION" : "onNotSupport:" + String.valueOf(mode);
//                        Toast.makeText(VideoPlayerActivity.this, tip, Toast.LENGTH_SHORT).show();
                    }
                })
                .pinchEnabled(true)
                .gesture(new MDVRLibrary.IGestureListener() {
                    @Override
                    public void onClick(MotionEvent e) {
//                        Toast.makeText(VideoPlayerActivity.this, "onClick!", Toast.LENGTH_SHORT).show();
                    }
                })
                .build(R.id.player_surface);
    }





    //PrepareAsync 의 콜백함수
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG,"onPrepared");
        progressBar.setVisibility(View.INVISIBLE);
        isPrepared=true;
        txtPosition.setText("0");
        txtDuration.setText(String.valueOf(mp.getDuration()));
        seekBar.setProgress(0);
        seekBar.setMax(mediaPlayer.getDuration());

        //컨트롤 뷰 숨기기
        controlDisable=false;
        ControlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(controlDisable) return;
                if(isControlViewShown()) hideControl();
                else showControl();
            }
        });
        ControlView.setClickable(true);

        start();
        onPreparedCallBack();
    }

    protected void onPreparedCallBack(){};

    //SeekBar Callback Start
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser && mediaPlayer!=null && seekBarEnalbe) {
            mediaPlayer.seekTo(progress);
        }
    }

    protected void setSeekBarEnalbe(Boolean state){
        seekBarEnalbe=state;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(mediaPlayer!=null){
            mediaPlayer.start();
        }
        onSeekBarChangeCallBack();
    }
    protected void onSeekBarChangeCallBack(){};
    //SeekBar Callback End



    private final Runnable UpdateHandler = new Runnable() {
        @Override
        public void run() {
            if(mUpdateHandler==null || !isPrepared || seekBar==null || mediaPlayer==null) return;

            int pos= mediaPlayer.getCurrentPosition();
            final int dur = mediaPlayer.getDuration();
            if(pos>dur) pos=dur;

            txtPosition.setText(String.valueOf(pos));
            txtDuration.setText(String.valueOf(dur));
            seekBar.setProgress(pos);
            seekBar.setMax(dur);

            playerEventHandler(pos,dur);

            if(mUpdateHandler!=null) mUpdateHandler.postDelayed(this,UPDATETIME);
        }
    };


    //콜백 핸들러 호출 시간 조정
    protected void setUpdatetime(int updatetime){
        this.UPDATETIME=updatetime;
    }

    protected void playerEventHandler(int pos, int dur){
        int prv = accumulateX;
        accumulateX = (int)mdvrLibrary.getDeltaX()%360;
        if(accumulateX<0) accumulateX += 360;
        if(prv!=accumulateX) contentsSurfaceView.moveSurface(accumulateX);
        Log.v("accumulate",""+accumulateX);
    };

    @Override
    public void onCompletion(MediaPlayer mp) {
        btnPlayPause.setImageResource(R.drawable.ic_play_arrow);
        if(mUpdateHandler!=null) mUpdateHandler.removeCallbacks(UpdateHandler);
        seekBar.setProgress(seekBar.getMax());
        showControl();
        onCompletionCallback();
    }
    public void onCompletionCallback(){};

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(TAG,"Buffering : "+percent);
        if(seekBar!=null){
            if(percent==100) seekBar.setSecondaryProgress(0);
            else seekBar.setSecondaryProgress(seekBar.getMax() * (percent/100));
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if(what==39) return false; //특정 삼성기기 에러 무시
        throw new RuntimeException(new Exception("Player Error"));
    }

    private boolean isControlViewShown(){
        return !controlDisable && ControlView!=null && ControlView.getAlpha()>.5f;
    }

    private void showControl(){
        //TODO : 예외처리 추가
        if(ControlView==null) return;
        ControlView.animate().cancel();
        ControlView.setAlpha(0f);
        ControlView.setVisibility(View.VISIBLE);
        ControlView.animate().alpha(1f)
                .setInterpolator(new DecelerateInterpolator())
                .start();

    }
    private void hideControl(){
        if(ControlView==null) return;
        ControlView.animate().cancel();
        ControlView.setAlpha(1f);
        ControlView.setVisibility(View.VISIBLE);
        ControlView.animate().alpha(0f)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

}

