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
import android.widget.Toast;

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

/**
 * Created by Ajay Mourya on 14,September,2019
 */
public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

    private String TAG = "DownloadImage";
    private String imageName;
    private Context context;

    ImageView downloadIcon;
    ProgressBar progressBar;
    TextView progressText;

    // Since Android Oreo we must NotificationChannel for notification as
    // it also supports dot notification
    private NotificationChannel mChannel;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;

    // NotificationChannel must use unique id for each notification
    // Generate a random for notification id
    private int randomId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        notificationManager = NotificationManagerCompat.from(context);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            // Giving high importance to show notification alert with popup
            mChannel = new NotificationChannel(
                    "id", "Channel_Name", NotificationManager.IMPORTANCE_HIGH);
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

        // After download is clicked hide download icon and show the progress bar with progress text
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
        // For storing image in memory
        Bitmap bitmap = null;

        // for keeping the track of input stream
        int count;
        try {
            //connecting to url
            URL url = new URL(sUrl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();

            //lenghtOfFile is used for calculating download progress
            int lenghtOfFile = urlConnection.getContentLength();

            // input stream to read file with 8k buffer
            InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = inputStream.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

            }

            // Covert the downloaded input stream to bitmap
            bitmap = BitmapFactory.decodeStream(inputStream);

            // Close input stream
            inputStream.close();
        } catch (Exception e) {
            Log.d(TAG, "Exception 1, Something went wrong!");

            e.printStackTrace();

            // When image is not downloaded hide the progress bar and progress text
            // and show the download icon
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    downloadIcon.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    progressText.setVisibility(View.INVISIBLE);
                }
            });
        }
        return bitmap;
    }

    private void publishProgress(final String s) {

        // Update the horizontal bar in notification
        updateNotification(Integer.parseInt(s));

        // Update the circular bar UI in list item
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

    // Updates the notification based on current progress
    private void updateNotification(int currentProgress) {
        builder.setProgress(100, currentProgress, false);
        if (currentProgress == 100)
            builder.setContentText("Download Complete");
        else
            builder.setContentText("Downloaded: " + currentProgress + "%");
        notificationManager.notify(randomId, builder.build());
    }

    // Save the image in the Picsum folder with a filename
    // If Picsum folder is not present then it is created
    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/Picsum");

        // If Picsum directory not present then create it
        if (!direct.exists()) {
            File picsumDirectory = new File(Environment.getExternalStorageDirectory() + "/Picsum");
            picsumDirectory.mkdirs();
        }

        // If same filename is already present then delete
        File file = new File(new File(Environment.getExternalStorageDirectory() + "/Picsum"), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            // Save the file
            FileOutputStream outputStream = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}