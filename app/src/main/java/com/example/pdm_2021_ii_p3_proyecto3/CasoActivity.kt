package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.CasoDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.RestApiError
import com.example.pdm_2021_ii_p3_proyecto3.Service.CasoService
import com.example.pdm_2021_ii_p3_proyecto3.Service.RestEngine
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_audiencia_detalle.*
import kotlinx.android.synthetic.main.activity_cai.*
import kotlinx.android.synthetic.main.activity_caso.*
import kotlinx.android.synthetic.main.activity_caso.txtIdCaso
import kotlinx.android.synthetic.main.activity_caso.txtEstadoCaso
import kotlinx.android.synthetic.main.activity_caso.txtIdCliente
import kotlinx.android.synthetic.main.activity_caso.txtSentencia
import kotlinx.android.synthetic.main.activity_cliente.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CasoActivity : AppCompatActivity() {
    var array = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caso)
        callServiceGetCasos()
        btnGuardarCaso.setOnClickListener { v -> callServicePostCaso() }
        btnActualizarCaso.setOnClickListener { v -> actualizarCaso(v) }
        btnBorrarCaso.setOnClickListener { v -> borrarCaso(v) }
    }

//
    private fun callServiceGetCasos() {
        val casoService: CasoService = RestEngine.buildService().create(CasoService::class.java)
        var result: Call<List<CasoDataCollectionItem>> = casoService.listCaso()

        result.enqueue(object : Callback<List<CasoDataCollectionItem>> {
            override fun onFailure(call: Call<List<CasoDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@CasoActivity, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<CasoDataCollectionItem>>,
                response: Response<List<CasoDataCollectionItem>>
            ) {
                array.clear()
                var arrayAdapter: ArrayAdapter<*>
                arrayAdapter = ArrayAdapter(this@CasoActivity,android.R.layout.simple_list_item_1,array)
                lvwCaso.adapter = arrayAdapter

                array.add("Todos los casos")
                array.add("Id caso|Tipo de caso|Sentencia|Id cliente|Id servicio|Estado caso")
                for (i in 0..(response.body()!!.size - 1)) {
                    array.add(
                        response.body()!!.get(i).idcaso.toString() + "|" + response.body()!!
                            .get(i).tipocaso + "|"
                                + response.body()!!.get(i).sentenciacaso + "|" + response.body()!!
                            .get(i).idcliente + "|" +
                                response.body()!!.get(i).idservicio + "|" + response.body()!!
                            .get(i).estadocaso + "|"
                    )
                    val arrayAdapter: ArrayAdapter<*>
                    arrayAdapter = ArrayAdapter(this@CasoActivity, android.R.layout.simple_list_item_1, array)
                    lvwCaso.adapter = arrayAdapter
                }
            }
        })
    }

    private fun callServicePostCaso() {

        val casoInfo = CasoDataCollectionItem(
            idcaso = 0 ,
            tipocaso = txtIdCai.text.toString(),
            sentenciacaso = txtSentencia.text.toString(),
            idcliente = txtIdCliente.text.toString().substring(0,1).toLong(),
            idservicio = txtIdServicio.text.toString().substring(0,1).toLong(),
            estadocaso = txtEstadoCaso.text.toString()
        )
        addCaso(casoInfo) {
            if (it?.idcaso != null) {
                callServiceGetCasos()
                Toast.makeText(this, "OK", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
            }
        }
    }


    fun addCaso(casoData: CasoDataCollectionItem, onResult: (CasoDataCollectionItem?) -> Unit) {
        if (estaVacio()) {
            return
        }
        if (noLoSuficienteLargo()) {
            return
        }
        val retrofit = RestEngine.buildService().create(CasoService::class.java)
        var result: Call<CasoDataCollectionItem> = retrofit.addCaso(casoData)
        result.enqueue(object : Callback<CasoDataCollectionItem> {
            override fun onFailure(call: Call<CasoDataCollectionItem>, t: Throwable) {
                onResult(null)
            }
//
            override fun onResponse(
                call: Call<CasoDataCollectionItem>,
                response: Response<CasoDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val addedCaso = response.body()!!
                    onResult(addedCaso)
                }
                /*else if (response.code() == 401){
                    Toast.makeText(this@MainActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }*/
                else if (response.code() == 500) {
                    //val gson = Gson()
                    //val type = object : TypeToken<RestApiError>() {}.type
                    //var errorResponse1: RestApiError? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    val errorResponse =
                        Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)


                    Toast.makeText(
                        this@CasoActivity,
                        errorResponse.errorDetails,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this@CasoActivity, "Fallo al traer el item", Toast.LENGTH_LONG)
                        .show()
                }
            }

        }
        )
    }

    fun actualizarCaso(view: View) {
        if (estaVacio()) {
            return
        }
        if (noLoSuficienteLargo()) {
            return
        }
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del caso")
        builder.setMessage("Por favor ingrese el ID del caso a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_caso, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if (editText.text.toString() == "") {
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServicePutCaso(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar") { dialogInterface, i -> return@setNegativeButton }
        builder.show()
    }


    private fun callServicePutCaso(idCaso: Long) {

        val casoInfo = CasoDataCollectionItem(
            idcaso = txtIdCaso.text.toString().substring(0,1).toLong(),
            tipocaso = txtIdCai.text.toString(),
            sentenciacaso = txtSentencia.text.toString(),
            idcliente = txtIdCliente.text.toString().substring(0,1).toLong(),
            idservicio = txtIdServicio.text.toString().substring(0,1).toLong(),
            estadocaso = txtEstadoCaso.text.toString()


        )

        val retrofit = RestEngine.buildService().create(CasoService::class.java)
        var result: Call<CasoDataCollectionItem> = retrofit.updateCaso(casoInfo)
        callServiceGetCasos()
        result.enqueue(object : Callback<CasoDataCollectionItem> {
            override fun onFailure(call: Call<CasoDataCollectionItem>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<CasoDataCollectionItem>,
                response: Response<CasoDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    callServiceGetCasos()
                    val updatedPerson = response.body()!!
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

    fun borrarCaso(view: View) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del caso")
        builder.setMessage("Por favor ingrese el ID del caso a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_caso, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if (editText.text.toString() == "") {
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServiceDeleteCaso(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar") { dialogInterface, i -> return@setNegativeButton }
        builder.show()
    }

    private fun callServiceDeleteCaso(idCaso: Long) {
        val casoService: CasoService = RestEngine.buildService().create(CasoService::class.java)
        var result: Call<ResponseBody> = casoService.deleteCaso(idCaso)

        result.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@CasoActivity, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CasoActivity, "DELETE", Toast.LENGTH_LONG).show()
                } else if (response.code() == 401) {
                    Toast.makeText(this@CasoActivity, "Sesion expirada", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@CasoActivity, "Fallo al traer el item", Toast.LENGTH_LONG)
                        .show()
                }
            }
        })
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private fun noLoSuficienteLargo(): Boolean {

        if (txtIdCaso.text.toString().length <10) {
            txtIdCaso.error =
                "El id del caso no puede ser menor 10, no olvide ingresar los guiones"
            return true
        }
        if (txtTipoCaso.text.toString().length < 3) {
            txtTipoCaso.error = "El tipo de caso no puede ser menor a 3 caracteres"
            return true
        }
        if (txtSentencia.text.toString().length >25) {
            txtSentencia.error = "La sentencia no debe ser mayor a 25 caracteres"
            return true
        }
        if (txtIdCliente.text.toString().length <8) {
            txtIdCliente.error =
                "El id del cliente no puede ser menor a 8 "
            return true
        }
        if (txtIdServicio.text.toString().length < 8) {
            txtIdServicio.error =
                "El id del servicio no puede ser menor a 8, no olvide ingresar los guiones"
            return true
        }
        if (txtEstadoCaso.text.toString().length < 1) {
            txtEstadoCaso.error = "El estado del caso no puede ser menor a 1 caracteres"
            return true
        }

        return false
    }

    private fun estaVacio(): Boolean {
        if (txtIdCaso.text.toString().isEmpty()) {
            txtIdCaso.error = "Debe rellenar el id del caso"
            return true
        } else if (txtIdCai.text.toString().isEmpty()) {
            txtIdCai.error = "Debe rellenar el tipo de caso"
            return true
        }
        if (txtSentencia.text.toString().isEmpty()) {
            txtSentencia.error = "Debe rellenar la sentencia"
            return true
        }
        if (txtIdCliente.text.toString().isEmpty()) {
            txtIdCliente.error = "Debe rellenar el id del cliente"
            return true
        }
        if (txtIdServicio.text.toString().isEmpty()) {
            txtIdServicio.error = "Debe rellenar el id del servicio"
            return true
        }
        if (txtEstadoCaso.text.toString().isEmpty()) {
            txtEstadoCaso.error = "Debe rellenar el estado del caso"
            return true
        }
        return false
    }
}






