package com.example.kayu;

import com.google.gson.JsonElement;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface FoodApi {

    /*@Headers({
            "UserAgent : Kayu - ",
            "Android - Version 11.0- "
    })*/
    @GET("{id}.json")
    Call<FoodInformation> getPosts(@Path("id") String id);
}
