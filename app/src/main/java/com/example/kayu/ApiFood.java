package com.example.kayu;


import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFood {

    private FoodDescription foodInfo = new FoodDescription();


    private MyListener mListener;

    public ApiFood(MyListener listener)
    {
        mListener = listener;
    }

    public void Call(String id)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://world.openfoodfacts.org/api/v0/product/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FoodApi result = retrofit.create(FoodApi.class);
        Call<FoodInformation> call = result.getPosts(id);

        call.enqueue(new Callback<FoodInformation>() {
            @Override
            public void onResponse(Call<FoodInformation> call, Response<FoodInformation> response) {

                if(response.isSuccessful())
                {
                    FoodInformation foodInformation = response.body();
                    if(foodInformation.getStatus() == 1)
                    {
                        FillInformation(foodInformation.getProduct());
                        mListener.OnComplete(true, foodInfo);
                    }
                    else
                        mListener.OnComplete(false, null);
                }
                else
                {
                    Log.d("Response :", Integer.toString(response.code()));
                    mListener.OnComplete(false, null);
                    return;
                }

            }

            @Override
            public void onFailure(Call<FoodInformation> call, Throwable t) {
                mListener.OnComplete(false, null);
                Log.d("Failed :" ,t.getMessage());
            }
        });
    }

    public void callMany(String[] id)
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://world.openfoodfacts.org/api/v0/product/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FoodApi result = retrofit.create(FoodApi.class);
        final int[] count = {0};
        List<FoodDescription> listFood = new ArrayList<FoodDescription>();

        for(int i = 0; i < id.length; i++)
        {
            Call<FoodInformation> call = result.getPosts(id[i]);

            call.enqueue(new Callback<FoodInformation>() {
                @Override
                public void onResponse(Call<FoodInformation> call, Response<FoodInformation> response) {

                    if(response.isSuccessful())
                    {
                        FoodInformation foodInformation = response.body();
                        if(foodInformation.getStatus() == 1)
                        {
                            FillInformation(foodInformation.getProduct());
                            listFood.add(foodInfo);
                            count[0]++;
                            if(count[0] == id.length)
                            {
                                for(int i = 0; i < listFood.size(); i++)
                                {
                                    for(int j = listFood.size() - 1; j >= i;j--)
                                    {
                                        if(listFood.get(j).id.equals(id[i]))
                                            Collections.swap(listFood,i,j);
                                    }
                                }
                                mListener.OnManyComplete(true,listFood);
                            }

                        }
                    }
                    else
                    {
                        Log.d("Response :", Integer.toString(response.code()));
                        return;
                    }

                }

                @Override
                public void onFailure(Call<FoodInformation> call, Throwable t) {
                    Log.d("Failed :" ,t.getMessage());
                }

            });
        }

    }

    private void FillInformation(JsonElement jsonElement)
    {
        foodInfo = new FoodDescription();
        JsonObject object = jsonElement.getAsJsonObject();
        foodInfo.id = GetValue("_id", object);
        foodInfo.name = GetValue("product_name", object);
        foodInfo.nutriScore = GetValue("nutrition_grade_fr", object).toUpperCase();
        foodInfo.quantity = GetValue("product_quantity", object);
        foodInfo.categories = GetValue("categories", object);
        foodInfo.ingredients = GetValue("ingredients_text", object);

        if(object.has("allergens_hierarchy"))
        {
            JsonArray tempAllergens = object.get("allergens_hierarchy").getAsJsonArray();
            foodInfo.allergens = FillInformationInTab(tempAllergens, 3);
        }

    }

    private String GetValue(String key, JsonObject object)
    {
        String content = "";
        if(object.has(key))
            content = object.get(key).getAsString();
        return content;
    }

    private String FillInformationInTab(JsonArray jsonArray, int indexStartString)
    {
        String content = "";
        for(int i =0; i < jsonArray.size(); i++)
        {
            String tempAllergen = jsonArray.get(i).getAsString().substring(indexStartString);
            if(i < jsonArray.size() - 1)
                content += tempAllergen + ", ";
            else
                content += tempAllergen;
        }
        return content;
    }

    public interface MyListener {
        // you can define any parameter as per your requirement
        public void OnComplete(boolean isSuccessful, FoodDescription foodInfo);
        public void OnManyComplete(boolean isAllSuccessful,List<FoodDescription> listFoodInfo);
    }
}