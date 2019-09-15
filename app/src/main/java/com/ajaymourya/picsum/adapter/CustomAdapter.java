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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ajaymourya.picsum.R;
import com.ajaymourya.picsum.model.PhotoPojo;
import com.ajaymourya.picsum.service.DownloadImage;

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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {
        holder.fileName.setText(dataList.get(position).getFilename());
        holder.authorName.setText(dataList.get(position).getAuthor());


//        Picasso.with(context).setLoggingEnabled(true);
//        Picasso.Builder builder = new Picasso.Builder(context);
//        builder.downloader(new OkHttp3Downloader(context));
//
//        builder.build().load(dataList.get(position).getPostUrl()).networkPolicy(NetworkPolicy.OFFLINE)
//                .resize(215, 100)
//                .placeholder(R.drawable.ic_launcher_background)
//                .error(R.drawable.ic_launcher_background)
//                .into(holder.imageView, new Callback() {
//                    @Override
//                    public void onSuccess() {
//
//                    }
//
//                    @Override
//                    public void onError() {
//                        //Try again online if cache failed
//                        Picasso.with(context).setLoggingEnabled(true);
//                        Picasso.Builder builder = new Picasso.Builder(context);
//                        builder.downloader(new OkHttp3Downloader(context));
//                        builder.build().load(dataList.get(position).getPostUrl()).resize(215, 100)
//                                .placeholder((R.drawable.ic_launcher_background))
//                                .error(R.drawable.ic_launcher_background)
//                                .into(holder.imageView, new Callback() {
//                                    @Override
//                                    public void onSuccess() {
//
//                                    }
//
//                                    @Override
//                                    public void onError() {
//                                        Log.v("Picasso", "Could not fetch image");
//                                    }
//                                });
//                    }
//                });
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        public final View view;

        TextView fileName;
        TextView authorName;
        ImageView downloadIcon;
        ProgressBar progressBar;
        TextView progressText;

        CustomViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            fileName = view.findViewById(R.id.file_name);
            authorName = view.findViewById(R.id.author_name);
            downloadIcon = view.findViewById(R.id.icon_download);
            progressBar = view.findViewById(R.id.progress_bar);
            progressText = view.findViewById(R.id.progress_text);


            downloadIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DownloadImage(dataList.get(getAdapterPosition()).getFilename(), context, downloadIcon, progressBar, progressText).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dataList.get(getAdapterPosition()).getPostUrl());

                }
            });
        }
    }

}
