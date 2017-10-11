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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Service_handler.SERVER;
import Service_handler.ServiceHandler;

public class Upload_Delivery_courier_info extends AppCompatActivity {
    Button Btn_Scan;
    EditText Edt_Txt;
    private ProgressDialog pDialog;
    String status = "", Message;
    String Device_id = "", Acess_tocken = "";
    Button Btn_submitted;
    String Str_Token="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload__delivery_courier_info);
        Btn_Scan = (Button) findViewById(R.id.button4);
        Edt_Txt = (EditText) findViewById(R.id.editText2);
        Btn_submitted=(Button) findViewById(R.id.button5);
        SharedPreferences shared = getSharedPreferences("Fast_express", MODE_PRIVATE);
        Acess_tocken = (shared.getString("Acess_tocken", "nodata"));
        Device_id = (shared.getString("device_id", "nodata"));
        Btn_Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new com.google.zxing.integration.android.IntentIntegrator(Upload_Delivery_courier_info.this).initiateScan();
            }
        });
        Btn_submitted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Edt_Txt.getText().toString().contentEquals("")) {
                    Animation anm = Shake_Animation();
                    Edt_Txt.startAnimation(anm);
                }else{
                    Str_Token=Edt_Txt.getText().toString();
                    new Upload_info().execute();

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Edt_Txt.setText(result.getContents());
                Str_Token=Edt_Txt.getText().toString();
                new Upload_info().execute();

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class Upload_info extends AsyncTask<Void, String, Void> implements DialogInterface.OnCancelListener {


        JSONObject jsonnode, json_User;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // mProgressHUD = ProgressHUD.show(Login_Screen.this, "Connecting", true, true, this);
            // Showing progress dialog
            pDialog = new ProgressDialog(Upload_Delivery_courier_info.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            publishProgress("Please wait...");


            ServiceHandler sh = new ServiceHandler();
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
            nameValuePairs.add(new BasicNameValuePair("token_no", Str_Token));


//            String jsonStr = sh.makeServiceCall(SERVER.LOGIN,
//                    ServiceHandler.POST, nameValuePairs);

            String jsonStr = sh.makeServiceCall_withHeader(SERVER.OUT_FOR_DELIVERY,
                    ServiceHandler.POST, nameValuePairs, Acess_tocken, Device_id);

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
//                    if (status.contentEquals("true")) {
//                        JSONObject jsonObj_data = null;
//                        jsonObj_data = jsonObj.getJSONObject("data");
//
//
//                        Device_id = jsonObj_data.getString("device_id");
//                        Acess_tocken = jsonObj_data.getString("access_token");
//                    }

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
                Edt_Txt.setText("");

//                Intent i1 = new Intent(Upload_Delivery_courier_info.this, info_screen.class);
//                startActivity(i1);
//                finish();
//                overridePendingTransition(R.anim.slide_in_left,
//                        R.anim.slide_out_left);
                Toast.makeText(Upload_Delivery_courier_info.this, Message, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Upload_Delivery_courier_info.this, Message, Toast.LENGTH_LONG).show();
            }


        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }
}
