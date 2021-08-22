package com.example.pdm_2021_ii_p3_proyecto3.Service

import android.widget.EditText
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.CasoEmpleadoDataCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface CasoEmpleadoService {
    @GET("casoempleado")
    fun listCasoEmpleado(): Call<List<CasoEmpleadoDataCollectionItem>>
    @GET("casoempleado/id/{id}")
    fun getCasoEmpleadoById(@Path("id") id: EditText): Call<CasoEmpleadoDataCollectionItem>
    @Headers("Content-Type: application/json")
    @POST("casoempleado/addcasoempleado")
    fun addCasoEmpleado(@Body casoempleadoData: CasoEmpleadoDataCollectionItem): Call<CasoEmpleadoDataCollectionItem>
    @Headers("Content-Type: application/json")
    @PUT("casoempleado")
    fun updateCasoEmpleado(@Body casoempleadoData: CasoEmpleadoDataCollectionItem): Call<CasoEmpleadoDataCollectionItem>
    @DELETE("casoempleado/delete/{id}")
    fun deleteCasoEmpleado(@Path("id") id: Long): Call<ResponseBody>
}