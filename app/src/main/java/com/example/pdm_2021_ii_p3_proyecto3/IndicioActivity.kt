package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.*
import com.example.pdm_2021_ii_p3_proyecto3.Service.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_audiencia_detalle.*
import kotlinx.android.synthetic.main.activity_indicio.*
import kotlinx.android.synthetic.main.activity_precio_historico.*
import kotlinx.android.synthetic.main.activity_servicio.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class IndicioActivity : AppCompatActivity() {
    var array = ArrayList<String>()
    var casoElegir = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_indicio)
        callServiceGetCaso()
        callServiceGetIndicio()
        btnGuardarIndicio.setOnClickListener { v -> callServicePostIndicio() }
        btnActualizarIndicio.setOnClickListener {v -> actualizarIndicio(v) }
        btnBorrarIndicio.setOnClickListener { v -> borrarIndicio(v) }

    }
    private fun callServiceGetCaso() {
        val personService: CasoService = RestEngine.buildService().create(CasoService::class.java)
        var result: Call<List<CasoDataCollectionItem>> = personService.listCaso()

        result.enqueue(object :  Callback<List<CasoDataCollectionItem>> {
            override fun onFailure(call: Call<List<CasoDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@IndicioActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<CasoDataCollectionItem>>,
                response: Response<List<CasoDataCollectionItem>>
            ) {
                casoElegir.add("Seleccione el Id Caso")
                for(i in 0..(response.body()!!.size-1)){
                    casoElegir.add(response.body()!!.get(i).idcaso.toString()) //+ "|" + response.body()!!.get(i).tipocaso)
                    val arrayAdapter: ArrayAdapter<*>
                    arrayAdapter = ArrayAdapter(this@IndicioActivity,android.R.layout.simple_list_item_1,casoElegir)
                    spnIdCaso.adapter = arrayAdapter
                }



            }
        })
    }
    private fun callServiceGetIndicio() {
        val personService: IndicioService = RestEngine.buildService().create(
            IndicioService::class.java)
        var result: Call<List<IndicioDataCollectionItem>> = personService.listIndicio()

        result.enqueue(object : Callback<List<IndicioDataCollectionItem>> {
            override fun onFailure(call: Call<List<IndicioDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@IndicioActivity,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<IndicioDataCollectionItem>>,
                response: Response<List<IndicioDataCollectionItem>>
            ) {
                array.add("Todas los Registros de Indicio")
                array.add("ID|ID Caso|Descripcion")
                for(i in 0..(response.body()!!.size-1)){
                    array.add(response.body()!!.get(i).idindicio.toString() + "|" + response.body()!!.get(i).idcaso.toString()+ "|"
                            + response.body()!!.get(i).descripcionindicio)

                    val arrayAdapter: ArrayAdapter<*>
                    arrayAdapter = ArrayAdapter(this@IndicioActivity,android.R.layout.simple_list_item_1,array)
                    lswIndicio.adapter = arrayAdapter
                }

            }
        })
    }
    fun actualizarIndicio(view: View) {
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del Indicio")
        builder.setMessage("Por favor ingrese el ID del indicio a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_indicio, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServicePutIndicio(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()

    }

    private fun callServicePutIndicio(idIndicio:Long) {

        val indicioInfo = IndicioDataCollectionItem(
            idindicio = idIndicio,
            idcaso = spnIdCaso.selectedItem.toString().toLong(),
            descripcionindicio = txtDescripcion.text.toString()

        )

        val retrofit = RestEngine.buildService().create(IndicioService::class.java)
        var result: Call<IndicioDataCollectionItem> = retrofit.updateIndicio(indicioInfo)
        result.enqueue(object : Callback<IndicioDataCollectionItem> {
            override fun onFailure(call: Call<IndicioDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@IndicioActivity,"Error",Toast.LENGTH_LONG).show()
            }
            override fun onResponse(call: Call<IndicioDataCollectionItem>,
                                    response: Response<IndicioDataCollectionItem>) {
                if (response.isSuccessful) {
                    val updatedPerson = response.body()!!
                    Toast.makeText(this@IndicioActivity,"OK"+response.body()!!.descripcionindicio,Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@IndicioActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@IndicioActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun callServicePostIndicio() {

        val  indicioInfo = IndicioDataCollectionItem(
            idindicio = 0, // Este se llena solo para no dejar vacio. Es incremental
            idcaso = spnIdCaso.selectedItem.toString().toLong(),
            descripcionindicio = txtDescripcion.text.toString()

            )
        addIndicio(indicioInfo) {
            if (it?.idindicio != null) {
                Toast.makeText(this@IndicioActivity,"OK", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@IndicioActivity,"Error", Toast.LENGTH_LONG).show()
            }
        }
    }




    fun addIndicio(indicioData: IndicioDataCollectionItem, onResult: (IndicioDataCollectionItem?) -> Unit){
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val retrofit = RestEngine.buildService().create(IndicioService::class.java)
        var result: Call<IndicioDataCollectionItem> = retrofit.addIndicio(indicioData)
        result.enqueue(object : Callback<IndicioDataCollectionItem> {
            override fun onFailure(call: Call<IndicioDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<IndicioDataCollectionItem>,
                                    response: Response<IndicioDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val addedPerson = response.body()!!
                    onResult(addedPerson)
                }
                /*else if (response.code() == 401){
                    Toast.makeText(this@MainActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }*/
                else if (response.code() == 500){
                    //val gson = Gson()
                    //val type = object : TypeToken<RestApiError>() {}.type
                    //var errorResponse1: RestApiError? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    val errorResponse = Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)


                    Toast.makeText(this@IndicioActivity,errorResponse.errorDetails, Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@IndicioActivity,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }

        }
        )

    }
    fun borrar(view: View){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del Servicio")
        builder.setMessage("Por favor ingrese el ID del Servicio a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_abogado, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServiceDeleteServicio(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()

    }

    private fun callServiceDeleteServicio(idServicio: Long) {
        val servicioService:ServicioService = RestEngine.buildService().create(ServicioService::class.java)
        var result: Call<ResponseBody> = servicioService.deleteServicio(idServicio)

        result.enqueue(object :  Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@IndicioActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@IndicioActivity,"DELETE",Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@IndicioActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@IndicioActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }
        })
    }



    /* fun actualizarIndicio(view: View) {
   *//*     if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }*//*
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del Indicio")
        builder.setMessage("Por favor ingrese el ID del Indicio a buscar, en caso de querer verificar nuevamente presione el botón \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_indicio, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServicePutIndicio(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()

    }

    private fun callServicePutIndicio(idIndicio:Long) {


        val indicioInfo = IndicioDataCollectionItem(
          idindicio = idIndicio,
            idcaso =spnPrecioServicio.selectedItem.toString().toLong(),
            descripcionindicio = txtDescripcion.toString()


        )

        val retrofit = RestEngine.buildService().create(IndicioService::class.java)
        var result: Call<IndicioDataCollectionItem> = retrofit.updateIndicio(indicioInfo)
        result.enqueue(object : Callback<IndicioDataCollectionItem> {
            override fun onFailure(call: Call<IndicioDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@IndicioActivity,"Error", Toast.LENGTH_LONG).show()
            }
            override fun onResponse(call: Call<IndicioDataCollectionItem>,
                                    response: Response<IndicioDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val updatedPerson = response.body()!!
                    Toast.makeText(this@IndicioActivity,"OK",
                        Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@IndicioActivity,"Sesion expirada", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@IndicioActivity,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun callServicePostIndicio(){
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val  indicioInfo = IndicioDataCollectionItem(
            idindicio = 0, // Este se llena solo para no dejar vacio. Es incremental
            idcaso =spnPrecioServicio.selectedItem.toString().toLong(),
            descripcionindicio = txtDescripcion.toString()
        )
        addIndicio(indicioInfo) {
            if (it?.idindicio != null) {
                Toast.makeText(this@IndicioActivity,"OK", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@IndicioActivity,"Error", Toast.LENGTH_LONG).show()
            }
        }
    }




    fun addIndicio(indicioData: IndicioDataCollectionItem, onResult: (IndicioDataCollectionItem?) -> Unit){
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val retrofit = RestEngine.buildService().create(IndicioService::class.java)
        var result: Call<IndicioDataCollectionItem> = retrofit.addIndicio(indicioData)
        result.enqueue(object : Callback<IndicioDataCollectionItem> {
            override fun onFailure(call: Call<IndicioDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<IndicioDataCollectionItem>,
                                    response: Response<IndicioDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val addedPerson = response.body()!!
                    onResult(addedPerson)
                }
                *//*else if (response.code() == 401){
                    Toast.makeText(this@MainActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }*//*
                else if (response.code() == 500){
                    //val gson = Gson()
                    //val type = object : TypeToken<RestApiError>() {}.type
                    //var errorResponse1: RestApiError? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    val errorResponse = Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)


                    Toast.makeText(this@IndicioActivity,errorResponse.errorDetails, Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@IndicioActivity,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }

        }
        )

    }*/
    fun borrarIndicio(view: View){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del Indicio")
        builder.setMessage("Por favor ingrese el ID del Indicio a buscar, en caso de querer verificar nuevamente presione el botón \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_indicio, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServiceDeleteIndicio(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()

    }

    private fun callServiceDeleteIndicio(idIndicio: Long) {
        val indicioService: IndicioService = RestEngine.buildService().create(IndicioService::class.java)
        var result: Call<ResponseBody> = indicioService.deleteIndicio(idIndicio)

        result.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@IndicioActivity,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@IndicioActivity,"DELETE", Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@IndicioActivity,"Sesion expirada", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@IndicioActivity,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun estaVacio():Boolean{
        if(txtDescripcion.text.toString().isEmpty()) {
            txtDescripcion.error = "Debe rellenar la Descripcion"
            return true
        }

        if(spnIdCaso.selectedItem.equals("Seleccione el Id Caso")) {
            txtDescripcion.error ="Debe rellenar el Id Caso"
            return true
        }


        return false
    }

    private fun noLoSuficienteLargo():Boolean{
        if(txtDescripcion.text.toString().length < 5) {
            txtDescripcion.error ="La Fecha no puede ser menor a 4 caracteres"
            return true
        }

        return false
    }

}