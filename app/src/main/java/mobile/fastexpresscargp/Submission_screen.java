package mobile.fastexpresscargp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import DataBase_Handler.DatabaseHandler;
import INTERFACE.Database_value;
import broadcast_reciever.ConnectivityReceiver;
import broadcast_reciever.MyApplication;
import Multipart_enttity.AndroidMultiPartEntity;
import Multipart_enttity.Utility;
import Service_handler.SERVER;

public class Submission_screen extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener {
    Button Btn_click_photo, Btn_Submit;
    ImageView Img_image, Img_cancel;
    Button Btn_Scan;
    File f;
    private static final int CAMERA_PHOTO = 111;
    private Uri imageToUploadUri;
    private ProgressDialog pDialog;
    long totalSize = 0;
    String Message;
    String status = "";
    String Access_tocken = "";
    String Device_id = "";
    String Str_img_path = "";
    EditText Token_number_edtx, Delivery_to_Edtxt, Remarks_Edtxt;
    Spinner Spinner_;
    DatabaseHandler db;
    String Token_string, Delivery_string = "By Signature", Remarks_string;
    String[] options = {"Consignment Delivered with signature", "Consignment Delivered with company stemp","Consignment Delivered with I’d proof"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_screen);
        Btn_click_photo = (Button) findViewById(R.id.button4);
        Btn_Submit = (Button) findViewById(R.id.button5);
        Btn_Scan = (Button) findViewById(R.id.button8);
        Img_image = (ImageView) findViewById(R.id.imageView2);
        Img_cancel = (ImageView) findViewById(R.id.img_cancel);
        Token_number_edtx = (EditText) findViewById(R.id.editText2);
        Spinner_ = (Spinner) findViewById(R.id.editText3);
        Remarks_Edtxt = (EditText) findViewById(R.id.editText);
        SharedPreferences shared = getSharedPreferences("Fast_express", MODE_PRIVATE);
        Access_tocken = (shared.getString("Acess_tocken", "nodata"));
        Device_id = (shared.getString("device_id", "nodata"));
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, options);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        Spinner_.setAdapter(aa);
        Spinner_.setOnItemSelectedListener(this);
        db = new DatabaseHandler(Submission_screen.this);

        Btn_click_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = Utility.checkPermission(Submission_screen.this);

                // if (result) {
                selectImage_new();

                //galleryIntent();


                //  }
            }
        });
        Btn_Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new com.google.zxing.integration.android.IntentIntegrator(Submission_screen.this).initiateScan();
            }
        });
        Btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Remarks_Edtxt.getText().toString().length() == 0) {
                    if (Token_number_edtx.getText().toString().contentEquals("")) {
                        Animation anm = Shake_Animation();
                        Token_number_edtx.startAnimation(anm);
                    } else if (Str_img_path.contentEquals("")) {
                        Toast.makeText(Submission_screen.this, "Click Signature", Toast.LENGTH_LONG).show();
                    } else {


                        Token_string = Token_number_edtx.getText().toString();
                        // Delivery_string = Delivery_to_Edtxt.getText().toString();
                        Remarks_string = Remarks_Edtxt.getText().toString();
                        Boolean is_connected = checkConnection();
                        if (is_connected) {
                            new Submit_Task().execute();
                        } else {
                            String Check_docket = "";
                            Check_docket = db.Check_Docket(Token_string);
                            if (Check_docket.contentEquals("")) {
                                db.Save_Fast_express_info(Token_string, Delivery_string, Remarks_string, Str_img_path);
                                Token_number_edtx.setText("");
                                Remarks_Edtxt.setText("");
                                Str_img_path = "";
                                Img_image.setImageResource(android.R.drawable.ic_menu_gallery);
                                Img_cancel.setVisibility(View.INVISIBLE);
                                SharedPreferences shared2 = getSharedPreferences("Fast_express", MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = shared2.edit();
                                editor2.putString("Satus", "Nothing");

                                editor2.commit();
                                Toast.makeText(Submission_screen.this, "Internet connection not available data saved locally", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(Submission_screen.this, "Already saved locally", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                } else {
                    Token_string = Token_number_edtx.getText().toString();
                    // Delivery_string = Delivery_to_Edtxt.getText().toString();
                    Remarks_string = Remarks_Edtxt.getText().toString();
                    Boolean is_connected = checkConnection();
                    if (is_connected) {
                        new Submit_Task().execute();
                    } else {
                        String Check_docket = "";
                        Check_docket = db.Check_Docket(Token_string);
                        if (Check_docket.contentEquals("")) {
                            db.Save_Fast_express_info(Token_string, Delivery_string, Remarks_string, Str_img_path);
                            Token_number_edtx.setText("");
                            Remarks_Edtxt.setText("");
                            Str_img_path = "";
                            Img_image.setImageResource(android.R.drawable.ic_menu_gallery);
                            Img_cancel.setVisibility(View.INVISIBLE);
                            SharedPreferences shared2 = getSharedPreferences("Fast_express", MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = shared2.edit();
                            editor2.putString("Satus", "Nothing");

                            editor2.commit();
                            Toast.makeText(Submission_screen.this, "Internet connection not available data saved locally", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Submission_screen.this, "Already saved locally", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
        Img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Img_image.setImageResource(android.R.drawable.ic_menu_gallery);
                Img_cancel.setVisibility(View.INVISIBLE);
                Str_img_path = "";
            }
        });
    }

    public Animation Shake_Animation() {
        Animation shake = new TranslateAnimation(0, 5, 0, 0);
        shake.setInterpolator(new CycleInterpolator(5));
        shake.setDuration(300);


        return shake;
    }

    private void selectImage_new() {

        final CharSequence[] options = {"Take Photo", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Submission_screen.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + "Fast_express_sign.jpg");
                    chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    imageToUploadUri = Uri.fromFile(f);
                    startActivityForResult(chooserIntent, CAMERA_PHOTO);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PHOTO && resultCode == RESULT_OK) {
            if (imageToUploadUri != null) {
                Uri selectedImage = imageToUploadUri;
                getContentResolver().notifyChange(selectedImage, null);
                Str_img_path = imageToUploadUri.getPath();
                Bitmap reducedSizeBitmap = getBitmap(imageToUploadUri.getPath());
                //Bitmap rotated_bitmap  =rotateBitmap(reducedSizeBitmap,90);
                if (reducedSizeBitmap != null) {
                    Img_image.setImageBitmap(reducedSizeBitmap);

                    Img_cancel.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "Error while capturing Image", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Error while capturing Image", Toast.LENGTH_LONG).show();
            }
        }
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Token_number_edtx.setText(result.getContents());
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {

            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

//            try {
//                bitmap.recycle();
//            }catch (java.lang.RuntimeException e){
//                e.printStackTrace();
//            }
            return bmRotated;

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }

    }

    private Bitmap getBitmap(String path) {

        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " +
                    b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Delivery_string = options[i];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            SharedPreferences sharedd = getSharedPreferences("Fast_express", MODE_PRIVATE);
            String Statuss = (sharedd.getString("Satus", "nodata"));
            if (Statuss.contentEquals("Nothing")) {

//            ConnectivityManager cm = (ConnectivityManager) Submission_screen.this
//                    .getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//            NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//            ;
//            boolean isConnectedd = wifi != null && wifi.isConnectedOrConnecting();

                List<Database_value> value = db.get_All_Fast_express_info();
                if (value.size() > 0) {
//                    SharedPreferences shared = getSharedPreferences("Fast_express", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = shared.edit();
//                    editor.putString("Satus", "Uploading");
//
//                    editor.commit();
//                    for (Database_value AV : value) {
//                        HashMap<String, String> Hash_map_fetch = new HashMap<String, String>();
//                        String DC_number = AV.getDockect_number();
//                        String spinner_value = AV.getSpinner_value();
//                        String Remarks = AV.getRemarks();
//                        String photo_url = AV.getPhoto_url();
//
//                        Token_string = DC_number;
//                        Delivery_string = spinner_value;
//                        Remarks_string = Remarks;
//                        Str_img_path = photo_url;
//
                    new Submit_Task().execute();
//                    }
//                    SharedPreferences shared2 = getSharedPreferences("Fast_express", MODE_PRIVATE);
//                    SharedPreferences.Editor editor2 = shared2.edit();
//                    editor2.putString("Satus", "Uploaded");
//
//                    editor2.commit();
//                    db.Delete_all();
                } else {
                    Log.i("FastExpress", "Database empty");
                }
            }
            Toast.makeText(Submission_screen.this, "Connected", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(Submission_screen.this, "Not Connected", Toast.LENGTH_LONG).show();
        }

    }

    private class Submit_Task extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            pDialog = new ProgressDialog(Submission_screen.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.show();
//            progressBar.setVisibility(View.VISIBLE);
//            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible


            // updating progress bar value
            pDialog.setProgress(progress[0]);

            // updating percentage value
            //txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(SERVER.SUBMIT_DELIVERY);
            httppost.addHeader("X-TOKEN", Access_tocken);
            httppost.addHeader("X-DEVICE", Device_id);

//
            List<Database_value> value = db.get_All_Fast_express_info();
            if (value.size() > 0) {
                SharedPreferences shared = getSharedPreferences("Fast_express", MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("Satus", "Uploading");

                editor.commit();
                for (Database_value AV : value) {
                    HashMap<String, String> Hash_map_fetch = new HashMap<String, String>();
                    String DC_number = AV.getDockect_number();
                    String spinner_value = AV.getSpinner_value();
                    String Remarks = AV.getRemarks();
                    String photo_url = AV.getPhoto_url();

                    Token_string = DC_number;
                    Delivery_string = spinner_value;
                    Remarks_string = Remarks;
                    Str_img_path = photo_url;
                    try {

                        try {
                            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                                    new AndroidMultiPartEntity.ProgressListener() {

                                        @Override
                                        public void transferred(long num) {
                                            publishProgress((int) ((num / (float) totalSize) * 100));
                                        }
                                    });
                            //new Submit_Task().execute();
                            if (Remarks_string.length() > 0) {
                                entity.addPart("token_no", new StringBody(Token_string));
                                entity.addPart("remarks", new StringBody(Remarks_string));
                            } else {


                                File sourceFile1 = new File(Str_img_path);
                                entity.addPart("signature", new FileBody(sourceFile1));


                                // Adding file data to http body

                                //entity.addPart("gender", new StringBody(gender));


                                entity.addPart("token_no", new StringBody(Token_string));
                                entity.addPart("deliver_to", new StringBody(Delivery_string));
                                //  entity.addPart("category", new StringBody(Str_category));
                                entity.addPart("remarks", new StringBody(Remarks_string));
                            }
                            totalSize = entity.getContentLength();
                            httppost.setEntity(entity);
//
                            // Making server call
                            HttpResponse response = httpclient.execute(httppost);
                            HttpEntity r_entity = response.getEntity();

                            int statusCode = response.getStatusLine().getStatusCode();
                            if (statusCode == 200) {
                                // Server response
                                responseString = EntityUtils.toString(r_entity);
                            } else {
                                responseString = "Error occurred! Http Status Code: "
                                        + statusCode;
                            }

                        } catch (ClientProtocolException e) {
                            responseString = e.toString();
                        } catch (IOException e) {
                            responseString = e.toString();
                        }
                        if (responseString != null) {
                            JSONObject jsonObj = null;
                            try {
                                jsonObj = new JSONObject(responseString);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Getting JSON Array node
                            // JSONArray array1 = null;
                            try {
                                status = jsonObj.getString("status");
                                Message = jsonObj.getString("message");
//                        if (status.contentEquals("true")) {
//                            JSONObject jsonObj_data = null;
//                           // jsonObj_data = jsonObj.getJSONObject("data");
//                          //  Message = jsonObj_data.getString("message");
//
//
//                        }
                                // error = jsonObj.getString("errorCode");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // entity.addPart("status", new StringBody("1"));


                    } catch (java.lang.NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                SharedPreferences shared2 = getSharedPreferences("Fast_express", MODE_PRIVATE);
                SharedPreferences.Editor editor2 = shared2.edit();
                editor2.putString("Satus", "Uploaded");

                editor2.commit();
                db.Delete_all();
            } else {
                Log.i("FastExpress", "Database empty");
                try {

                    try {
                        AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                                new AndroidMultiPartEntity.ProgressListener() {

                                    @Override
                                    public void transferred(long num) {
                                        publishProgress((int) ((num / (float) totalSize) * 100));
                                    }
                                });

                        if (Remarks_string.length() > 0) {
                            entity.addPart("token_no", new StringBody(Token_string));
                            entity.addPart("remarks", new StringBody(Remarks_string));
                        } else {


                            File sourceFile1 = new File(Str_img_path);
                            entity.addPart("signature", new FileBody(sourceFile1));


                            // Adding file data to http body

                            //entity.addPart("gender", new StringBody(gender));


                            entity.addPart("token_no", new StringBody(Token_string));
                            entity.addPart("deliver_to", new StringBody(Delivery_string));
                            //  entity.addPart("category", new StringBody(Str_category));
                            entity.addPart("remarks", new StringBody(Remarks_string));
                        }
                        totalSize = entity.getContentLength();
                        httppost.setEntity(entity);
//
                        // Making server call
                        HttpResponse response = httpclient.execute(httppost);
                        HttpEntity r_entity = response.getEntity();

                        int statusCode = response.getStatusLine().getStatusCode();
                        if (statusCode == 200) {
                            // Server response
                            responseString = EntityUtils.toString(r_entity);
                        } else {
                            responseString = "Error occurred! Http Status Code: "
                                    + statusCode;
                        }

                    } catch (ClientProtocolException e) {
                        responseString = e.toString();
                    } catch (IOException e) {
                        responseString = e.toString();
                    }
                    if (responseString != null) {
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(responseString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Getting JSON Array node
                        // JSONArray array1 = null;
                        try {
                            status = jsonObj.getString("status");
                            Message = jsonObj.getString("message");
//                        if (status.contentEquals("true")) {
//                            JSONObject jsonObj_data = null;
//                           // jsonObj_data = jsonObj.getJSONObject("data");
//                          //  Message = jsonObj_data.getString("message");
//
//
//                        }
                            // error = jsonObj.getString("errorCode");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    // entity.addPart("status", new StringBody("1"));


                } catch (java.lang.NullPointerException e) {
                    e.printStackTrace();
                }
                //  entity.addPart("status", new StringBody("1"));
            }


            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Log.e(TAG, "Response from server: " + result);

            // showing the server response in an alert dialog
            //  showAlert(result);

            // new User_profile().execute();
            if (status.contentEquals("true")) {
                Intent i1 = new Intent(Submission_screen.this, Delivery_successfully_saved.class);
                startActivity(i1);
                finish();
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);

                // Toast.makeText(getActivity(), "Your Property Detail have been submitted successfully! It will publish shortly", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(Submission_screen.this, Message, Toast.LENGTH_LONG).show();
            }
            pDialog.dismiss();
            //progressBar.setVisibility(View.GONE);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    // Method to manually check connection status
    private Boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        //showSnack(isConnected);
        return isConnected;
    }
}
