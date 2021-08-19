package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.ExpedienteDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.CasoDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.RestApiError
import com.example.pdm_2021_ii_p3_proyecto3.Service.CasoService
import com.example.pdm_2021_ii_p3_proyecto3.Service.ExpedienteService
import com.example.pdm_2021_ii_p3_proyecto3.Service.RestEngine
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_abogado.*
import kotlinx.android.synthetic.main.activity_cliente.*
import kotlinx.android.synthetic.main.activity_expediente.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ExpedienteActivity : AppCompatActivity() {
    var array = ArrayList<String>()
    var idCaso = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expediente)
        llenarSpinner()
        callServiceGetExpedientes()
        btnGuardarExp.setOnClickListener { v-> callServicePostExpediente() }
        btnActualizarExp.setOnClickListener { v -> actualizarExpediente(v) }
        btnBorrarExp.setOnClickListener { v-> borrarExpediente(v) }
    }
    private fun llenarSpinner(){
        val arrayAdapter: ArrayAdapter<*>
        idCaso.add("Seleccione el caso")

        val casosService: CasoService = RestEngine.buildService().create(CasoService::class.java)
        var result: Call<List<CasoDataCollectionItem>> = casosService.listCaso()

        result.enqueue(object : Callback<List<CasoDataCollectionItem>> {
            override fun onFailure(call: Call<List<CasoDataCollectionItem>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<List<CasoDataCollectionItem>>,
                response: Response<List<CasoDataCollectionItem>>
            ) {
                for(i in 0..(response.body()!!.size-1)){
                    idCaso.add(response.body()!!.get(i).idcaso.toString() + ". " + response.body()!!.get(i).sentenciacaso)
                }

            }
        })
        arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,idCaso)
        spnIdCasoExpediente.adapter = arrayAdapter
    }

    private fun callServiceGetExpedientes() {

        val expedienteService: ExpedienteService = RestEngine.buildService().create(ExpedienteService::class.java)
        var result: Call<List<ExpedienteDataCollectionItem>> = expedienteService.listExpedientes()
        var arrayAdapter: ArrayAdapter<*>
        result.enqueue(object : Callback<List<ExpedienteDataCollectionItem>> {
            override fun onFailure(call: Call<List<ExpedienteDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@ExpedienteActivity,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<ExpedienteDataCollectionItem>>,
                response: Response<List<ExpedienteDataCollectionItem>>
            ) {

                arrayAdapter = ArrayAdapter(this@ExpedienteActivity,android.R.layout.simple_list_item_1,array)
                lvwPrincipalExpediente.adapter = arrayAdapter
                array.clear()

                array.add("Todos los expedientes")
                array.add("ID|Entidad|Número de expediente|ID del caso")
                for(i in 0..(response.body()!!.size-1)){
                    array.add(response.body()!!.get(i).idexpediente.toString() + "|" + response.body()!!.get(i).entidad
                            + "|" + response.body()!!.get(i).numexpediente + "|" + response.body()!!.get(i).idcaso)

                    arrayAdapter = ArrayAdapter(this@ExpedienteActivity,android.R.layout.simple_list_item_1,array)
                    lvwPrincipalExpediente.adapter = arrayAdapter
                }

            }
        })
    }

    private fun callServiceGetCasos(){
        val casosService: CasoService = RestEngine.buildService().create(CasoService::class.java)
        var result: Call<List<CasoDataCollectionItem>> = casosService.listCaso()

        result.enqueue(object : Callback<List<CasoDataCollectionItem>> {
            override fun onFailure(call: Call<List<CasoDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@ExpedienteActivity,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<CasoDataCollectionItem>>,
                response: Response<List<CasoDataCollectionItem>>
            ) {
                for(i in 0..(response.body()!!.size-1)){
                    idCaso.add(response.body()!!.get(i).idcaso.toString() + "|" + response.body()!!.get(i).sentenciacaso)
                    val arrayAdapter: ArrayAdapter<*>
                    arrayAdapter = ArrayAdapter(this@ExpedienteActivity,android.R.layout.simple_list_item_1,idCaso)
                    spnIdCasoExpediente.adapter = arrayAdapter
                }

            }
        })
    }

    private fun callServicePostExpediente() {
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val clienteInfo = ExpedienteDataCollectionItem(
            idexpediente = 0, // Este se pone asi porque es automatico
            entidad = txtEntidadExpediente.text.toString(),
            numexpediente = txtNumExpediente.text.toString(),
            idcaso = spnIdCasoExpediente.selectedItem.toString().substring(0,1).toLong()
        )
        addExpediente(clienteInfo) {
            if (it?.idexpediente != null) {
                callServiceGetExpedientes()
                Toast.makeText(this,"OK", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this,"Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun addExpediente(expedienteData: ExpedienteDataCollectionItem, onResult: (ExpedienteDataCollectionItem?) -> Unit){
        val retrofit = RestEngine.buildService().create(ExpedienteService::class.java)
        var result: Call<ExpedienteDataCollectionItem> = retrofit.addExpediente(expedienteData)
        result.enqueue(object : Callback<ExpedienteDataCollectionItem> {
            override fun onFailure(call: Call<ExpedienteDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<ExpedienteDataCollectionItem>,
                                    response: Response<ExpedienteDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val addedExpediente = response.body()!!
                    onResult(addedExpediente)
                }
                /*else if (response.code() == 401){
                    Toast.makeText(this@MainActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }*/
                else if (response.code() == 500){
                    //val gson = Gson()
                    //val type = object : TypeToken<RestApiError>() {}.type
                    //var errorResponse1: RestApiError? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    val errorResponse = Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)


                    Toast.makeText(this@ExpedienteActivity,errorResponse.errorDetails,Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@ExpedienteActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }

        }
        )
    }

    private fun estaVacio():Boolean{
        if(spnIdCasoExpediente.selectedItem.equals("Seleccione el caso")) {
            Toast.makeText(this@ExpedienteActivity,"Debe seleccionar un caso",Toast.LENGTH_LONG).show()
            return true
        }else if(txtEntidadExpediente.text.toString().isEmpty()){
            txtEntidadExpediente.error = "Debe rellenar la entidad"
            return true
        }
        if(txtNumExpediente.text.toString().isEmpty()) {
            txtNumExpediente.error ="Debe rellenar el número de expediente"
            return true
        }
        return false
    }

    private fun noLoSuficienteLargo():Boolean{
        if(txtEntidadExpediente.text.toString().length < 5) {
            txtEntidadExpediente.error ="La entidad no puede ser menor a 5 caracteres"
            return true
        }else if(txtNumExpediente.text.toString().length < 8){
            txtNumExpediente.error = "El número de expediente no puede ser menor a 3 caracteres"
            return true
        }
        return false
    }

    fun actualizarExpediente(view: View) {
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del expediente")
        builder.setMessage("Por favor ingrese el ID del expediente a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_expediente, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServicePutExpediente(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()
    }

    private fun callServicePutExpediente(idExpediente:Long) {
        val expedienteInfo = ExpedienteDataCollectionItem(
            idexpediente = idExpediente,
            entidad = txtEntidadExpediente.text.toString(),
            numexpediente = txtNumExpediente.text.toString(),
            idcaso = spnIdCasoExpediente.selectedItem.toString().substring(0,1).toLong()
        )
        val retrofit = RestEngine.buildService().create(ExpedienteService::class.java)
        var result: Call<ExpedienteDataCollectionItem> = retrofit.updateExpediente(expedienteInfo)
        result.enqueue(object : Callback<ExpedienteDataCollectionItem> {
            override fun onFailure(call: Call<ExpedienteDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@ExpedienteActivity,"Error",Toast.LENGTH_LONG).show()
            }
            override fun onResponse(call: Call<ExpedienteDataCollectionItem>,
                                    response: Response<ExpedienteDataCollectionItem>) {
                if (response.isSuccessful) {
                    val updatedPerson = response.body()!!
                    Toast.makeText(this@ExpedienteActivity,"OK",Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@ExpedienteActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@ExpedienteActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    fun borrarExpediente(view:View){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del expediente")
        builder.setMessage("Por favor ingrese el ID del expediente a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_expediente, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServiceDeleteExpediente(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()
    }

    private fun callServiceDeleteExpediente(idExpediente: Long) {
        val expedienteService: ExpedienteService = RestEngine.buildService().create(ExpedienteService::class.java)
        var result: Call<ResponseBody> = expedienteService.deleteExpediente(idExpediente)

        result.enqueue(object :  Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ExpedienteActivity,"Error",Toast.LENGTH_LONG).show()
            }
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ExpedienteActivity,"DELETE",Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@ExpedienteActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@ExpedienteActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}