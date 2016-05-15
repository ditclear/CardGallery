package vienan.app.cardgallery.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import vienan.app.cardgallery.R;

/**
 * Created by vienan on 2015/9/11.
 */
public class WelcomeActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.welcome);
        YoYo.with(Techniques.RotateIn).duration(1000).playOn(findViewById(R.id.rl_welcome));
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, TimeLineActivity.class));
                WelcomeActivity.this.finish();
            }
        }, 2000);
    }
}
