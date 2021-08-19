package com.example.pdm_2021_ii_p3_proyecto3.Service

import android.widget.EditText
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.PrecioHistoricoDataCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface PrecioHistoricoService {
    @GET("preciohistorico")
    fun listPrecioHistorico(): Call<List<PrecioHistoricoDataCollectionItem>>
    @GET("preciohistorico/id/{id}")
    fun getPrecioHistoricoById(@Path("id") id: Long): Call<PrecioHistoricoDataCollectionItem>
    @GET("preciohistorico/idservicio/{idservicio}")
    fun getPrecioHistoricoByIdServicio(@Path("idservicio") id: Long): Call<PrecioHistoricoDataCollectionItem>
    @Headers("Content-Type: application/json")
    @POST("preciohistorico/addpreciohistorico")
    fun addPrecioHistorico(@Body precioHistoricoData: PrecioHistoricoDataCollectionItem): Call<PrecioHistoricoDataCollectionItem>
    @Headers("Content-Type: application/json")
    @PUT("preciohistorico")
    fun updatePrecioHistorico(@Body precioHistoricoData: PrecioHistoricoDataCollectionItem): Call<PrecioHistoricoDataCollectionItem>
    @DELETE("preciohistorico/delete/{id}")
    fun deletePrecioHistorico(@Path("id") id: Long): Call<ResponseBody>
}