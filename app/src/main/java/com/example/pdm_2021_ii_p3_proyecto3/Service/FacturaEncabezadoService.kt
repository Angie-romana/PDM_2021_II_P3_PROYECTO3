package com.example.pdm_2021_ii_p3_proyecto3.Service

import android.widget.EditText
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.FacturaEncabezadoCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface FacturaEncabezadoService {

    @GET("facturaencabezado")
    fun listFacturasEncabezados(): Call<List<FacturaEncabezadoCollectionItem>>
    @GET("facturaencabezado/id/{id}")
    fun getFacturasEncabezadoById(@Path("id") id: EditText): Call<FacturaEncabezadoCollectionItem>
    @Headers("Content-Type: application/json")
    @POST("facturaencabezado/addfacturaencabezado")
    fun addFacturasEncabezado(@Body facturaEncabezado: FacturaEncabezadoCollectionItem): Call<FacturaEncabezadoCollectionItem>
    @Headers("Content-Type: application/json")
    @PUT("facturaencabezado")
    fun updateFacturasEncabezado(@Body facturaEncabezado: FacturaEncabezadoCollectionItem): Call<FacturaEncabezadoCollectionItem>
    @DELETE("facturaencabezado/delete/{id}")
    fun deleteFacturasEncabezado(@Path("id") id: Long): Call<ResponseBody>
}