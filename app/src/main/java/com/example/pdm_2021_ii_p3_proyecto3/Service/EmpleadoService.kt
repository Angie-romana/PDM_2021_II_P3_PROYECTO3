package com.example.pdm_2021_ii_p3_proyecto3.Service

import android.widget.EditText
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.EmpleadoDataCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface EmpleadoService {
    @GET("empleado")
    fun listEmpleado(): Call<List<EmpleadoDataCollectionItem>>
    @GET("empleado/id/{id}")
    fun getEmpleadoById(@Path("id") id: EditText): Call<EmpleadoDataCollectionItem>
    @Headers("Content-Type: application/json")
    @POST("empleado/addempleado")
    fun addEmpleado(@Body caiData: EmpleadoDataCollectionItem): Call<EmpleadoDataCollectionItem>
    @Headers("Content-Type: application/json")
    @PUT("empleado")
    fun updateEmpleado(@Body caiData: EmpleadoDataCollectionItem): Call<EmpleadoDataCollectionItem>
    @DELETE("empleado/delete/{id}")
    fun deleteEmpleado(@Path("id") id: EditText): Call<ResponseBody>
}