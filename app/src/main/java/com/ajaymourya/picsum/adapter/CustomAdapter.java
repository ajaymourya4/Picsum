package com.ajaymourya.picsum.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ajaymourya.picsum.R;
import com.ajaymourya.picsum.model.PhotoPojo;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private List<PhotoPojo> dataList;
    private Context context;

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


        Log.e("imageurl", " " + dataList.get(position).getPostUrl());
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
                    Log.e("download"," "+ dataList.get(getAdapterPosition()).getPostUrl());
                }
            });
        }
    }
}
