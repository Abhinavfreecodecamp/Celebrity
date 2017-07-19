package com.example.android.celebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    ArrayList<String> celebesimages= new ArrayList<String>();
    ArrayList<String> celebesnames = new ArrayList<String>();
    int  chosenceleb =0;
    ImageView imageView;
    Bitmap celebimage;
    public class DownloadImage extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.connect();
                InputStream in = httpURLConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);
                return  bitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }

    public class DownloadTask extends AsyncTask<String ,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection httpURLConnection = null;
            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char character = (char) data;
                    result += character;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.imageView);
        DownloadTask task = new DownloadTask();
        String result= "";
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String[] splitresult = result.split("<div class=\"col-xs-12 col-sm-6 col-md-4\">");
        Pattern p = Pattern.compile("<img src=\"(.*?)\"");
        Matcher matcher = p.matcher(splitresult[0]);
         while (matcher.find()){
            celebesimages.add(matcher.group(1));
        }
        p=Pattern.compile("alt=\"(.*?)\"");
        matcher=p.matcher(splitresult[0]);
        while (matcher.find()){
            celebesnames.add(matcher.group(1));
        }

        Random random= new Random();
        chosenceleb = random.nextInt(celebesimages.size());

        DownloadImage downloadImage = new DownloadImage();
        try {
            celebimage= downloadImage.execute(celebesimages.get(chosenceleb)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(celebimage);
    }

    public void celebchosen(View view) {
    }
}
