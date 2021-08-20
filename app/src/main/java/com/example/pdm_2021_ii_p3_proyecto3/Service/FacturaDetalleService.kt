package com.example.pdm_2021_ii_p3_proyecto3.Service

import android.widget.EditText
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.FacturaDetalleCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface FacturaDetalleService {

    @GET("facturadetalle")
    fun listFacturasDetalle(): Call<List<FacturaDetalleCollectionItem>>
    @GET("facturadetalle/id/{id}")
    fun getFacturaDetalleById(@Path("id") id: Long): Call<FacturaDetalleCollectionItem>
    @Headers("Content-Type: application/json")
    @POST("facturadetalle/addfacturadetalle")
    fun addFacturaDetalle(@Body facturaDetalleData: FacturaDetalleCollectionItem): Call<FacturaDetalleCollectionItem>
    @Headers("Content-Type: application/json")
    @PUT("facturadetalle")
    fun updateFacturaDetalle(@Body facturaDetalleData: FacturaDetalleCollectionItem): Call<FacturaDetalleCollectionItem>
    @DELETE("facturadetalle/delete/{id}")
    fun deleteFacturaDetalle(@Path("id") id: Long): Call<ResponseBody>
}