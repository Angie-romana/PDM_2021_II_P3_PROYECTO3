package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.AudienciaDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.RestApiError
import com.example.pdm_2021_ii_p3_proyecto3.Service.AudienciaService
import com.example.pdm_2021_ii_p3_proyecto3.Service.RestEngine
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_audiencia_detalle.*
import kotlinx.android.synthetic.main.activity_cai.*
import kotlinx.android.synthetic.main.activity_cliente.*
import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
class AudienciaDetalleActivity : AppCompatActivity() {
    var array = ArrayList<String>()
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audiencia_detalle)
        callServiceGetAudiencias()
        btnGuardarAudiencia.setOnClickListener { v-> callServicePostAudiencia() }
        btnActualizarAudiencia.setOnClickListener { v -> actualizarAudiencia(v) }
        btnEliminarAudiencia.setOnClickListener { v-> borrarAudiencia(v) }
    }

    private fun callServiceGetAudiencias() {
        val audienciaService: AudienciaService = RestEngine.buildService().create(AudienciaService::class.java)
        var result: Call<List<AudienciaDataCollectionItem>> = audienciaService.listAudiencia()

        result.enqueue(object : Callback<List<AudienciaDataCollectionItem>> {
            override fun onFailure(call: Call<List<AudienciaDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@AudienciaDetalleActivity,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<AudienciaDataCollectionItem>>,
                response: Response<List<AudienciaDataCollectionItem>>
            ) {
                array.clear()
                var arrayAdapter: ArrayAdapter<*>
                arrayAdapter = ArrayAdapter(this@AudienciaDetalleActivity,android.R.layout.simple_list_item_1,array)
                lstAudiencia.adapter = arrayAdapter

                array.add("Todas las audiencias")
                array.add("Id caso|Fecha|Id juzgado|Descripcion")
                for(i in 0..(response.body()!!.size-1)){
                    array.add(response.body()!!.get(i).idcaso.toString() + "|" + response.body()!!.get(i).fechaaudiencia + "|"
                              + response.body()!!.get(i).idjuzgado+ "|" + response.body()!!.get(i).descripcionaudiencia+ "|")
                    val arrayAdapter: ArrayAdapter<*>
                    arrayAdapter = ArrayAdapter(this@AudienciaDetalleActivity,android.R.layout.simple_list_item_1,array)
                    lstAudiencia.adapter = arrayAdapter
                }

            }
        })
    }//
    private fun callServicePostAudiencia() {

        val audienciaInfo = AudienciaDataCollectionItem(
            idcaso = 0, // Este se pone asi porque es automatico
          fechaaudiencia = txtFechaAudiencia.text.toString(),
            idjuzgado = txtIdJuzgado.text.toString().substring(0,1).toLong(),
           descripcionaudiencia = txtDescripAudie.text.toString()

        )
        addAudiencia(audienciaInfo) {
            if (it?.idcaso != null) {
                callServiceGetAudiencias()
                Toast.makeText(this,"OK", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this,"Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun addAudiencia(audienciaData: AudienciaDataCollectionItem, onResult: (AudienciaDataCollectionItem?) -> Unit){
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val retrofit = RestEngine.buildService().create(AudienciaService::class.java)
        var result: Call<AudienciaDataCollectionItem> = retrofit.addAudiencia(audienciaData)
        result.enqueue(object : Callback<AudienciaDataCollectionItem> {
            override fun onFailure(call: Call<AudienciaDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<AudienciaDataCollectionItem>,
                                    response: Response<AudienciaDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val addedAudiencia = response.body()!!
                    onResult(addedAudiencia)
                }
                /*else if (response.code() == 401){
                    Toast.makeText(this@MainActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }*/
                else if (response.code() == 500){
                    //val gson = Gson()
                    //val type = object : TypeToken<RestApiError>() {}.type
                    //var errorResponse1: RestApiError? = gson.fromJson(response.errorBody()!!.charStream(), type)
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

    private fun estaVacio():Boolean{
        if(txtIdcaso.text.toString().isEmpty()) {
            txtIdcaso.error ="Debe rellenar el id del caso"
            return true
        }else if(txtFechaAudiencia.text.toString().isEmpty()){
            txtFechaAudiencia.error = "Debe rellenar la fecha"
            return true
        }
        if(txtIdJuzgado.text.toString().isEmpty()) {
            txtIdJuzgado.error ="Debe rellenar el id del juzgado"
            return true
        }
        if(txtDescripAudie.text.toString().isEmpty()) {
            txtDescripAudie.error ="Debe rellenar la descripcion de la audiencia"
            return true
        }

        return false
    }

    private fun noLoSuficienteLargo():Boolean{
        if(txtIdcaso.text.toString().length != 15) {
            txtIdcaso.error ="El id del caso no puede ser distinto a 13 dígitos, no olvide ingresar los guiones"
            return true
        }else if(txtFechaAudiencia.text.toString().length < 3){
            txtFechaAudiencia.error = "La fecha no puede ser menor a 3 caracteres"
            return true
        }
        if(txtIdJuzgado.text.toString().length != 15) {
            txtIdJuzgado.error ="El id del juzgado no puede ser distinto a 13 dígitos, no olvide ingresar los guiones"
            return true
        }
        if(txtDescripAudie.text.toString().length <3) {
            txtDescripAudie.error ="La descripcion no puede ser menor a 3 caracteres"
            return true
        }

        return false
    }

    fun actualizarAudiencia(view: View) {
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del caso")
        builder.setMessage("Por favor ingrese el ID del caso a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_audiencia, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServicePutAudiencia(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()
    }

    private fun callServicePutAudiencia(idCaso:Long) {

        val audienciaInfo = AudienciaDataCollectionItem(
            idcaso = txtIdCaso.text.toString().substring(0,1).toLong(), // Este se pone asi porque es automatico
            fechaaudiencia = txtFechaAudiencia.text.toString(),
            idjuzgado = txtIdJuzgado.text.toString().substring(0,1).toLong(),
            descripcionaudiencia = txtDescripAudie.text.toString()
        )

        val retrofit = RestEngine.buildService().create(AudienciaService::class.java)
        var result: Call<AudienciaDataCollectionItem> = retrofit.updateAudiencia(audienciaInfo)
        callServiceGetAudiencias()
        result.enqueue(object : Callback<AudienciaDataCollectionItem> {
            override fun onFailure(call: Call<AudienciaDataCollectionItem>, t: Throwable) {

            }

            override fun onResponse(call: Call<AudienciaDataCollectionItem>,
                                    response: Response<AudienciaDataCollectionItem>) {
                if (response.isSuccessful) {
                    callServiceGetAudiencias()
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

    fun borrarAudiencia(view:View){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del caso")
        builder.setMessage("Por favor ingrese el ID del caso a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_audiencia, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServiceDeleteAudiencia(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()
    }

    private fun callServiceDeleteAudiencia(idCaso: Long) {
        val audienciaService:AudienciaService = RestEngine.buildService().create(AudienciaService::class.java)
        var result: Call<ResponseBody> = audienciaService.deleteAudiencia(idCaso)

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


}