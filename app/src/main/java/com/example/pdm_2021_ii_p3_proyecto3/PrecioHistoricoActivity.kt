package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.PrecioHistoricoDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.RestApiError
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.ServicioDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.SucursalDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.Service.PrecioHistoricoService
import com.example.pdm_2021_ii_p3_proyecto3.Service.RestEngine
import com.example.pdm_2021_ii_p3_proyecto3.Service.ServicioService
import com.example.pdm_2021_ii_p3_proyecto3.Service.SucursalService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_abogado.*
import kotlinx.android.synthetic.main.activity_precio_historico.*
import kotlinx.android.synthetic.main.activity_servicio.*
import kotlinx.android.synthetic.main.activity_sucursal.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class PrecioHistoricoActivity : AppCompatActivity() {
    var array = ArrayList<String>()
    var servicioElegir = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_precio_historico)
        callServiceGetPrecioHistorico()
        callServiceGetServicio()
        btnGuardarPrecio.setOnClickListener { v -> callServicePostPrecioHistorico() }
        btnActualizarPrecio.setOnClickListener {v -> actualizarPrecioHistorico(v) }
        btnEliminarPrecio.setOnClickListener { v -> borrarPrecioHistorico(v) }
    }
    private fun callServiceGetServicio() {
        val personService: ServicioService = RestEngine.buildService().create(ServicioService::class.java)
        var result: Call<List<ServicioDataCollectionItem>> = personService.listServicio()

        result.enqueue(object :  Callback<List<ServicioDataCollectionItem>> {
            override fun onFailure(call: Call<List<ServicioDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@PrecioHistoricoActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<ServicioDataCollectionItem>>,
                response: Response<List<ServicioDataCollectionItem>>
            ) {
                servicioElegir.add("Seleccione el Servicio")
                for(i in 0..(response.body()!!.size-1)){
                    servicioElegir.add(response.body()!!.get(i).idservicio.toString())// + "|" + response.body()!!.get(i).nombreservicio)
                    val arrayAdapter: ArrayAdapter<*>
                    arrayAdapter = ArrayAdapter(this@PrecioHistoricoActivity,android.R.layout.simple_list_item_1,servicioElegir)
                    spnPrecioServicio.adapter = arrayAdapter
                }



            }
        })
    }
    private fun callServiceGetPrecioHistorico() {
        val personService: PrecioHistoricoService = RestEngine.buildService().create(PrecioHistoricoService::class.java)
        var result: Call<List<PrecioHistoricoDataCollectionItem>> = personService.listPrecioHistorico()

        result.enqueue(object : Callback<List<PrecioHistoricoDataCollectionItem>> {
            override fun onFailure(call: Call<List<PrecioHistoricoDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@PrecioHistoricoActivity,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<PrecioHistoricoDataCollectionItem>>,
                response: Response<List<PrecioHistoricoDataCollectionItem>>
            ) {
                array.add("Todas los Registros de Precio")
                array.add("ID|Fecha Inicial|Fecha Final|Precio|Servicio")
                for(i in 0..(response.body()!!.size-1)){
                    array.add(response.body()!!.get(i).idpreciohistorico.toString() + "|" + response.body()!!.get(i).fechainicialpreciohistorico + "|" + response.body()!!.get(i).fechafinalpreciohistorico + "¬"
                            +response.body()!!.get(i).precio.toString()+ "|" + response.body()!!.get(i).idservicio)

                    val arrayAdapter: ArrayAdapter<*>
                    arrayAdapter = ArrayAdapter(this@PrecioHistoricoActivity,android.R.layout.simple_list_item_1,array)
                    lvwPrecio.adapter = arrayAdapter
                }

            }
        })
    }


    fun actualizarPrecioHistorico(view: View) {
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del Precio Histórico")
        builder.setMessage("Por favor ingrese el ID del Precio Histórico a buscar, en caso de querer verificar nuevamente presione el botón \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_preciohistorico, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServicePutPrecioHistorico(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()

    }

    private fun callServicePutPrecioHistorico(idPrecioHistorico:Long) {
        var id:String
        var nombre: String
        var seleccion=spnPrecioServicio.selectedItem.toString()

        for (servicio in servicioElegir) {
            val lista = servicio.toString().split("|", "=")
            id = lista[1]
            nombre = lista[2]
            if (seleccion.contains(id)){

                seleccion = id
            }
        }

        val preciohistoricoInfo = PrecioHistoricoDataCollectionItem(
            idpreciohistorico = idPrecioHistorico,
            fechainicialpreciohistorico = txtFechaInicial.text.toString(),
            fechafinalpreciohistorico = txtFechaFinal.text.toString(),
            precio = txtPrecio.text.toString().toDouble(),
            idservicio = seleccion.toLong()



                )

        val retrofit = RestEngine.buildService().create(PrecioHistoricoService::class.java)
        var result: Call<PrecioHistoricoDataCollectionItem> = retrofit.updatePrecioHistorico(preciohistoricoInfo)
        result.enqueue(object : Callback<PrecioHistoricoDataCollectionItem> {
            override fun onFailure(call: Call<PrecioHistoricoDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@PrecioHistoricoActivity,"Error", Toast.LENGTH_LONG).show()
            }
            override fun onResponse(call: Call<PrecioHistoricoDataCollectionItem>,
                                    response: Response<PrecioHistoricoDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val updatedPerson = response.body()!!
                    Toast.makeText(this@PrecioHistoricoActivity,"OK",
                        Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@PrecioHistoricoActivity,"Sesion expirada", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@PrecioHistoricoActivity,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun callServicePostPrecioHistorico(){
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val  preciohistoricoInfo = PrecioHistoricoDataCollectionItem(
            idpreciohistorico = 0, // Este se llena solo para no dejar vacio. Es incremental
            fechainicialpreciohistorico = txtFechaInicial.text.toString(),
            fechafinalpreciohistorico = txtFechaFinal.text.toString(),
            precio = txtPrecio.text.toString().toDouble(),
            idservicio = spnPrecioServicio.selectedItem.toString().toLong()
            )
        addPrecioHistorico(preciohistoricoInfo) {
            if (it?.idpreciohistorico != null) {
                Toast.makeText(this@PrecioHistoricoActivity,"OK", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@PrecioHistoricoActivity,"Error", Toast.LENGTH_LONG).show()
            }
        }
    }




    fun addPrecioHistorico(preciohistoricoData: PrecioHistoricoDataCollectionItem, onResult: (PrecioHistoricoDataCollectionItem?) -> Unit){
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val retrofit = RestEngine.buildService().create(PrecioHistoricoService::class.java)
        var result: Call<PrecioHistoricoDataCollectionItem> = retrofit.addPrecioHistorico(preciohistoricoData)
        result.enqueue(object : Callback<PrecioHistoricoDataCollectionItem> {
            override fun onFailure(call: Call<PrecioHistoricoDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<PrecioHistoricoDataCollectionItem>,
                                    response: Response<PrecioHistoricoDataCollectionItem>
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


                    Toast.makeText(this@PrecioHistoricoActivity,errorResponse.errorDetails, Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@PrecioHistoricoActivity,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }

        }
        )

    }
    fun borrarPrecioHistorico(view: View){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del Precio Histórico")
        builder.setMessage("Por favor ingrese el ID delPrecio Histórico a buscar, en caso de querer verificar nuevamente presione el botón \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_preciohistorico, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServiceDeletePrecioHistorico(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()

    }

    private fun callServiceDeletePrecioHistorico(idPrecioHistorico: Long) {
        val preciohistoricoService: PrecioHistoricoService = RestEngine.buildService().create(PrecioHistoricoService::class.java)
        var result: Call<ResponseBody> = preciohistoricoService.deletePrecioHistorico(idPrecioHistorico)

        result.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@PrecioHistoricoActivity,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@PrecioHistoricoActivity,"DELETE", Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@PrecioHistoricoActivity,"Sesion expirada", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@PrecioHistoricoActivity,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun estaVacio():Boolean{
        if(txtFechaInicial.text.toString().isEmpty()) {
            txtFechaInicial.error ="Debe rellenar la fecha inicial"
            return true
        }else if(txtFechaFinal.text.toString().isEmpty()){
            txtFechaFinal.error = "Debe rellenar la fecha final"
            return true
        }
        if(txtPrecio.text.toString().isEmpty()) {
            txtPrecio.error ="Debe rellenar el precio"
            return true
        }
        if(spnPrecioServicio.selectedItem.equals("Seleccione el Servicio")) {
            txtPrecio.error ="Debe rellenar el Servicio"
            return true
        }


        return false
    }

    private fun noLoSuficienteLargo():Boolean{
        if(txtFechaInicial.text.toString().length < 9) {
            txtFechaInicial.error ="La Fecha no puede ser menor a 9 caracteres"
            return true
        }else if(txtFechaFinal.text.toString().length < 9) {
            txtFechaFinal.error ="La Fecha no puede ser menor a 9 caracteres"
            return true
        }

        if(txtPrecio.text.toString().toDouble()<=0) {
            txtPrecio.error ="El precio no puede ser menor a 0"
            return true
        }

        return false
    }

}