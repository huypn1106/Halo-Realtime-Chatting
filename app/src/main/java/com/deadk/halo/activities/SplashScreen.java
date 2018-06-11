package com.deadk.halo.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.deadk.halo.R;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SplashScreen extends AppCompatActivity {


    @BindView(R.id.prg_loading)
    ProgressBar progressBar;
    @BindView(R.id.img_logo)
    ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ButterKnife.bind(this);

        setAnimation();
    }


    void setAnimation(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        imgLogo.setY(imgLogo.getY() + height/10);

        Drawable dw = getResources().getDrawable(R.drawable.color_blue_gradient);
        progressBar.getProgressDrawable().setColorFilter(
                getResources().getColor(R.color.general), android.graphics.PorterDuff.Mode.SRC_IN);

        loadingProgress.execute();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                    Intent mainIntent = new Intent(SplashScreen.this,MainScreen.class);
                    startActivity(mainIntent);
                }
                else{
                    Intent startupIntent = new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(startupIntent);
                }
            }
        }, 300);

    }


    private AsyncTask<Void,Integer,Void> loadingProgress = new AsyncTask<Void, Integer, Void>() {
        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i <= 100; i++) {
                SystemClock.sleep(10);

                if(i==70){

                }
                //khi gọi hàm này thì onProgressUpdate sẽ thực thi
                publishProgress(i);
            }




            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //Hàm thực hiện update giao diện khi có dữ liệu từ hàm doInBackground gửi xuống
            super.onProgressUpdate(values);
            int number = values[0];
            //tăng giá trị của Progressbar lên
            progressBar.setProgress(number);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

//            progressBar.setVisibility(View.GONE);
//            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
//                Intent mainIntent = new Intent(SplashScreen.this,MainScreen.class);
//                startActivity(mainIntent);
//            }
//            else{
//                Intent startupIntent = new Intent(SplashScreen.this,MainActivity.class);
//                startActivity(startupIntent);
//            }

            finish();
        }
    };
}
