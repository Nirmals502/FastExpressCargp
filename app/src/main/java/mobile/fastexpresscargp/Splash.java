package mobile.fastexpresscargp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    String Access_tocken = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences shared = getSharedPreferences("Fast_express", MODE_PRIVATE);
        Access_tocken = (shared.getString("Acess_tocken", "nodata"));
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            @Override
            public void run() {
                // This method wil be executed once the timer is over
                // Start your app main activity
                if (!Access_tocken.contentEquals("nodata")) {
                    // do some thing

                    Intent i1 = new Intent(Splash.this, info_screen.class);

                    startActivity(i1);

                    finish();

                    overridePendingTransition(R.anim.slide_in_left,
                            R.anim.slide_out_left);
                } else {
                    Intent i1 = new Intent(Splash.this, Login_screen.class);

                    startActivity(i1);

                    finish();

                    overridePendingTransition(R.anim.slide_in_left,
                            R.anim.slide_out_left);
                }

            }
        }, SPLASH_TIME_OUT);
    }

}
