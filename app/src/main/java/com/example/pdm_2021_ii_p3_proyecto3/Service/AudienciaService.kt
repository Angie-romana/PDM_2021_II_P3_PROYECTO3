package com.example.pdm_2021_ii_p3_proyecto3.Service

import android.widget.EditText
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.AudienciaDataCollectionItem
import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.http.*

interface AudienciaService {
    @GET("audienciadetalle")
    fun listAudiencia(): Call<List<AudienciaDataCollectionItem>>
    @GET("audiencia/id/{id}")
    fun getAudienciaById(@Path("id") id: EditText): Call<AudienciaDataCollectionItem>
    @Headers("Content-Type: application/json")
    @POST("audiencia/addaudiencia")
    fun addAudiencia(@Body personData: AudienciaDataCollectionItem): Call<AudienciaDataCollectionItem>
    @Headers("Content-Type: application/json")
    @PUT("audiencia")
    fun updateAudiencia(@Body audienceData: AudienciaDataCollectionItem): Call<AudienciaDataCollectionItem>
    @DELETE("audiencia/delete/{id}")
    fun deleteAudiencia(@Path("id") id: Long): Call<ResponseBody>
}