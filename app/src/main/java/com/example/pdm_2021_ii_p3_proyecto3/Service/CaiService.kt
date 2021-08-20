package com.example.pdm_2021_ii_p3_proyecto3.Service

import android.widget.EditText

import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.CaiDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.EmpleadoDataCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface CaiService {
    @GET("cai")
    fun listCai(): Call<List<CaiDataCollectionItem>>
    @GET("cai/id/{id}")
    fun getCaiById(@Path("id") id: EditText): Call<CaiDataCollectionItem>
    @Headers("Content-Type: application/json")
    @POST("cai/addcai")
    fun addCai(@Body caiData: CaiDataCollectionItem): Call<CaiDataCollectionItem>
    @Headers("Content-Type: application/json")
    @PUT("cai")
    fun updateCai(@Body caiData: CaiDataCollectionItem): Call<CaiDataCollectionItem>
    @DELETE("cai/delete/{id}")
    fun deleteCai(@Path("id") id: EditText): Call<ResponseBody>

}