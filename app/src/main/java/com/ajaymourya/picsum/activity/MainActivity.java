package com.ajaymourya.picsum.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ajaymourya.picsum.R;
import com.ajaymourya.picsum.adapter.CustomAdapter;
import com.ajaymourya.picsum.model.ImagePojo;
import com.ajaymourya.picsum.network.GetDataService;
import com.ajaymourya.picsum.network.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ajay Mourya on 14,September,2019
 */
public class MainActivity extends AppCompatActivity {

    private CustomAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Show progress dialog at start of the activity
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        // Create handle for the RetrofitInstance interface
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        Call<List<ImagePojo>> call = service.getAllImages();
        call.enqueue(new Callback<List<ImagePojo>>() {
            @Override
            public void onResponse(Call<List<ImagePojo>> call, Response<List<ImagePojo>> response) {
                // On success dismiss the progress dialog and inflate the recycler view
                progressDialog.dismiss();
                generateDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<ImagePojo>> call, Throwable t) {
                // On failure dismiss the progress dialog and show appropriate toast message
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to generate List of data using RecyclerView with custom adapter
    private void generateDataList(List<ImagePojo> photoList) {
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new CustomAdapter(this, photoList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
