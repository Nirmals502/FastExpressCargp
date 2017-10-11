package mobile.fastexpresscargp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Service_handler.SERVER;
import Service_handler.ServiceHandler;

public class info_screen extends AppCompatActivity {
    Button Btn_enter_new, Btn_view_deliver, bTN_cHANGE_PASSWORD, Btn_logout, Upload_packet;
    private ProgressDialog pDialog;
    String Access_tocken, Device_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_screen);
        Btn_enter_new = (Button) findViewById(R.id.button3);
        Btn_view_deliver = (Button) findViewById(R.id.button);
        bTN_cHANGE_PASSWORD = (Button) findViewById(R.id.button6);
        Btn_logout = (Button) findViewById(R.id.button7);
        Upload_packet = (Button) findViewById(R.id.button2);
        SharedPreferences shared = getSharedPreferences("Fast_express", MODE_PRIVATE);
        Access_tocken = (shared.getString("Acess_tocken", "nodata"));
        Device_id = (shared.getString("device_id", "nodata"));

        Btn_enter_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(info_screen.this, Submission_screen.class);
                startActivity(i);

                // finish();

                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);

            }
        });
        Btn_view_deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(info_screen.this, History_screen.class);
                startActivity(i);

                // finish();

                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);
            }
        });
        Upload_packet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(info_screen.this, Upload_Delivery_courier_info.class);
                startActivity(i);

                // finish();

                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);
            }
        });
        bTN_cHANGE_PASSWORD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(info_screen.this, Change_password.class);
                startActivity(i);

                // finish();

                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);
            }
        });
        Btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = getSharedPreferences("Fast_express", MODE_PRIVATE);
                settings.edit().clear().commit();
                new Logout().execute();
            }
        });
    }

    private class Logout extends AsyncTask<Void, String, Void> implements DialogInterface.OnCancelListener {


        JSONObject jsonnode, json_User;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // mProgressHUD = ProgressHUD.show(Login_Screen.this, "Connecting", true, true, this);
            // Showing progress dialog
            pDialog = new ProgressDialog(info_screen.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            publishProgress("Please wait...");


            ServiceHandler sh = new ServiceHandler();
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            //nameValuePairs.add(new BasicNameValuePair("email", email_fb));


            String jsonStr = sh.makeServiceCall_withHeader(SERVER.LOGOUT,
                    ServiceHandler.POST, nameValuePairs, Access_tocken, Device_id);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(jsonStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Getting JSON Array node
                // JSONArray array1 = null;


            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            pDialog.dismiss();

            Intent i = new Intent(info_screen.this, Login_screen.class);
            startActivity(i);

            finish();

            overridePendingTransition(R.anim.slide_in_left,
                    R.anim.slide_out_left);

        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }
}
