package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.AudienciaDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.CasoDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.RestApiError
import com.example.pdm_2021_ii_p3_proyecto3.Service.AudienciaService
import com.example.pdm_2021_ii_p3_proyecto3.Service.CasoService
import com.example.pdm_2021_ii_p3_proyecto3.Service.RestEngine
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_audiencia_detalle.*
import kotlinx.android.synthetic.main.activity_caso.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CasoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caso)
    }

    private fun callServiceDeleteCaso() {
        val casoService: CasoService = RestEngine.buildService().create(CasoService::class.java)
        var result: Call<ResponseBody> = casoService.deleteCaso(txtIdCaso)

        result.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@CasoActivity,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CasoActivity,"DELETE", Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@CasoActivity,"Sesion expirada", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@CasoActivity,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
    private fun callServicePutCaso() {
        val fecha = "2021-04-11"
        val casoInfo = CasoDataCollectionItem(
            idcaso = 0,
            tipocaso = txtTipoCaso.text.toString(),
            sentenciacaso = txtSentencia.text.toString(),
            idcliente = 0,
            idservicio = 0,
            estadocaso = txtEstadoCaso.text.toString()
        )

        val retrofit = RestEngine.buildService().create(CasoService::class.java)
        var result: Call<CasoDataCollectionItem> = retrofit.updateCaso(casoInfo)

        result.enqueue(object : Callback<CasoDataCollectionItem> {
            override fun onFailure(call: Call<CasoDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@CasoActivity, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<CasoDataCollectionItem>,
                response: Response<CasoDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val updatedCaso = response.body()!!
                    Toast.makeText(
                        this@CasoActivity,
                        "OK" + response.body()!!.idcaso,
                        Toast.LENGTH_LONG
                    ).show()
                } else if (response.code() == 401) {
                    Toast.makeText(this@CasoActivity, "Sesion expirada", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@CasoActivity, "Fallo al traer el item", Toast.LENGTH_LONG)
                        .show()
                }
            }

        })
    }
        private fun callServiceGetCaso() {
            val casoService:CasoService = RestEngine.buildService().create(CasoService::class.java)
            var result: Call<CasoDataCollectionItem> = casoService.getCasoById(txtIdCaso)

            result.enqueue(object :  Callback<CasoDataCollectionItem> {
                override fun onFailure(call: Call<CasoDataCollectionItem>, t: Throwable) {
                    Toast.makeText(this@CasoActivity,"Error",Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<CasoDataCollectionItem>,
                    response: Response<CasoDataCollectionItem>
                ) {
                    Toast.makeText(this@CasoActivity,"OK"+response.body()!!.idcaso,Toast.LENGTH_LONG).show()
                }


            })
        }
    private fun callServicePostCaso() {
        val fecha = "2021-04-10"
        val casoInfo = CasoDataCollectionItem(
            idcaso = 0,
            tipocaso = txtTipoCaso.text.toString(),
            sentenciacaso = txtSentencia.text.toString(),
            idcliente = 0,
            idservicio = 0,
            estadocaso = txtEstadoCaso.text.toString()

        )
        addCaso(casoInfo) {
            if (it?.idcaso!= null) {
                Toast.makeText(this@CasoActivity,"OK"+it?.idcaso,Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@CasoActivity,"Error",Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun callServiceGetCasos() {
        val casoService:CasoService = RestEngine.buildService().create(CasoService::class.java)
        var result: Call<List<CasoDataCollectionItem>> = casoService.listCaso()

        result.enqueue(object :  Callback<List<CasoDataCollectionItem>> {
            override fun onFailure(call: Call<List<CasoDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@CasoActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<CasoDataCollectionItem>>,
                response: Response<List<CasoDataCollectionItem>>
            ) {
                Toast.makeText(this@CasoActivity,"OK"+response.body()!!.get(0).idcaso,Toast.LENGTH_LONG).show()
            }
        })
    }

    fun addCaso(casoData: CasoDataCollectionItem, onResult: (CasoDataCollectionItem?) -> Unit){
        val retrofit = RestEngine.buildService().create(CasoService::class.java)
        var result: Call<CasoDataCollectionItem> = retrofit.addCaso(casoData)

        result.enqueue(object : Callback<CasoDataCollectionItem> {
            override fun onFailure(call: Call<CasoDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<CasoDataCollectionItem>,
                                    response: Response<CasoDataCollectionItem>) {
                if (response.isSuccessful) {
                    val addedCaso = response.body()!!
                    onResult(addedCaso)
                }

                else if (response.code() == 500){

                    val errorResponse = Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)

                    Toast.makeText(this@CasoActivity,errorResponse.errorDetails,Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@CasoActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }

        }
        )
    }

    fun String.toDate(format: String, locale: Locale = Locale.getDefault()): Date = SimpleDateFormat(format, locale).parse(this)
}




