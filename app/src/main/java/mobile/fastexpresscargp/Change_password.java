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
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Multipart_enttity.Utility;
import Service_handler.SERVER;
import Service_handler.ServiceHandler;

public class Change_password extends AppCompatActivity {
    Button Btn_login;
    EditText old_password, New_password, Confirm_password;
    private ProgressDialog pDialog;
    String status, Message;
    // String  Acess_tocken;
    String Str_name, str_old_password, str_new_password;
    String Access_tocken, Device_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Btn_login = (Button) findViewById(R.id.button);
        old_password = (EditText) findViewById(R.id.editText2);
        New_password = (EditText) findViewById(R.id.editText4);
        Confirm_password = (EditText) findViewById(R.id.editText);
        SharedPreferences shared = getSharedPreferences("Fast_express", MODE_PRIVATE);
        Access_tocken = (shared.getString("Acess_tocken", "nodata"));
        Device_id = (shared.getString("device_id", "nodata"));
        boolean result = Utility.checkPermission(Change_password.this);
        Btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (old_password.getText().toString().contentEquals("")) {
                    Animation anm = Shake_Animation();
                    old_password.startAnimation(anm);
                } else if (New_password.getText().toString().contentEquals("")) {
                    Animation anm = Shake_Animation();
                    New_password.startAnimation(anm);
                } else if (Confirm_password.getText().toString().contentEquals("")) {
                    Animation anm = Shake_Animation();
                    Confirm_password.startAnimation(anm);
                } else if (!New_password.getText().toString().contentEquals(Confirm_password.getText().toString())) {
                    Animation anm = Shake_Animation();
                    New_password.startAnimation(anm);
                    Toast.makeText(Change_password.this, "Password did not match", Toast.LENGTH_LONG).show();
                } else {


                    str_old_password = old_password.getText().toString();
                    str_new_password = Confirm_password.getText().toString();
                    new change_password().execute();
                }

            }
        });
    }

    public Animation Shake_Animation() {
        Animation shake = new TranslateAnimation(0, 5, 0, 0);
        shake.setInterpolator(new CycleInterpolator(5));
        shake.setDuration(300);


        return shake;
    }

    private class change_password extends AsyncTask<Void, String, Void> implements DialogInterface.OnCancelListener {


        JSONObject jsonnode, json_User;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // mProgressHUD = ProgressHUD.show(Login_Screen.this, "Connecting", true, true, this);
            // Showing progress dialog
            pDialog = new ProgressDialog(Change_password.this);
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
            nameValuePairs.add(new BasicNameValuePair("old_pwd", str_old_password));
            nameValuePairs.add(new BasicNameValuePair("new_pwd", str_new_password));


            String jsonStr = sh.makeServiceCall_withHeader(SERVER.CHANGE_PASSWORD,
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
                try {
                    status = jsonObj.getString("status");

                    Message = jsonObj.getString("message");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


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

            if (status.contentEquals("true")) {
                Toast.makeText(Change_password.this, Message, Toast.LENGTH_LONG).show();
                Intent i1 = new Intent(Change_password.this, info_screen.class);

                startActivity(i1);

                finish();

                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);
            } else {
                Toast.makeText(Change_password.this, Message, Toast.LENGTH_LONG).show();
            }


        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }
}
