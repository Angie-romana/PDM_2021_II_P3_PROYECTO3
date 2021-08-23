package com.example.pdm_2021_ii_p3_proyecto3.Service

import android.widget.EditText
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.CasoDataCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface CasoService {
    @GET("caso")
    fun listCaso(): Call<List<CasoDataCollectionItem>>
    @GET("caso/id/{id}")
    fun getCasoById(@Path("id") id: EditText): Call<CasoDataCollectionItem>
    @Headers("Content-Type: application/json")
    @POST("caso/addcaso")
    fun addCaso(@Body casoData: CasoDataCollectionItem): Call<CasoDataCollectionItem>
    @Headers("Content-Type: application/json")
    @PUT("caso")
    fun updateCaso(@Body casoData: CasoDataCollectionItem): Call<CasoDataCollectionItem>
    @DELETE("caso/delete/{id}")
    fun deleteCaso(@Path("id") id: Long): Call<ResponseBody>
}