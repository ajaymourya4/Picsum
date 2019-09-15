package com.ajaymourya.picsum.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ajaymourya.picsum.R;
import com.ajaymourya.picsum.model.ImagePojo;
import com.ajaymourya.picsum.service.DownloadImage;

import java.util.List;

/**
 * Created by Ajay Mourya on 14,September,2019
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    public Context context;

    // List of Image Objects
    private List<ImagePojo> dataList;

    public CustomAdapter(Context context, List<ImagePojo> dataList) {
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

    // Overriding the below method to handle UI changes for the individual list item
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {
        // Set the file name and author name for each list item
        holder.fileName.setText(dataList.get(position).getFilename());
        holder.authorName.setText(dataList.get(position).getAuthor());
    }

    @Override
    public int getItemCount() {
        // Show only the first 20 items in the recycler view
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
                    new DownloadImage(dataList.get(getAdapterPosition())
                            .getFilename(), context, downloadIcon, progressBar, progressText)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                    dataList.get(getAdapterPosition()).getPostUrl());
                }
            });
        }
    }
}
