package com.example.krishnakumar.linkpreview;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ProgressBar loader;
    private ImageView imgWebsite;
    private EditText etURL;
    private ImageView imgCancel;
    private TextView txtStatus,txtDomain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        loader.setVisibility(View.GONE);
        etURL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String word = s.toString();
                if (word.contains(" ")) {
                    loader.setVisibility(View.VISIBLE);
                    new callService().execute(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private class callService extends AsyncTask<String,Void,JSONObject>{
        private String mainURL;
        @Override
        protected JSONObject doInBackground(String... params) {
            mainURL = params[0];
            JSONObject obj = null;
            try {
                URL url = new URL("http://playground.maxmorgandesign.com/url-details.php?url="+params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

                // read the response
                System.out.println("Response Code: " + conn.getResponseCode());
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                obj = new JSONObject(responseStrBuilder.toString());
            }catch (Exception e){
                Log.e("### exc",e.toString());
            }
            return obj;
        }

        @Override
        protected void onPostExecute(JSONObject jObj) {
            super.onPostExecute(jObj);
            loader.setVisibility(View.GONE);
            try {
                if (jObj.has("Error")) {
                    txtStatus.setText(jObj.getString("Error"));
                    txtDomain.setVisibility(View.GONE);

                } else {
                    if (jObj.has("Title")) {
                        txtStatus.setText(jObj.getString("Title"));
                    }

                    if (jObj.has("DomainName")) {
                        txtDomain.setText(jObj.getString("DomainName"));
                        txtDomain.setVisibility(View.VISIBLE);
                    }

                    if (jObj.has("Image")) {
                        imgWebsite.setVisibility(View.VISIBLE);

                        Glide.clear(imgWebsite);
                        Log.e("##img URL", "" + jObj.getString("Image"));
                        if(jObj.getString("Image").trim().startsWith("[")){
                            JSONArray imgArray = jObj.getJSONArray("Image");
                            Glide.with(MainActivity.this).load(imgArray.get(0)).into(imgWebsite);
                        }else {
                            Glide.with(MainActivity.this).load(jObj.getString("Image")).into(imgWebsite);
                        }




                    }
                }
            }catch (Exception e){
                Log.e("eee",e.toString());
            }
        }
    }


    private void init(){
        imgCancel =  (ImageView)findViewById(R.id.imgCancel);
        imgWebsite = (ImageView)findViewById(R.id.imgWebsite);
        loader = (ProgressBar)findViewById(R.id.loader);
        etURL = (EditText)findViewById(R.id.etURL);
        txtStatus = (TextView)findViewById(R.id.txtStatus);
        txtDomain = (TextView)findViewById(R.id.txtDomain);
    }
}
