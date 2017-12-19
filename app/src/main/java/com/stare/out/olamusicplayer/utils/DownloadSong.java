package com.stare.out.olamusicplayer.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.stare.out.olamusicplayer.MainActivity;
import com.stare.out.olamusicplayer.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static android.app.Notification.DEFAULT_LIGHTS;
import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;

/**
 * Created by rishabh on 17/12/17.
 */

public class DownloadSong {

    static Context context;
    static NotificationCompat.Builder mBuilder;
    private static final String TAG = "DownloadSong";

    public DownloadSong(Context context){
        this.context = context;
        mBuilder = new NotificationCompat.Builder(context);
    }

    public void startDownload(String url) {
        new DownloadFileAsync().execute(url);
    }

    public static class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showNotification("Downloading Song...");
        }

        @Override
        protected String doInBackground(String... aurl) {
            OutputStream outStream = null;
            URLConnection uCon = null;
            HttpURLConnection mHttpCon;
            String localFileName = "OLA"+ System.currentTimeMillis()+".mp3";
            String destinationDir = Environment.getExternalStorageDirectory().toString() + "/OlaMusicPlayer";

            InputStream is = null;
            try {

                URL url;
                byte[] buf;
                int ByteRead, ByteWritten = 0;
                url = new URL(aurl[0]);
                Log.d(TAG, "download: "+url);
                outStream = new BufferedOutputStream(new FileOutputStream(
                        destinationDir + localFileName));

                try {

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();
                    String Location = urlConnection.getHeaderField("Location");
                    Log.d(TAG,"location: "+Location);
//                    String ResponseCode = urlConnection.getResponseCode();
//                    String ContentType = urlConnection.getContentType();
//                    if ( result.ResponseCode == HttpURLConnection.HTTP_MOVED_TEMP || result.ResponseCode == HttpURLConnection.HTTP_MOVED_PERM )
//                    {
//                        String Location = urlConnection.getHeaderField("Location");
//                    }

                    Location = Location.replace("(", "/(");
                    Location = Location.replace(")", "/)");
                    Log.d("Updated Location: ", Location);
                    URL url1 = new URL(Location);
                    mHttpCon = (HttpURLConnection) url1.openConnection();

//                    while (!url.toString().startsWith("https")) {
//                        Log.d(TAG,"url: "+url);
//                        mHttpCon.getResponseCode();
//                        url = mHttpCon.getURL();
//                        mHttpCon = (HttpURLConnection) url.openConnection();
//
//                    }

                    is = mHttpCon.getInputStream();
                    Log.d(TAG, "input stream recieved");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Error: "+e.getMessage());
                    // url = new URL(e.getMessage().substring(
                    // e.getMessage().indexOf("https"),
                    // e.getMessage().length()));
                    // outStream = new BufferedOutputStream(new FileOutputStream(
                    // destinationDir + localFileName));
                    //
                    // uCon = url.openConnection();
                    // is = uCon.getInputStream();
                }

                buf = new byte[1024];
                while ((ByteRead = is.read(buf)) != -1) {
                    outStream.write(buf, 0, ByteRead);
                    ByteWritten += ByteRead;
                }
                Log.d(TAG,"Downloaded Successfully.");
                Log.d(TAG,"File name:\"" + localFileName
                        + "\"\nNo ofbytes :" + ByteWritten);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Error: "+e.getMessage());
            } finally {
                try {
                    is.close();
                    outStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String unused) {
            showNotification("Download Complete");
        }
    }


    private static void showNotification(String message){
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Ola Music Player")
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE | DEFAULT_LIGHTS);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int)
                System.currentTimeMillis(), intent, 0);
        mBuilder.setContentIntent(pendingIntent);


        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

}
