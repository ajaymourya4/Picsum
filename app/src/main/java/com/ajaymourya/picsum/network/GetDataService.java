package com.ajaymourya.picsum.network;

import com.ajaymourya.picsum.model.PhotoPojo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataService {

    @GET("/photos")
    Call<List<PhotoPojo>> getAllPhotos();
}
