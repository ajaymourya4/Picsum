package com.ajaymourya.picsum.adapter;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ajaymourya.picsum.ProgressBarInterface;
import com.ajaymourya.picsum.R;
import com.ajaymourya.picsum.model.PhotoPojo;
import com.ajaymourya.picsum.service.DownloadImage;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private List<PhotoPojo> dataList;
    public Context context;

    public CustomAdapter(Context context, List<PhotoPojo> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {
        holder.fileName.setText(dataList.get(position).getFilename());
        holder.authorName.setText(dataList.get(position).getAuthor());

        Picasso.with(context).setLoggingEnabled(true);
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(context));

        builder.build().load(dataList.get(position).getPostUrl()).networkPolicy(NetworkPolicy.OFFLINE)
                .resize(215, 100)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(context).setLoggingEnabled(true);
                        Picasso.Builder builder = new Picasso.Builder(context);
                        builder.downloader(new OkHttp3Downloader(context));
                        builder.build().load(dataList.get(position).getPostUrl()).resize(215, 100)
                                .placeholder((R.drawable.ic_launcher_background))
                                .error(R.drawable.ic_launcher_background)
                                .into(holder.imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.v("Picasso", "Could not fetch image");
                                    }
                                });
                    }
                });
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements ProgressBarInterface {

        public final View view;

        TextView fileName;
        TextView authorName;
        ImageView imageView;
        ImageView downloadIcon;
        ProgressBar progressBar;
        TextView progressText;
        ProgressBarInterface progressBarInterface;

        CustomViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            fileName = view.findViewById(R.id.file_name);
            authorName = view.findViewById(R.id.author_name);
            imageView = view.findViewById(R.id.image_view);
            downloadIcon = view.findViewById(R.id.icon_download);
            progressBar = view.findViewById(R.id.progress_bar);
            progressText = view.findViewById(R.id.progress_text);


            downloadIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DownloadImage2(dataList.get(getAdapterPosition()).getFilename(), context, downloadIcon, progressBar, progressText).execute(dataList.get(getAdapterPosition()).getPostUrl());

                }
            });
        }


        @Override
        public void setViews() {
            downloadIcon.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.VISIBLE);
        }

        @Override
        public void setProgressBarAndText(String currentProgress) {
            progressBar.setProgress(Integer.parseInt(currentProgress));
            progressText.setText(currentProgress + "%");
        }
    }

    public class DownloadImage2 extends AsyncTask<String, Void, Bitmap> {

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

        public DownloadImage2(String imageName, Context context, ImageView downloadIcon, ProgressBar progressBar, TextView progressText) {
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

}
