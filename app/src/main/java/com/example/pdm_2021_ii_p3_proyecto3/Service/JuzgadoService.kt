

package com.example.pdm_2021_ii_p3_proyecto3.Service

import android.widget.EditText
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.JuzgadoDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.ServicioDataCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface JuzgadoService {

    @GET("juzgado")
    fun listJuzgado(): Call<List<JuzgadoDataCollectionItem>>
    @GET("juzgado/id/{id}")
    fun getJuzgadoById(@Path("id") id: EditText): Call<JuzgadoDataCollectionItem>
    @Headers("Content-Type: application/json")
    @POST("juzgado/addjuzgado")
    fun addJuzgado(@Body caiData: JuzgadoDataCollectionItem): Call<JuzgadoDataCollectionItem>
    @Headers("Content-Type: application/json")
    @PUT("juzgado")
    fun updateJuzgado(@Body caiData: JuzgadoDataCollectionItem): Call<JuzgadoDataCollectionItem>
    @DELETE("juzgado/delete/{id}")
    fun deleteJuzgado(@Path("id") id: Long): Call<ResponseBody>

}