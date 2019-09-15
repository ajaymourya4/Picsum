package com.ajaymourya.picsum.network;

import com.ajaymourya.picsum.model.ImagePojo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Ajay Mourya on 14,September,2019
 */
public interface GetDataService {

    @GET("/list")
    Call<List<ImagePojo>> getAllImages();
}
