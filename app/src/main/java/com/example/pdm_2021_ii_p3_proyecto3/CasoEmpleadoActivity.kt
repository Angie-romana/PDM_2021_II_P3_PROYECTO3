package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.CasoDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.CasoEmpleadoDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.RestApiError
import com.example.pdm_2021_ii_p3_proyecto3.Service.CasoEmpleadoService
import com.example.pdm_2021_ii_p3_proyecto3.Service.CasoService
import com.example.pdm_2021_ii_p3_proyecto3.Service.RestEngine
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_audiencia_detalle.*
import kotlinx.android.synthetic.main.activity_caso.*
import kotlinx.android.synthetic.main.activity_caso_empleado.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CasoEmpleadoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caso_empleado)
    }
    private fun callServiceDeleteCasoEmpleado() {
        val casoempleadoService: CasoEmpleadoService = RestEngine.buildService().create(CasoEmpleadoService::class.java)
        var result: Call<ResponseBody> = casoempleadoService.deleteCasoEmpleado(txtIdCaso)

        result.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@CasoEmpleadoActivity,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CasoEmpleadoActivity,"DELETE", Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@CasoEmpleadoActivity,"Sesion expirada", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@CasoEmpleadoActivity,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun callServicePutCasoEmpleado() {
        val fecha = "2021-04-11"
        val casoempleadoInfo = CasoEmpleadoDataCollectionItem(
            idcaso = txtIdCaso,
           // idempleado = txtIdEmpleado,
            fechainiciotrabajoencaso  = txtFechaInicioT,
            fechafinaltrabajoencaso   = txtFechaFinalT
           // descripcioncasoempleado   =
        )

        val retrofit = RestEngine.buildService().create(CasoEmpleadoService::class.java)
        var result: Call<CasoEmpleadoDataCollectionItem> = retrofit.updateCasoEmpleado(casoempleadoInfo)

        result.enqueue(object : Callback<CasoEmpleadoDataCollectionItem> {
            override fun onFailure(call: Call<CasoEmpleadoDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@CasoEmpleadoActivity, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<CasoEmpleadoDataCollectionItem>,
                response: Response<CasoEmpleadoDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val updatedCasoEmpleado= response.body()!!
                    Toast.makeText(
                        this@CasoEmpleadoActivity,
                        "OK" + response.body()!!.idcaso,
                        Toast.LENGTH_LONG
                    ).show()
                } else if (response.code() == 401) {
                    Toast.makeText(this@CasoEmpleadoActivity, "Sesion expirada", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@CasoEmpleadoActivity, "Fallo al traer el item", Toast.LENGTH_LONG)
                        .show()
                }
            }

        })
    }
    private fun callServiceGetCasoEmpleado() {
        val casoempleadoService:CasoEmpleadoService = RestEngine.buildService().create(CasoEmpleadoService::class.java)
        var result: Call<CasoEmpleadoDataCollectionItem> = casoempleadoService.getCasoEmpleadoById(txtIdCaso)

        result.enqueue(object :  Callback<CasoEmpleadoDataCollectionItem> {
            override fun onFailure(call: Call<CasoEmpleadoDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@CasoEmpleadoActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<CasoEmpleadoDataCollectionItem>,
                response: Response<CasoEmpleadoDataCollectionItem>
            ) {
                Toast.makeText(this@CasoEmpleadoActivity,"OK"+response.body()!!.idcaso,Toast.LENGTH_LONG).show()
            }


        })
    }

    private fun callServicePostCasoEmpleado() {
        val fecha = "2021-04-10"
        val casoempleadoInfo = CasoEmpleadoDataCollectionItem(
            idcaso = txtIdCaso,
            fechainiciotrabajoencaso = txtFechaInicioT,
            fechafinaltrabajoencaso = txtFechaFinalT
        )
        addCasoEmpleado(casoempleadoInfo) {
            if (it?.idcaso!= null) {
                Toast.makeText(this@CasoEmpleadoActivity,"OK"+it?.idcaso,Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@CasoEmpleadoActivity,"Error",Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun callServiceGetCasoEmpleados() {
        val casoempleadoService:CasoEmpleadoService = RestEngine.buildService().create(CasoEmpleadoService::class.java)
        var result: Call<List<CasoEmpleadoDataCollectionItem>> = casoempleadoService.listCasoEmpleado()

        result.enqueue(object :  Callback<List<CasoEmpleadoDataCollectionItem>> {
            override fun onFailure(call: Call<List<CasoEmpleadoDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@CasoEmpleadoActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<CasoEmpleadoDataCollectionItem>>,
                response: Response<List<CasoEmpleadoDataCollectionItem>>
            ) {
                Toast.makeText(this@CasoEmpleadoActivity,"OK"+response.body()!!.get(0).idcaso,Toast.LENGTH_LONG).show()
            }
        })
    }

    fun addCasoEmpleado(casoempleadoData: CasoEmpleadoDataCollectionItem, onResult: (CasoEmpleadoDataCollectionItem?) -> Unit){
        val retrofit = RestEngine.buildService().create(CasoEmpleadoService::class.java)
        var result: Call<CasoEmpleadoDataCollectionItem> = retrofit.addCasoEmpleado(casoempleadoData)

        result.enqueue(object : Callback<CasoEmpleadoDataCollectionItem> {
            override fun onFailure(call: Call<CasoEmpleadoDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<CasoEmpleadoDataCollectionItem>,
                                    response: Response<CasoEmpleadoDataCollectionItem>) {
                if (response.isSuccessful) {
                    val addedCasoEmpleado = response.body()!!
                    onResult(addedCasoEmpleado)
                }

                else if (response.code() == 500){

                    val errorResponse = Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)

                    Toast.makeText(this@CasoEmpleadoActivity,errorResponse.errorDetails,Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@CasoEmpleadoActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }

        }
        )
    }

    fun String.toDate(format: String, locale: Locale = Locale.getDefault()): Date = SimpleDateFormat(format, locale).parse(this)
}

