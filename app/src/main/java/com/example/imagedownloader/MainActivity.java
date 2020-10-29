package com.example.imagedownloader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button btnDownload;
    EditText etUrl;
    ProgressBar progressBar;
    public static final String TAG = "Exception";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnDownload = findViewById(R.id.btn_download);
        progressBar = new ProgressBar(MainActivity.this);
        progressBar.setIndeterminate(true);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    etUrl = findViewById(R.id.et_url);

                    DownloadTask downloadTask = (DownloadTask) new DownloadTask().execute("https://drive.google.com/file/d/1ZaS1iocan4hq0KBiPzSsEtksnnx7Snkc/view?usp=sharing");

                    downloadTask.execute(etUrl.getText().toString());

                }else {
                    Toast.makeText(getBaseContext(), "Network is not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        boolean available = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isAvailable()) {
            available = true;
        }
        return available;

    }

    private Bitmap downloadUrl(String strUrl) throws IOException {
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(strUrl);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
//            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                return "Server returned HTTP " + urlConnection.getResponseCode() + " " + urlConnection.getResponseMessage();
//            }
            inputStream = urlConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        }catch (Exception e) {
            Log.d(TAG, e.toString());
        }finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return bitmap;
    }

    private class DownloadTask extends AsyncTask<String, Integer, Bitmap>{
        Bitmap bitmap = null;

        @Override
        protected Bitmap doInBackground(String... strings) {
            try{
                bitmap = downloadUrl(strings[0]);
            }catch (Exception e){

                Log.d(TAG, "doInBackground: "+ e.getMessage());
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView = findViewById(R.id.iv_image);
            imageView.setImageBitmap(bitmap);
            Toast.makeText(getBaseContext(), "Image downloaded successfully", Toast.LENGTH_SHORT).show();
        }
    }

}