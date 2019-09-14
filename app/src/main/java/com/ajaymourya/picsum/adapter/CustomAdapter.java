package com.ajaymourya.picsum.adapter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.recyclerview.widget.RecyclerView;

import com.ajaymourya.picsum.R;
import com.ajaymourya.picsum.activity.MainActivity;
import com.ajaymourya.picsum.model.PhotoPojo;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

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

    class CustomViewHolder extends RecyclerView.ViewHolder {

        public final View view;

        TextView fileName;
        TextView authorName;
        ImageView imageView;
        ImageView downloadIcon;

        CustomViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            fileName = view.findViewById(R.id.file_name);
            authorName = view.findViewById(R.id.author_name);
            imageView = view.findViewById(R.id.image_view);
            downloadIcon = view.findViewById(R.id.icon_download);

            downloadIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DownloadImage(dataList.get(getAdapterPosition()).getFilename(), context).execute(dataList.get(getAdapterPosition()).getPostUrl());
                }
            });
        }
    }

    private static class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private String TAG = "DownloadImage";
        private String imageName;
        private Context context;

        private NotificationCompat.Builder notificationBuilder;
        private NotificationManager notificationManager;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        "id","Channel Name" , NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "id");
            builder.setContentTitle("Picture Download")
                    .setContentText("Download in progress")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setPriority(NotificationCompat.PRIORITY_LOW);

            // Issue the initial notification with zero progress
            int PROGRESS_MAX = 100;
            int PROGRESS_CURRENT = 50;
            builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
            notificationManager.notify(m, builder.build());

        }

        private Bitmap downloadImageBitmap(String sUrl) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(sUrl);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                int file_size = urlConnection.getContentLength();
                Log.e("file_length", " "+file_size);
                InputStream inputStream = new URL(sUrl).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);       
                inputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception 1, Something went wrong!");
                e.printStackTrace();
            }
            return bitmap;
        }

        public DownloadImage(String imageName, Context context) {
            this.imageName = imageName;
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null)
                createDirectoryAndSaveFile(result, imageName);
        }
    }


    private static void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

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
