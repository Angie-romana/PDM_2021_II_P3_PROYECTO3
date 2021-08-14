package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.CaiDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.RestApiError
import com.example.pdm_2021_ii_p3_proyecto3.Service.CaiService

import com.example.pdm_2021_ii_p3_proyecto3.Service.RestEngine
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_audiencia_detalle.*
import kotlinx.android.synthetic.main.activity_cai.*
import kotlinx.android.synthetic.main.activity_cai.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CaiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cai2)
        val botonGetId = findViewById<Button>(R.id.btnGetIdC)
        botonGetId.setOnClickListener {v -> callServiceGetCai()}
        val botonConsumir = findViewById<Button>(R.id.btnGetAllC)
        botonConsumir.setOnClickListener {v -> callServiceGetCais()}
        val botonPostear = findViewById<Button>(R.id.btnPostearC)
        botonPostear.setOnClickListener { v-> callServicePostCai()}
        val botonPut = findViewById<Button>(R.id.btnPutC)
        botonPut.setOnClickListener { v-> callServicePutCai()}
        val botonDelete = findViewById<Button>(R.id.btnDeleteC)
        botonDelete.setOnClickListener { v-> callServiceDeleteCai()}
    }

    private fun callServiceDeleteCai() {
        val caiService: CaiService = RestEngine.buildService().create(CaiService::class.java)
        var result: Call<ResponseBody> = caiService.deleteCai(txtCAI)

        result.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@CaiActivity,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CaiActivity,"DELETE", Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@CaiActivity,"Sesion expirada", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@CaiActivity,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
    private fun callServicePutCai() {
        val fecha = "2021-04-11"
        val caiInfo = CaiDataCollectionItem(
            cai = txtCAI,
            rangoinicial = txtRangoIni,
            rangofinal = txtRangoFinal,
            fechalimite = txtFechaLimite

        )

        val retrofit = RestEngine.buildService().create(CaiService::class.java)
        var result: Call<CaiDataCollectionItem> = retrofit.updateCai(caiInfo)

        result.enqueue(object : Callback<CaiDataCollectionItem> {
            override fun onFailure(call: Call<CaiDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@CaiActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<CaiDataCollectionItem>,
                                    response: Response<CaiDataCollectionItem>) {
                if (response.isSuccessful) {
                    val updatedPerson = response.body()!!
                    Toast.makeText(this@CaiActivity,"OK"+response.body()!!.cai,Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@CaiActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@CaiActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    private fun callServiceGetCai() {
        val caiService: CaiService = RestEngine.buildService().create(CaiService::class.java)
        var result: Call<CaiDataCollectionItem> = caiService.getCaiById(txtIdCAI)

        result.enqueue(object :  Callback<CaiDataCollectionItem> {
            override fun onFailure(call: Call<CaiDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@CaiActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<CaiDataCollectionItem>,
                response: Response<CaiDataCollectionItem>
            ) {
                Toast.makeText(this@CaiActivity,"OK"+response.body()!!.cai,Toast.LENGTH_LONG).show()
            }
        })
    }
    private fun callServicePostCai() {
        val fecha = "2021-04-10"
        val caiInfo = CaiDataCollectionItem(
            cai = txtCAI,
            rangoinicial = txtRangoIni,
            rangofinal = txtRangoFinal,
            fechalimite = txtFechaLimite

        )
        addCai(caiInfo) {
            if (it?.cai!= null) {
                Toast.makeText(this@CaiActivity,"OK"+it?.cai,Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@CaiActivity,"Error",Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun callServiceGetCais() {
        val CaiService: CaiService = RestEngine.buildService().create(CaiService::class.java)
        var result: Call<List<CaiDataCollectionItem>> = CaiService.listCai()

        result.enqueue(object :  Callback<List<CaiDataCollectionItem>> {
            override fun onFailure(call: Call<List<CaiDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@CaiActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<CaiDataCollectionItem>>,
                response: Response<List<CaiDataCollectionItem>>
            ) {
                Toast.makeText(this@CaiActivity,"OK"+response.body()!!.get(0).cai,Toast.LENGTH_LONG).show()
            }
        })
    }
    fun addCai(caiData: CaiDataCollectionItem, onResult: (CaiDataCollectionItem?) -> Unit){
        val retrofit = RestEngine.buildService().create(CaiService::class.java)
        var result: Call<CaiDataCollectionItem> = retrofit.addCai(caiData)

        result.enqueue(object : Callback<CaiDataCollectionItem> {
            override fun onFailure(call: Call<CaiDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<CaiDataCollectionItem>,
                                    response: Response<CaiDataCollectionItem>) {
                if (response.isSuccessful) {
                    val addedCai = response.body()!!
                    onResult(addedCai)
                }

                else if (response.code() == 500){

                    val errorResponse = Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)

                    Toast.makeText(this@CaiActivity,errorResponse.errorDetails,Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@CaiActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }

        }
        )
    }

    fun String.toDate(format: String, locale: Locale = Locale.getDefault()): Date = SimpleDateFormat(format, locale).parse(this)

}