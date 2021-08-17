package com.example.pdm_2021_ii_p3_proyecto3.Service

import android.widget.EditText
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.ServicioDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.SucursalDataCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface SucursalService {
    @GET("sucursal")
    fun listSucursal(): Call<List<SucursalDataCollectionItem>>
    @GET("sucursal/id/{id}")
    fun getSucursalById(@Path("id") id: EditText): Call<SucursalDataCollectionItem>
    @Headers("Content-Type: application/json")
    @POST("sucursal/addsucursal")
    fun addSucursal(@Body caiData: SucursalDataCollectionItem): Call<SucursalDataCollectionItem>
    @Headers("Content-Type: application/json")
    @PUT("sucursal")
    fun updateSucursal(@Body caiData: SucursalDataCollectionItem): Call<SucursalDataCollectionItem>
    @DELETE("sucursal/delete/{id}")
    fun deleteSucursal(@Path("id") id: Long): Call<ResponseBody>
}