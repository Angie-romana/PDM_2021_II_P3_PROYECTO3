package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.AudienciaDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.RestApiError
import com.example.pdm_2021_ii_p3_proyecto3.Service.AudienciaService
import com.example.pdm_2021_ii_p3_proyecto3.Service.RestEngine
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_audiencia_detalle.*

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
class AudienciaDetalleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audiencia2)
        val botonGetId = findViewById<Button>(R.id.btnGetId)
        botonGetId.setOnClickListener {v -> callServiceGetAudiencia()}
        val botonConsumir = findViewById<Button>(R.id.btnGetAll)
        botonConsumir.setOnClickListener {v -> callServiceGetAudiencias()}
        val botonPostear = findViewById<Button>(R.id.btnPostear)
        botonPostear.setOnClickListener { v-> callServicePostAudiencia()}
        val botonPut = findViewById<Button>(R.id.btnPut)
        botonPut.setOnClickListener { v-> callServicePutAudiencia()}
        val botonDelete = findViewById<Button>(R.id.btnDelete)
        botonDelete.setOnClickListener { v-> callServiceDeleteAudiencia()}
    }
    private fun callServiceDeleteAudiencia() {
        val audienciaService:AudienciaService = RestEngine.buildService().create(AudienciaService::class.java)
        var result: Call<ResponseBody> = audienciaService.deleteAudiencia(txtIdCaso)

        result.enqueue(object :  Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@AudienciaDetalleActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AudienciaDetalleActivity,"DELETE",Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@AudienciaDetalleActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@AudienciaDetalleActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun callServicePutAudiencia() {
        val fecha = "2021-04-11"
        val audienciaInfo = AudienciaDataCollectionItem(
            idcaso = txtIdCaso,
            fechaaudiencia = txtFechaAudiencia,
            idjuzgado = txtIdJuzgado,
            descripcionaudiencia = txtDescripAudie

        )

        val retrofit = RestEngine.buildService().create(AudienciaService::class.java)
        var result: Call<AudienciaDataCollectionItem> = retrofit.updateAudiencia(audienciaInfo)

        result.enqueue(object : Callback<AudienciaDataCollectionItem> {
            override fun onFailure(call: Call<AudienciaDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@AudienciaDetalleActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<AudienciaDataCollectionItem>,
                                    response: Response<AudienciaDataCollectionItem>) {
                if (response.isSuccessful) {
                    val updatedPerson = response.body()!!
                    Toast.makeText(this@AudienciaDetalleActivity,"OK"+response.body()!!.idcaso,Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@AudienciaDetalleActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@AudienciaDetalleActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }

        })
    }
    private fun callServiceGetAudiencia() {
        val audienciaService:AudienciaService = RestEngine.buildService().create(AudienciaService::class.java)
        var result: Call<AudienciaDataCollectionItem> = audienciaService.getAudienciaById(txtIdCaso)

        result.enqueue(object :  Callback<AudienciaDataCollectionItem> {
            override fun onFailure(call: Call<AudienciaDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@AudienciaDetalleActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<AudienciaDataCollectionItem>,
                response: Response<AudienciaDataCollectionItem>
            ) {
                Toast.makeText(this@AudienciaDetalleActivity,"OK"+response.body()!!.idcaso,Toast.LENGTH_LONG).show()
            }
        })
    }
    private fun callServicePostAudiencia() {
        val fecha = "2021-04-10"
        val audienciaInfo = AudienciaDataCollectionItem(
            idcaso = txtIdCaso,
           fechaaudiencia = txtFechaAudiencia,
            idjuzgado = txtIdJuzgado,
            descripcionaudiencia = txtDescripAudie

        )
        addAudiencia(audienciaInfo) {
            if (it?.idcaso!= null) {
                Toast.makeText(this@AudienciaDetalleActivity,"OK"+it?.idcaso,Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@AudienciaDetalleActivity,"Error",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun callServiceGetAudiencias() {
        val audienciaService:AudienciaService = RestEngine.buildService().create(AudienciaService::class.java)
        var result: Call<List<AudienciaDataCollectionItem>> = audienciaService.listAudiencia()

        result.enqueue(object :  Callback<List<AudienciaDataCollectionItem>> {
            override fun onFailure(call: Call<List<AudienciaDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@AudienciaDetalleActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<AudienciaDataCollectionItem>>,
                response: Response<List<AudienciaDataCollectionItem>>
            ) {
                Toast.makeText(this@AudienciaDetalleActivity,"OK"+response.body()!!.get(0).idcaso,Toast.LENGTH_LONG).show()
            }
        })
    }
    fun addAudiencia(audienciaData: AudienciaDataCollectionItem, onResult: (AudienciaDataCollectionItem?) -> Unit){
        val retrofit = RestEngine.buildService().create(AudienciaService::class.java)
        var result: Call<AudienciaDataCollectionItem> = retrofit.addAudiencia(audienciaData)

        result.enqueue(object : Callback<AudienciaDataCollectionItem> {
            override fun onFailure(call: Call<AudienciaDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<AudienciaDataCollectionItem>,
                                    response: Response<AudienciaDataCollectionItem>) {
                if (response.isSuccessful) {
                    val addedAudiencia = response.body()!!
                    onResult(addedAudiencia)
                }

                else if (response.code() == 500){

                    val errorResponse = Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)

                    Toast.makeText(this@AudienciaDetalleActivity,errorResponse.errorDetails,Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@AudienciaDetalleActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }

        }
        )
    }

    fun String.toDate(format: String, locale: Locale = Locale.getDefault()): Date = SimpleDateFormat(format, locale).parse(this)
}

