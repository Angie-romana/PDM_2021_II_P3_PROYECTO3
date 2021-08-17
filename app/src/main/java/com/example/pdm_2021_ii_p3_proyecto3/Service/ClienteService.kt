package com.example.pdm_2021_ii_p3_proyecto3.Service

import android.widget.EditText
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.ClienteDataCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ClienteService {

    @GET("cliente")
    fun listClientes(): Call<List<ClienteDataCollectionItem>>
    @GET("cliente/id/{id}")
    fun getClienteById(@Path("id") id: EditText): Call<ClienteDataCollectionItem>
    @Headers("Content-Type: application/json")
    @POST("cliente/addcliente")
    fun addCliente(@Body cliente: ClienteDataCollectionItem): Call<ClienteDataCollectionItem>
    @Headers("Content-Type: application/json")
    @PUT("cliente")
    fun updateCliente(@Body cliente: ClienteDataCollectionItem): Call<ClienteDataCollectionItem>
    @DELETE("cliente/delete/{id}")
    fun deleteCliente(@Path("id") id: Long): Call<ResponseBody>

}