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
import java.util.regex.Pattern;

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
    private static String songName;
    static NotificationManager mNotificationManager;

    public DownloadSong(Context context){
        this.context = context;
        initializeNotificationBuilder();
    }

    //Notification Builder to show download notification
    private void initializeNotificationBuilder() {
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Ola Music Player")
                .setAutoCancel(true)
                .setDefaults(DEFAULT_SOUND|DEFAULT_VIBRATE|DEFAULT_LIGHTS);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int)
                System.currentTimeMillis(), intent, 0);
        mBuilder.setContentIntent(pendingIntent);

        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    //Call this function to start download song
    public void startDownload(String url, String name) {
        songName = name;
        new DownloadFileAsync().execute(url);
    }

    //Async class to handle download of song
    public static class DownloadFileAsync extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showNotification("Downloading "+ songName +"......");
        }

        @Override
        protected String doInBackground(String... aurl) {
            OutputStream outStream = null;
            URLConnection uCon = null;
            HttpURLConnection urlConnection;
            String localFileName = songName+".mp3";
            String destinationDir = Environment.getExternalStorageDirectory().toString() + "/OlaMusicPlayer/";
            File folder = new File(destinationDir);
            if (!folder.exists()){
                folder.mkdir();
            }
            InputStream is = null;
            int fileLength = 0;
            try {

                URL url;
                byte[] buf;
                int ByteRead, ByteWritten = 0;
                url = new URL(aurl[0]);
                Log.d(TAG, "download: "+url);
                outStream = new BufferedOutputStream(new FileOutputStream(
                        destinationDir + localFileName));
                try {

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();

                    int ResponseCode = urlConnection.getResponseCode();
                    Log.d(TAG, "Response code: "+ResponseCode);
                    String ContentType = urlConnection.getContentType();
                    if ( ResponseCode == HttpURLConnection.HTTP_MOVED_TEMP || ResponseCode == HttpURLConnection.HTTP_MOVED_PERM )
                    {
                        String Location = Pattern.quote(urlConnection.getHeaderField("Location"));
                        Log.d(TAG,"location: "+Location.substring(2,Location.length()-2));

                        URL url1 = new URL(Location.substring(2,Location.length()-2));
                        urlConnection = (HttpURLConnection) url1.openConnection();
                    }
                    fileLength = urlConnection.getContentLength();
                    is = urlConnection.getInputStream();
                    Log.d(TAG, "input stream recieved");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Error: "+e.getMessage());
                }

                buf = new byte[1024];
                while ((ByteRead = is.read(buf)) != -1) {
                    outStream.write(buf, 0, ByteRead);
                    ByteWritten += ByteRead;
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (ByteWritten * 100 / fileLength));
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
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //showNotification("Downloading "+ songName +" "+values[0]+"/100");
            Log.d(TAG, "progress: "+"Downloading "+ songName +" "+values[0]+"/100");
        }

        @Override
        protected void onPostExecute(String unused) {
            showNotification("Download Complete");
        }
    }


    //Shows notification
    private static void showNotification(String message){
        mBuilder.setContentText(message);
        mNotificationManager.notify(0, mBuilder.build());
    }

}
