package com.example.safedrive_guardian.ui.publicplaces;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadUrl {
    private String TAG = "APP: DownloadUrl";

    public String retriveUrl(String url) throws IOException {
        String urlData = "";
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try{
            URL getUrl = new URL(url);
            httpURLConnection = (HttpURLConnection) getUrl.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();

            String line = "";

            while((line =  bufferedReader.readLine()) != null){
                sb.append(line);
            }

            urlData = sb.toString();
            bufferedReader.close();


        } catch (MalformedURLException e) {

            Log.e(TAG, "retriveUrl: "+e.getStackTrace());
            throw new RuntimeException(e);
        } catch (IOException e) {

            Log.e(TAG, "retriveUrl: "+e.getStackTrace());
            throw new RuntimeException(e);
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            httpURLConnection.disconnect();
        }

        return urlData;
    }

}
