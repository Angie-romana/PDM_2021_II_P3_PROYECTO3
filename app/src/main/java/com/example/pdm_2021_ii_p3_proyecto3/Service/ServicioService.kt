package com.example.pdm_2021_ii_p3_proyecto3.Service

import android.widget.EditText
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.EmpleadoDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.ServicioDataCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ServicioService {
    @GET("servicio")
    fun listServicio(): Call<List<ServicioDataCollectionItem>>
    @GET("servicio/id/{id}")
    fun getServicioById(@Path("id") id: EditText): Call<ServicioDataCollectionItem>
    @Headers("Content-Type: application/json")
    @POST("servicio/addservicio")
    fun addServicio(@Body caiData: ServicioDataCollectionItem): Call<ServicioDataCollectionItem>
    @Headers("Content-Type: application/json")
    @PUT("servicio")
    fun updateSercicio(@Body caiData: ServicioDataCollectionItem): Call<ServicioDataCollectionItem>
    @DELETE("servicio/delete/{id}")
    fun deleteServicio(@Path("id") id: Long): Call<ResponseBody>
}
