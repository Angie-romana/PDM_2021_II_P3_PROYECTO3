package com.example.pdm_2021_ii_p3_proyecto3.Service

import android.widget.EditText
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.IndicioDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.ServicioDataCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface IndicioService {
    @GET("indicio")
    fun listIndicio(): Call<List<IndicioDataCollectionItem>>
    @GET("indicio/id/{id}")
    fun getIndicioById(@Path("id") id: EditText): Call<IndicioDataCollectionItem>
    @Headers("Content-Type: application/json")
    @POST("indicio/addindicio")
    fun addIndicio(@Body caiData: IndicioDataCollectionItem): Call<IndicioDataCollectionItem>
    @Headers("Content-Type: application/json")
    @PUT("indicio")
    fun updateIndicio(@Body caiData: IndicioDataCollectionItem): Call<IndicioDataCollectionItem>
    @DELETE("indicio/delete/{id}")
    fun deleteIndicio(@Path("id") id: Long): Call<ResponseBody>
}