package com.example.pdm_2021_ii_p3_proyecto3.Service

import android.widget.EditText
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.ExpedienteDataCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ExpedienteService {
    @GET("expediente")
    fun listExpedientes(): Call<List<ExpedienteDataCollectionItem>>
    @GET("expediente/id/{id}")
    fun getExpedienteById(@Path("id") id: EditText): Call<ExpedienteDataCollectionItem>
    @Headers("Content-Type: application/json")
    @POST("expediente/addexpediente")
    fun addExpediente(@Body expedienteData: ExpedienteDataCollectionItem): Call<ExpedienteDataCollectionItem>
    @Headers("Content-Type: application/json")
    @PUT("expediente")
    fun updateExpediente(@Body expedienteData: ExpedienteDataCollectionItem): Call<ExpedienteDataCollectionItem>
    @DELETE("expediente/delete/{id}")
    fun deleteExpediente(@Path("id") id: Long): Call<ResponseBody>
}