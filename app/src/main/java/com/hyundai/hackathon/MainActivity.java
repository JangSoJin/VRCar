package com.hyundai.hackathon;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hyundai.hackathon.player.*;

//Client ID	4386311749415805529
//Client Secret	50dfef73da57949fa76a973cbe46aad4


public class MainActivity extends AppCompatActivity {
    private final String SAMPLE_VIDEO_PATH =
            "android.resource://com.hyundai.hackathon/raw/" + R.raw.sample;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 101;
    public TextView txtView;
    static final String apikey = "77f135712f929cf7f0b012f6d867afc4";
    ContentsSurfaceView contentsSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //Runtime 권한 확인 -> 앱 자체는 Video list 만을 필요로함 Read 권한만 요청
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission. -> 권한 요청
                // 권한 요청 성공시 이벤트 헨들러인 onRequestPermissionsResult에서 탭뷰 랜더링
                // 실패시 빈페이지
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            // 이미 권한이 있다면 실행

            ImageButton mainButton = (ImageButton)findViewById(R.id.mainButton);
            mainButton.setBackgroundResource(R.drawable.ic_main);

            mainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, Player360Activity.class);
                    i.putExtra("url", SAMPLE_VIDEO_PATH);
                    startActivity(i);

                }
            });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                  Intent i = new Intent(this, Player360Activity.class);
//                  i.putExtra("url", SAMPLE_VIDEO_PATH);
//                  startActivity(i);

                } else {
                    // 권한 거부
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                }
                return;
        }
    }
}
