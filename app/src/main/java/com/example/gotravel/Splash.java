package com.example.gotravel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static java.lang.Thread.sleep;

public class Splash extends AppCompatActivity implements View.OnClickListener{

    ImageView imgLogo;
    Animation ani,ani2;
    private static int SPLASH_SCREEN_TIME_OUT=1000;
    private static ConnectivityManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imgLogo = findViewById(R.id.imgLogo);
        ani = AnimationUtils.loadAnimation(this,R.anim.face_in);
        imgLogo.setAnimation(ani);
        //imgLogo.setOnClickListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ani2 = AnimationUtils.loadAnimation(Splash.this,R.anim.fade_out);
                imgLogo.setAnimation(ani2);
                if(isOnline(Splash.this)){
                    if(!executeCmd("ping -c 1 -w 1 google.com", false).isEmpty()){
                        Intent act = new Intent(Splash.this,MainActivity.class);
                        startActivity(act);

                    }
                }else {
                    startActivity(new Intent(Splash.this,sinconexion.class));
                }

                finish();
                //the current activity will get finished.
            }
        }, SPLASH_SCREEN_TIME_OUT);

    }
    public static  String executeCmd(String cmd, boolean sudo){
        try{
            Process p;
            if(!sudo)
                p=Runtime.getRuntime().exec(cmd);
            else{
                p=Runtime.getRuntime().exec(new  String[]{"su", "-c", cmd});
            }
            BufferedReader stdInput=new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s;
            String res="";
            while((s=stdInput.readLine())!=null){
                res+=s+"\n";
            }
            p.destroy();
            return res;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected() ;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgLogo:

                if(isOnline(this)){
                    if(!executeCmd("ping -c 1 -w 1 google.com", false).isEmpty()){
                        Intent act = new Intent(this,MainActivity.class);
                        startActivity(act);
                    }
                }else {
                    Intent act = new Intent(this,sinconexion.class);
                    startActivity(act);
                }
                break;
        }
    }
}
