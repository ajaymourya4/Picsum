package com.ajaymourya.picsum.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ajaymourya.picsum.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

    private String TAG = "DownloadImage";
    private String imageName;
    private Context context;

    ImageView downloadIcon;
    ProgressBar progressBar;
    TextView progressText;

    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;
    private NotificationChannel mChannel;

    int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        notificationManager = NotificationManagerCompat.from(context);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(
                    "id", "Channel Name", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        builder = new NotificationCompat.Builder(context, "id");
        builder.setContentTitle(imageName)
                .setContentText("Downloading Image")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Issue the initial notification with zero progress
        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);


        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                downloadIcon.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                progressText.setVisibility(View.VISIBLE);
            }
        });

    }

    private Bitmap downloadImageBitmap(String sUrl) {
        Bitmap bitmap = null;

        int count;
        try {
            URL url = new URL(sUrl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            int lenghtOfFile = urlConnection.getContentLength();
            Log.e("file_length", " " + lenghtOfFile);
            InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = inputStream.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

            }

            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            Log.d(TAG, "Exception 1, Something went wrong!");
            e.printStackTrace();
        }
        return bitmap;
    }

    private void publishProgress(final String s) {

        updateNotification(Integer.parseInt(s));

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(Integer.parseInt(s));
                progressText.setText(s + "%");
            }
        });


    }

    public DownloadImage(String imageName, Context context, ImageView downloadIcon, ProgressBar progressBar, TextView progressText) {
        this.imageName = imageName;
        this.context = context;
        this.downloadIcon = downloadIcon;
        this.progressBar = progressBar;
        this.progressText = progressText;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        return downloadImageBitmap(params[0]);
    }

    protected void onPostExecute(Bitmap result) {
        if (result != null)
            createDirectoryAndSaveFile(result, imageName);

    }

    private void updateNotification(int currentProgress) {

        builder.setProgress(100, currentProgress, false);
        if (currentProgress == 100)
            builder.setContentText("Download Complete");
        else
            builder.setContentText("Downloaded: " + currentProgress + "%");
        notificationManager.notify(m, builder.build());
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/Picsum");

        if (!direct.exists()) {
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + "/Picsum");
            wallpaperDirectory.mkdirs();

        }

        File file = new File(new File(Environment.getExternalStorageDirectory() + "/Picsum"), fileName);
        if (file.exists()) {
            file.delete();

        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}