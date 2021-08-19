package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.RestApiError
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.ServicioDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.SucursalDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.Service.RestEngine
import com.example.pdm_2021_ii_p3_proyecto3.Service.ServicioService
import com.example.pdm_2021_ii_p3_proyecto3.Service.SucursalService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_abogado.*
import kotlinx.android.synthetic.main.activity_servicio.*
import kotlinx.android.synthetic.main.activity_sucursal.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ServicioActivity : AppCompatActivity() {
   val idservicio=0
    var array = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servicio)
        callServiceGetServicio()
        btnGuardarServicio.setOnClickListener { v-> callServicePostServicio() }
        btnActualizarServicio.setOnClickListener {v -> actualizarServicio(v)  }
        btnEliminarServicio.setOnClickListener { v-> borrarServicio(v) }
    }
    private fun callServiceGetServicio() {
        val personService: ServicioService = RestEngine.buildService().create(ServicioService::class.java)
        var result: Call<List<ServicioDataCollectionItem>> = personService.listServicio()

        result.enqueue(object :  Callback<List<ServicioDataCollectionItem>> {
            override fun onFailure(call: Call<List<ServicioDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@ServicioActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<ServicioDataCollectionItem>>,
                response: Response<List<ServicioDataCollectionItem>>
            ) {
                array.add("Todas los Servicios")
                array.add("ID|Servicio|Descripción")
                for(i in 0..(response.body()!!.size-1)){
                    array.add(response.body()!!.get(i).idservicio.toString() + "|" + response.body()!!.get(i).nombreservicio + "|" + response.body()!!.get(i).descripcionservicio)

                    val arrayAdapter: ArrayAdapter<*>
                    arrayAdapter = ArrayAdapter(this@ServicioActivity,android.R.layout.simple_list_item_1,array)
                    lvwServicio.adapter = arrayAdapter
                }

            }
        })
    }


    fun actualizarServicio(view: View) {
       if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del servicio")
        builder.setMessage("Por favor ingrese el ID del servicio a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_servicio, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServicePutServicio(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()

    }

    private fun callServicePutServicio(idServicio:Long) {

        val servicioInfo = ServicioDataCollectionItem(
            idservicio = idServicio,
            nombreservicio = txtNombreServicio.text.toString(),
            descripcionservicio = txtDescripcionServicio.text.toString()

        )

        val retrofit = RestEngine.buildService().create(ServicioService::class.java)
        var result: Call<ServicioDataCollectionItem> = retrofit.updateSercicio(servicioInfo)
        result.enqueue(object : Callback<ServicioDataCollectionItem> {
            override fun onFailure(call: Call<ServicioDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@ServicioActivity,"Error",Toast.LENGTH_LONG).show()
            }
            override fun onResponse(call: Call<ServicioDataCollectionItem>,
                                    response: Response<ServicioDataCollectionItem>) {
                if (response.isSuccessful) {
                    val updatedPerson = response.body()!!
                    Toast.makeText(this@ServicioActivity,"OK"+response.body()!!.nombreservicio,Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@ServicioActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@ServicioActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun callServicePostServicio() {

        val  servicioInfo = ServicioDataCollectionItem(
            idservicio = 0, // Este se llena solo para no dejar vacio. Es incremental
            nombreservicio = txtNombreServicio.text.toString(),
            descripcionservicio = txtDescripcionServicio.text.toString(),

        )
        addServicio(servicioInfo) {
            if (it?.idservicio != null) {
                Toast.makeText(this@ServicioActivity,"OK", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@ServicioActivity,"Error", Toast.LENGTH_LONG).show()
            }
        }
    }




    fun addServicio(servicioData: ServicioDataCollectionItem, onResult: (ServicioDataCollectionItem?) -> Unit){
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val retrofit = RestEngine.buildService().create(ServicioService::class.java)
        var result: Call<ServicioDataCollectionItem> = retrofit.addServicio(servicioData)
        result.enqueue(object : Callback<ServicioDataCollectionItem> {
            override fun onFailure(call: Call<ServicioDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<ServicioDataCollectionItem>,
                                    response: Response<ServicioDataCollectionItem>
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


                    Toast.makeText(this@ServicioActivity,errorResponse.errorDetails, Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@ServicioActivity,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }

        }
        )

    }
    fun borrarServicio(view: View){
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
                Toast.makeText(this@ServicioActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ServicioActivity,"DELETE",Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@ServicioActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@ServicioActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun estaVacio():Boolean{
        if(txtNombreServicio.text.toString().isEmpty()) {
            txtNombreServicio.error ="Debe rellenar el nombre "
            return true
        }else if(txtDescripcionServicio.text.toString().isEmpty()){
            txtDescripcionServicio.error = "Debe rellenar la descripción"
            return true
        }


        return false
    }

    private fun noLoSuficienteLargo():Boolean{
        if(txtNombreServicio.text.toString().length < 4) {
            txtNombreServicio.error ="El nombre no puede ser menor a 4 caracteres"
            return true
        }else if(txtDescripcionServicio.text.toString().length < 6){
            txtDescripcionServicio.error = "La descripción no puede ser menor a 6 caracteres"
            return true
        }

        return false
    }
}